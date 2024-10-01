/**
 * Created by Michael Avoyan on 4/11/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import java.util.UUID

data class VCLPresentationSubmission(
    override val submitUri: String,
    override val exchangeId: String,
    override val presentationDefinitionId: String,
    override val verifiableCredentials: List<VCLVerifiableCredential>?,
    override val pushDelegate: VCLPushDelegate?,
    override val vendorOriginContext: String?,
    override val didJwk: VCLDidJwk,
    override val remoteCryptoServicesToken: VCLToken?,
    val progressUri: String
) : VCLSubmission {

    override val jti: String = UUID.randomUUID().toString()
    override val submissionId: String = UUID.randomUUID().toString()

    constructor(
        presentationRequest: VCLPresentationRequest,
        verifiableCredentials: List<VCLVerifiableCredential>
    ) : this(
        submitUri = presentationRequest.submitPresentationUri,
        exchangeId = presentationRequest.exchangeId,
        presentationDefinitionId = presentationRequest.presentationDefinitionId,
        verifiableCredentials = verifiableCredentials,
        pushDelegate = presentationRequest.pushDelegate,
        vendorOriginContext = presentationRequest.vendorOriginContext,
        didJwk = presentationRequest.didJwk,
        remoteCryptoServicesToken = presentationRequest.remoteCryptoServicesToken,
        progressUri = presentationRequest.progressUri
    )
}
