/**
 * Created by Michael Avoyan on 3/11/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api

import android.content.Context
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.initialization.VCLInitializationDescriptor

interface VCL {
    fun initialize(
        context: Context,
        initializationDescriptor: VCLInitializationDescriptor,
        successHandler: () -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    val countries: VCLCountries?
    val credentialTypes: VCLCredentialTypes?
    val credentialTypeSchemas: VCLCredentialTypeSchemas?

    fun getPresentationRequest(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        remoteCryptoServicesToken: VCLToken? = null,
        successHandler: (VCLPresentationRequest) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun submitPresentation(
        presentationSubmission: VCLPresentationSubmission,
        didJwk: VCLDidJwk? = null,
        remoteCryptoServicesToken: VCLToken? = null,
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
        remoteCryptoServicesToken: VCLToken? = null,
        successHandler: (VCLCredentialManifest) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun generateOffers(
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        didJwk: VCLDidJwk? = null,
        remoteCryptoServicesToken: VCLToken? = null,
        successHandler: (VCLOffers) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun checkForOffers(
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        exchangeToken: VCLToken,
        successHandler: (VCLOffers) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun finalizeOffers(
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        didJwk: VCLDidJwk? = null,
        exchangeToken: VCLToken,
        remoteCryptoServicesToken: VCLToken? = null,
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
        publicJwk: VCLPublicJwk,
        remoteCryptoServicesToken: VCLToken? = null,
        successHandler: (Boolean) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun generateSignedJwt(
        jwtDescriptor: VCLJwtDescriptor,
        remoteCryptoServicesToken: VCLToken? = null,
        successHandler: (VCLJwt) -> Unit,
        errorHandler: (VCLError) -> Unit
    )

    fun generateDidJwk(
        remoteCryptoServicesToken: VCLToken? = null,
        successHandler: (VCLDidJwk) -> Unit,
        errorHandler: (VCLError) -> Unit
    )
}