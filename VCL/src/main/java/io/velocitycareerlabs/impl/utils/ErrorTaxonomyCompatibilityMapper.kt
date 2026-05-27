/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.utils

import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode

internal class ErrorTaxonomyCompatibilityMapper {
    fun map(
        error: VCLError,
        requestKind: String,
    ): VCLError =
        when (error.errorCode) {
            VCLErrorCode.InvalidLink.value -> mapInvalidLink(error, requestKind)
            VCLErrorCode.ConnectivityFailure.value -> legacyCopy(error, errorCode = VCLErrorCode.SdkError.value)
            else -> if (ErrorTaxonomy.run { error.isTaxonomyError() }) mapTaxonomyError(error) else mapNetworkStatus(error)
        }

    private fun mapInvalidLink(
        error: VCLError,
        requestKind: String,
    ): VCLError {
        val endpointNullMessage = requestKind.endpointNullMessage()
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
            VelocityDeepLinkValidator.SourceUnsupportedVelocityLink ->
                legacyCopy(
                    error,
                    errorCode = VCLErrorCode.SdkError.value,
                    message = endpointNullMessage,
                )
            else ->
                legacyCopy(
                    error,
                    errorCode = VCLErrorCode.SdkError.value,
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
        if (sourceErrorCode == networkStatusError.errorCode ||
            sourceErrorCode == ProfileServiceTypeVerifier.SourceWrongServiceType ||
            sourceErrorCode == null
        ) {
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

    private fun mapNetworkStatus(error: VCLError): VCLError = error

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

    private fun String.endpointNullMessage(): String =
        if (this == ErrorTaxonomy.RequestKindPresentation) {
            "presentationRequestDescriptor.endpoint = null"
        } else {
            "credentialManifestDescriptor.endpoint = null"
        }

    private companion object {
        const val LegacyMissingDidMessage = "did was not found in Velocity link"
    }
}
