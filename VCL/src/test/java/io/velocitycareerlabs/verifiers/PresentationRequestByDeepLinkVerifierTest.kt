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

    private val correctDeepLink = DeepLinkMocks.PresentationRequestDeepLinkDevNet
    private val wrongDeepLink = VCLDeepLink(
        "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f163056%2Finspect%2Fget-presentation-request%3Fid%3D62e0e80c5ebfe73230b0becc%26inspectorDid%3Ddid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f163056%26vendorOriginContext%3D%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22%2C%22KeyCode%22%3A%2254514480%22%7D%2C%22Token%22%3A%22832077a4%22%7D"
    )

    @Test
    fun testVerifyPresentationRequestSuccess() {
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
    fun testVerifyPresentationRequestError() {
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