/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.utils

import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.error.VCLStatusCode

internal object ErrorTaxonomy {
    const val PhaseLinkValidation = "link_validation"
    const val PhaseClientRequestFetch = "client_request_fetch"
    const val PhaseDidResolution = "did_resolution"
    const val PhaseRegistrationCheck = "registration_check"
    const val PhaseRequestValidation = "request_validation"
    const val PhaseRequestAuthorization = "request_authorization"

    const val RequestKindIssuing = "issuing_request"
    const val RequestKindPresentation = "presentation_request"

    fun invalidLink(
        message: String?,
        sourceErrorCode: String? = null,
        requestDid: String? = null,
        requestUri: String? = null,
        requestKind: String? = null,
        cause: Throwable? = null,
    ) = VCLError(
        errorCode = VCLErrorCode.InvalidLink.value,
        message = message,
        sourceErrorCode = sourceErrorCode,
        validationPhase = PhaseLinkValidation,
        requestDid = requestDid,
        requestUri = requestUri,
        requestKind = requestKind,
        cause = cause,
    )

    fun toClientRequestFetchError(
        error: VCLError,
        requestUri: String?,
        requestKind: String,
    ): VCLError =
        error.toTaxonomyError(
            taxonomyCode = clientRequestFetchCode(error),
            context = TaxonomyContext(
                validationPhase = PhaseClientRequestFetch,
                requestUri = requestUri,
                requestKind = requestKind,
            ),
        )

    fun toDidResolutionError(error: VCLError, requestKind: String, requestDid: String?): VCLError =
        error.toTaxonomyError(
            taxonomyCode = if (error.isConnectivityFailure()) {
                VCLErrorCode.ConnectivityFailure
            } else {
                requestKind.didUnresolvableCode()
            },
            context = TaxonomyContext(
                validationPhase = PhaseDidResolution,
                requestDid = requestDid,
                requestKind = requestKind,
            ),
        )

    fun toRegistrationCheckError(error: VCLError, requestKind: String, requestDid: String?): VCLError =
        error.toTaxonomyError(
            taxonomyCode = registrationCheckCode(error, requestKind),
            context = TaxonomyContext(
                validationPhase = PhaseRegistrationCheck,
                requestDid = requestDid,
                requestKind = requestKind,
            ),
        )

    fun toRequestAuthorizationError(error: VCLError, requestKind: String, requestDid: String?): VCLError =
        error.toTaxonomyError(
            taxonomyCode = requestKind.requestUnauthorizedCode(),
            context = TaxonomyContext(
                validationPhase = PhaseRequestAuthorization,
                requestDid = requestDid,
                requestKind = requestKind,
            ),
        )

    fun toRequestValidationError(
        error: VCLError,
        requestKind: String,
        requestDid: String?,
        requestUri: String? = null,
    ): VCLError =
        error.toTaxonomyError(
            taxonomyCode = if (error.isConnectivityFailure()) {
                VCLErrorCode.ConnectivityFailure
            } else {
                requestKind.requestInvalidCode()
            },
            context = TaxonomyContext(
                validationPhase = PhaseRequestValidation,
                requestDid = requestDid,
                requestUri = requestUri,
                requestKind = requestKind,
            ),
        )

    fun VCLError.isConnectivityFailure(): Boolean =
        errorCode == VCLErrorCode.ConnectivityFailure.value ||
            statusCode == VCLStatusCode.NetworkError.value

    private val taxonomyErrorCodes = setOf(
        VCLErrorCode.InvalidLink.value,
        VCLErrorCode.ConnectivityFailure.value,
        VCLErrorCode.ClientRequestUnauthorized.value,
        VCLErrorCode.ClientRequestRejected.value,
        VCLErrorCode.IssuerDidUnresolvable.value,
        VCLErrorCode.VerifierDidUnresolvable.value,
        VCLErrorCode.RegistrationCheckInconclusive.value,
        VCLErrorCode.IssuerNotRegistered.value,
        VCLErrorCode.VerifierNotRegistered.value,
        VCLErrorCode.IssuerRequestInvalid.value,
        VCLErrorCode.VerifierRequestInvalid.value,
        VCLErrorCode.IssuerRequestUnauthorized.value,
        VCLErrorCode.VerifierRequestUnauthorized.value,
    )

    fun VCLError.isTaxonomyError(): Boolean =
        errorCode in taxonomyErrorCodes

    private fun VCLError.toTaxonomyError(
        taxonomyCode: VCLErrorCode,
        context: TaxonomyContext,
    ): VCLError =
        if (isTaxonomyError()) {
            copy(
                validationPhase = validationPhase ?: context.validationPhase,
                requestDid = requestDid ?: context.requestDid,
                requestUri = requestUri ?: context.requestUri,
                requestKind = requestKind ?: context.requestKind,
            )
        } else {
            copy(
                errorCode = taxonomyCode.value,
                sourceErrorCode = sourceErrorCode ?: sourceErrorCodeFor(taxonomyCode),
                validationPhase = context.validationPhase,
                requestDid = context.requestDid ?: requestDid,
                requestUri = context.requestUri ?: requestUri,
                requestKind = context.requestKind ?: requestKind,
            )
        }

    private fun VCLError.sourceErrorCodeFor(taxonomyCode: VCLErrorCode): String? =
        if (taxonomyCode == VCLErrorCode.ConnectivityFailure && errorCode == VCLErrorCode.SdkError.value) {
            null
        } else {
            errorCode.takeUnless { it == taxonomyCode.value }
        }

    private fun clientRequestFetchCode(error: VCLError): VCLErrorCode =
        when {
            error.isConnectivityFailure() -> VCLErrorCode.ConnectivityFailure
            error.statusCode == 401 || error.statusCode == 403 -> VCLErrorCode.ClientRequestUnauthorized
            else -> VCLErrorCode.ClientRequestRejected
        }

    private fun registrationCheckCode(error: VCLError, requestKind: String): VCLErrorCode =
        when {
            error.isConnectivityFailure() -> VCLErrorCode.ConnectivityFailure
            error.statusCode == 404 -> requestKind.notRegisteredCode()
            else -> VCLErrorCode.RegistrationCheckInconclusive
        }

    private data class TaxonomyContext(
        val validationPhase: String,
        val requestDid: String? = null,
        val requestUri: String? = null,
        val requestKind: String? = null,
    )

    private fun String.didUnresolvableCode(): VCLErrorCode =
        if (isPresentationRequest()) VCLErrorCode.VerifierDidUnresolvable else VCLErrorCode.IssuerDidUnresolvable

    private fun String.notRegisteredCode(): VCLErrorCode =
        if (isPresentationRequest()) VCLErrorCode.VerifierNotRegistered else VCLErrorCode.IssuerNotRegistered

    private fun String.requestInvalidCode(): VCLErrorCode =
        if (isPresentationRequest()) VCLErrorCode.VerifierRequestInvalid else VCLErrorCode.IssuerRequestInvalid

    private fun String.requestUnauthorizedCode(): VCLErrorCode =
        if (isPresentationRequest()) VCLErrorCode.VerifierRequestUnauthorized else VCLErrorCode.IssuerRequestUnauthorized

    private fun String.isPresentationRequest(): Boolean = this == RequestKindPresentation
}
