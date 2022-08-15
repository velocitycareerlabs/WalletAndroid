package io.velocitycareerlabs.api.entities

/**
 * Created by Michael Avoyan on 4/11/21.
 */
class VCLPresentationSubmission(
    presentationRequest: VCLPresentationRequest,
    verifiableCredentials: List<VCLVerifiableCredential>
) : VCLSubmission(
    submitUri = presentationRequest.submitPresentationUri,
    iss = presentationRequest.iss,
    exchangeId = presentationRequest.exchangeId,
    presentationDefinitionId = presentationRequest.presentationDefinitionId,
    verifiableCredentials = verifiableCredentials,
    vendorOriginContext = presentationRequest.vendorOriginContext
) {
    val progressUri = presentationRequest.progressUri
}