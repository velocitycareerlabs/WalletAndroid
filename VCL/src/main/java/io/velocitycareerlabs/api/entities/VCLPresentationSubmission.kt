/**
 * Created by Michael Avoyan on 4/11/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

class VCLPresentationSubmission(
    presentationRequest: VCLPresentationRequest,
    verifiableCredentials: List<VCLVerifiableCredential>
) : VCLSubmission(
    submitUri = presentationRequest.submitPresentationUri,
    exchangeId = presentationRequest.exchangeId,
    presentationDefinitionId = presentationRequest.presentationDefinitionId,
    verifiableCredentials = verifiableCredentials,
    pushDelegate = presentationRequest.pushDelegate,
    vendorOriginContext = presentationRequest.vendorOriginContext,
    didJwk = presentationRequest.didJwk,
    remoteCryptoServicesToken = presentationRequest.remoteCryptoServicesToken
) {
    val progressUri = presentationRequest.progressUri
}