/**
 * Created by Michael Avoyan on 3/11/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.utils.VCLLog

interface VCL {
    fun initialize(
        initializationDescriptor: VCLInitializationDescriptor,
        successHandler: () -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    val countries: VCLCountries?
    val credentialTypes: VCLCredentialTypes?
    val credentialTypeSchemas: VCLCredentialTypeSchemas?

    fun getPresentationRequest(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        successHandler: (VCLPresentationRequest) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun submitPresentation(
        presentationSubmission: VCLPresentationSubmission,
        successHandler: (VCLSubmissionResult) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun getExchangeProgress(
        exchangeDescriptor: VCLExchangeDescriptor,
        successHandler: (VCLExchange) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun searchForOrganizations(
        organizationsSearchDescriptor: VCLOrganizationsSearchDescriptor,
        successHandler: (VCLOrganizations) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun getCredentialManifest(
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        successHandler: (VCLCredentialManifest) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun generateOffers(
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        successHandler: (VCLOffers) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun checkForOffers(
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        token: VCLToken,
        successHandler: (VCLOffers) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun finalizeOffers(
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        token: VCLToken,
        successHandler: (VCLJwtVerifiableCredentials) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun getCredentialTypesUIFormSchema(
        credentialTypesUIFormSchemaDescriptor: VCLCredentialTypesUIFormSchemaDescriptor,
        successHandler: (VCLCredentialTypesUIFormSchema) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun getVerifiedProfile(
        verifiedProfileDescriptor: VCLVerifiedProfileDescriptor,
        successHandler: (VCLVerifiedProfile) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun verifyJwt(
        jwt: VCLJwt,
        jwkPublic: VCLJwkPublic,
        successHandler: (Boolean) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun generateSignedJwt(
        jwtDescriptor: VCLJwtDescriptor,
        successHandler: (VCLJwt) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun generateDidJwk(
        successHandler: (VCLDidJwk) -> Unit,
        errorHandler: (VCLError) -> Unit
    )
}

fun VCL.printVersion() {
    VCLLog.d("VCL", "Version: ${GlobalConfig.VersionName}")
    VCLLog.d("VCL", "Build: ${GlobalConfig.VersionCode}")
}