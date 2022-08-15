package io.velocitycareerlabs.api.entities

/**
 * Created by Michael Avoyan on 12/05/2021.
 */
class VCLIdentificationSubmission(
    credentialManifest: VCLCredentialManifest,
    verifiableCredentials: List<VCLVerifiableCredential>
) : VCLSubmission(
    submitUri = credentialManifest.submitPresentationUri,
    iss = credentialManifest.iss,
    exchangeId = credentialManifest.exchangeId,
    presentationDefinitionId = credentialManifest.presentationDefinitionId,
    verifiableCredentials = verifiableCredentials,
    vendorOriginContext = credentialManifest.vendorOriginContext
)