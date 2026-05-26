/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.utils

import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject

internal class ErrorTaxonomyCompatibilityMapper {
    fun map(
        error: VCLError,
        requestKind: String,
        endpointNullMessage: String,
    ): VCLError =
        when (error.errorCode) {
            VCLErrorCode.InvalidLink.value -> mapInvalidLink(error, requestKind, endpointNullMessage)
            VCLErrorCode.ConnectivityFailure.value -> legacyCopy(error, errorCode = VCLErrorCode.SdkError.value)
            else -> if (ErrorTaxonomy.run { error.isTaxonomyError() }) mapTaxonomyError(error) else mapNetworkStatus(error)
        }

    private fun mapInvalidLink(
        error: VCLError,
        requestKind: String,
        endpointNullMessage: String,
    ): VCLError {
        return when (error.sourceErrorCode) {
            VelocityDeepLinkValidator.SourceInvalidOrMissingDid ->
                if (error.requestUri != null) {
                    legacyCopy(error, errorCode = requestKind.mismatchErrorCode())
                } else {
                    legacyCopy(
                        error,
                        errorCode = VCLErrorCode.SdkError.value,
                        message = LegacyMissingDidMessage,
                    )
                }
            VelocityDeepLinkValidator.SourceInvalidOrMissingRequestUri,
            VelocityDeepLinkValidator.SourceInvalidOrMissingRequestEndpoint ->
                mapInvalidRequestUri(error, endpointNullMessage)
            VelocityDeepLinkValidator.SourceUnparseablePayload ->
                legacyCopy(
                    error,
                    errorCode = VCLErrorCode.SdkError.value,
                    message = LegacyMissingDidMessage,
                )
            else ->
                legacyCopy(
                    error,
                    errorCode = VCLErrorCode.SdkError.value,
                    message = endpointNullMessage,
                )
        }
    }

    private fun mapInvalidRequestUri(error: VCLError, endpointNullMessage: String): VCLError {
        error.requestUri?.let { requestUri ->
            if (requestUri.startsWith("ftp://")) {
                return legacyCopy(
                    error,
                    errorCode = VCLErrorCode.SdkError.value,
                    message = "java.net.MalformedURLException: unknown protocol: ftp",
                )
            }
            if (!requestUri.contains("://")) {
                return legacyCopy(
                    error,
                    errorCode = VCLErrorCode.SdkError.value,
                    message = "java.net.MalformedURLException: no protocol: $requestUri",
                )
            }
        }
        return legacyCopy(
            error,
            errorCode = VCLErrorCode.SdkError.value,
            message = endpointNullMessage,
        )
    }

    private fun mapTaxonomyError(error: VCLError): VCLError {
        val networkStatusError = mapNetworkStatus(error)
        val sourceErrorCode = networkStatusError.sourceErrorCode
        if (sourceErrorCode == networkStatusError.errorCode || sourceErrorCode == null) {
            return legacyCopy(
                networkStatusError,
                errorCode = VCLErrorCode.SdkError.value,
            )
        }
        return legacyCopy(
            networkStatusError,
            errorCode = sourceErrorCode,
        )
    }

    private fun mapNetworkStatus(error: VCLError): VCLError {
        val payloadStatusCode = error.payload?.toJsonObject()?.optNullableInt(VCLError.KeyStatusCode)
        return payloadStatusCode?.let { error.copy(statusCode = it) } ?: error
    }

    private fun legacyCopy(
        error: VCLError,
        errorCode: String,
        message: String? = error.message,
    ): VCLError =
        error.copy(
            errorCode = errorCode,
            message = message,
            sourceErrorCode = null,
            validationPhase = null,
            requestDid = null,
            requestUri = null,
            requestKind = null,
        )

    private fun String.mismatchErrorCode(): String =
        if (this == ErrorTaxonomy.RequestKindPresentation) {
            VCLErrorCode.MismatchedPresentationRequestInspectorDid.value
        } else {
            VCLErrorCode.MismatchedRequestIssuerDid.value
        }

    private fun JSONObject?.optNullableInt(key: String): Int? =
        takeIf { it?.has(key) == true && !it.isNull(key) }?.optInt(key)

    private companion object {
        const val LegacyMissingDidMessage = "did was not found in Velocity link"
    }
}
