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

    fun classifyClientRequestFetch(
        error: VCLError,
        requestUri: String?,
        requestKind: String,
    ): VCLError =
        if (error.isTaxonomyError()) {
            error.withMissingTaxonomyContext(
                requestUri = requestUri,
                requestKind = requestKind,
                validationPhase = PhaseClientRequestFetch,
            )
        } else when {
            error.isConnectivityFailure() -> error.withTaxonomy(
                VCLErrorCode.ConnectivityFailure,
                PhaseClientRequestFetch,
                requestUri = requestUri,
                requestKind = requestKind,
            )
            error.transportStatusCode == 401 || error.transportStatusCode == 403 -> error.withTaxonomy(
                VCLErrorCode.ClientRequestUnauthorized,
                PhaseClientRequestFetch,
                requestUri = requestUri,
                requestKind = requestKind,
            )
            else -> error.withTaxonomy(
                VCLErrorCode.ClientRequestRejected,
                PhaseClientRequestFetch,
                requestUri = requestUri,
                requestKind = requestKind,
            )
        }

    fun classifyDidResolution(error: VCLError, requestKind: String, requestDid: String?): VCLError =
        if (error.isTaxonomyError()) {
            error.withMissingTaxonomyContext(
                requestDid = requestDid,
                requestKind = requestKind,
                validationPhase = PhaseDidResolution,
            )
        } else if (error.isConnectivityFailure()) {
            error.withTaxonomy(
                VCLErrorCode.ConnectivityFailure,
                PhaseDidResolution,
                requestDid = requestDid,
                requestKind = requestKind,
            )
        } else {
            error.withTaxonomy(
                requestKind.didUnresolvableCode(),
                PhaseDidResolution,
                requestDid = requestDid,
                requestKind = requestKind,
            )
        }

    fun classifyRegistration(error: VCLError, requestKind: String, requestDid: String?): VCLError =
        if (error.isTaxonomyError()) {
            error.withMissingTaxonomyContext(
                requestDid = requestDid,
                requestKind = requestKind,
                validationPhase = PhaseRegistrationCheck,
            )
        } else if (error.isConnectivityFailure()) {
            error.withTaxonomy(
                VCLErrorCode.ConnectivityFailure,
                PhaseRegistrationCheck,
                requestDid = requestDid,
                requestKind = requestKind,
            )
        } else {
            error.withTaxonomy(
                requestKind.notRegisteredCode(),
                PhaseRegistrationCheck,
                requestDid = requestDid,
                requestKind = requestKind,
            )
        }

    fun classifyServiceAuthorization(error: VCLError, requestKind: String, requestDid: String?): VCLError =
        if (error.isTaxonomyError()) error.withMissingTaxonomyContext(
            requestDid = requestDid,
            requestKind = requestKind,
            validationPhase = PhaseRequestAuthorization,
        )
        else error.withTaxonomy(
            requestKind.requestUnauthorizedCode(),
            PhaseRequestAuthorization,
            requestDid = requestDid,
            requestKind = requestKind,
        )

    fun classifyRequestValidation(error: VCLError, requestKind: String, requestDid: String?): VCLError =
        if (error.isTaxonomyError()) {
            error.withMissingTaxonomyContext(
                requestDid = requestDid,
                requestKind = requestKind,
                validationPhase = PhaseRequestValidation,
            )
        } else if (error.isConnectivityFailure()) {
            error.withTaxonomy(
                VCLErrorCode.ConnectivityFailure,
                PhaseRequestValidation,
                requestDid = requestDid,
                requestKind = requestKind,
            )
        } else {
            error.withTaxonomy(
                requestKind.requestInvalidCode(),
                PhaseRequestValidation,
                requestDid = requestDid,
                requestKind = requestKind,
            )
        }

    fun classifyRequestAuthorization(error: VCLError, requestDid: String? = null): VCLError =
        if (error.isTaxonomyError()) error.withMissingTaxonomyContext(
            requestDid = requestDid,
            requestKind = RequestKindIssuing,
            validationPhase = PhaseRequestAuthorization,
        )
        else error.withTaxonomy(
            VCLErrorCode.IssuerRequestUnauthorized,
            PhaseRequestAuthorization,
            requestDid = requestDid,
            requestKind = RequestKindIssuing,
        )

    fun VCLError.isConnectivityFailure(): Boolean =
        errorCode == VCLErrorCode.ConnectivityFailure.value ||
            statusCode == VCLStatusCode.NetworkError.value

    private val VCLError.transportStatusCode: Int?
        get() = httpStatusCode ?: statusCode

    fun VCLError.isTaxonomyError(): Boolean =
        VCLErrorCode.entries.any { it.value == errorCode && it.isTaxonomyCode }

    private val VCLErrorCode.isTaxonomyCode: Boolean
        get() = when (this) {
            VCLErrorCode.InvalidLink,
            VCLErrorCode.ConnectivityFailure,
            VCLErrorCode.ClientRequestUnauthorized,
            VCLErrorCode.ClientRequestRejected,
            VCLErrorCode.IssuerDidUnresolvable,
            VCLErrorCode.VerifierDidUnresolvable,
            VCLErrorCode.IssuerNotRegistered,
            VCLErrorCode.VerifierNotRegistered,
            VCLErrorCode.IssuerRequestInvalid,
            VCLErrorCode.VerifierRequestInvalid,
            VCLErrorCode.IssuerRequestUnauthorized,
            VCLErrorCode.VerifierRequestUnauthorized -> true
            else -> false
        }

    fun VCLError.withMissingTaxonomyContext(
        requestDid: String? = null,
        requestUri: String? = null,
        requestKind: String? = null,
        validationPhase: String? = null,
    ): VCLError =
        copy(
            validationPhase = this.validationPhase ?: validationPhase,
            requestDid = this.requestDid ?: requestDid,
            requestUri = this.requestUri ?: requestUri,
            requestKind = this.requestKind ?: requestKind,
        )

    fun VCLError.withTaxonomy(
        taxonomyCode: VCLErrorCode,
        validationPhase: String,
        requestDid: String? = this.requestDid,
        requestUri: String? = this.requestUri,
        requestKind: String? = this.requestKind,
    ): VCLError =
        if (isTaxonomyError()) {
            withMissingTaxonomyContext(
                requestDid = requestDid,
                requestUri = requestUri,
                requestKind = requestKind,
                validationPhase = validationPhase,
            )
        } else {
            copy(
                errorCode = taxonomyCode.value,
                sourceErrorCode = sourceErrorCode ?: errorCode.takeUnless { it == taxonomyCode.value },
                validationPhase = validationPhase,
                requestDid = requestDid,
                requestUri = requestUri,
                requestKind = requestKind,
            )
        }

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
