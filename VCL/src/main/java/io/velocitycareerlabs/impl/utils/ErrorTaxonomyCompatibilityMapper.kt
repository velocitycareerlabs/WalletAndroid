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
        if (error.message?.contains("Invalid or missing DID") == true && error.requestUri != null) {
            return legacyCopy(error, errorCode = requestKind.mismatchErrorCode())
        }
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
        if (error.message?.contains("Payload is not a parseable URL") == true ||
            error.message?.contains("Invalid or missing DID") == true
        ) {
            return legacyCopy(
                error,
                errorCode = VCLErrorCode.SdkError.value,
                message = "did was not found in Velocity link",
            )
        }
        return legacyCopy(
            error,
            errorCode = VCLErrorCode.SdkError.value,
            message = endpointNullMessage,
        )
    }

    private fun mapTaxonomyError(error: VCLError): VCLError =
        legacyCopy(
            mapNetworkStatus(error),
            errorCode = error.sourceErrorCode ?: VCLErrorCode.SdkError.value,
        )

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
}
