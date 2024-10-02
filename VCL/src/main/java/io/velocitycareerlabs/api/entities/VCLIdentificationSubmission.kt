/**
 * Created by Michael Avoyan on 12/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import java.util.UUID

data class VCLIdentificationSubmission(
    override val submitUri: String,
    override val exchangeId: String,
    override val presentationDefinitionId: String,
    override val verifiableCredentials: List<VCLVerifiableCredential>?,
    override val pushDelegate: VCLPushDelegate?,
    override val vendorOriginContext: String?,
    override val didJwk: VCLDidJwk,
    override val remoteCryptoServicesToken: VCLToken?
) : VCLSubmission {

    override val jti: String = UUID.randomUUID().toString()
    override val submissionId: String = UUID.randomUUID().toString()

    constructor(
        credentialManifest: VCLCredentialManifest,
        verifiableCredentials: List<VCLVerifiableCredential>? = null
    ) : this(
        submitUri = credentialManifest.submitPresentationUri,
        exchangeId = credentialManifest.exchangeId,
        presentationDefinitionId = credentialManifest.presentationDefinitionId,
        verifiableCredentials = verifiableCredentials,
        vendorOriginContext = credentialManifest.vendorOriginContext,
        didJwk = credentialManifest.didJwk,
        remoteCryptoServicesToken = credentialManifest.remoteCryptoServicesToken,
        pushDelegate = null
    )
}
