package io.velocitycareerlabs.verifiers

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.verifiers.PresentationRequestByDeepLinkVerifierImpl
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestMocks
import org.junit.Test

class PresentationRequestByDeepLinkVerifierTest {
    private val subject = PresentationRequestByDeepLinkVerifierImpl()
    private val presentationRequest = PresentationRequestMocks.PresentationRequest

    private val correctDeepLink =
        VCLDeepLink("velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f16305692%2Fissue%2Fget-credential-manifest%3Fid%3D611b5836e93d08000af6f1bc%26credential_types%3DPastEmploymentPosition%26issuerDid%3Ddid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f16305692")
    private val wrongDeepLink = DeepLinkMocks.PresentationRequestDeepLinkDevNet

    @Test
    fun testVerifyCredentialManifestSuccess() {
        subject.verifyPresentationRequest(
            presentationRequest,
            correctDeepLink
        ) {
            it.handleResult({ isVerified ->
                assert(isVerified)
            }, { error ->
                assert(false) { "${error.toJsonObject()}" }
            })
        }
    }

    @Test
    fun testVerifyCredentialManifestError() {
        subject.verifyPresentationRequest(
            presentationRequest,
            wrongDeepLink
        ) {
            it.handleResult({
                assert(false) { "${VCLErrorCode.MismatchedPresentationRequestInspectorDid.value} error code is expected" }
            }, { error ->
                assert(error.errorCode == VCLErrorCode.MismatchedPresentationRequestInspectorDid.value)
            })
        }
    }
}