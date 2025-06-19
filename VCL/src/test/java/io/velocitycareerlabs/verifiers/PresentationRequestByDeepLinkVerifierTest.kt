package io.velocitycareerlabs.verifiers

import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.repositories.ResolveDidDocumentRepositoryImpl
import io.velocitycareerlabs.impl.data.verifiers.PresentationRequestByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.domain.verifiers.PresentationRequestByDeepLinkVerifier
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidDocumentMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestMocks
import org.junit.Test

class PresentationRequestByDeepLinkVerifierTest {
    private lateinit var subject: PresentationRequestByDeepLinkVerifier

    private val presentationRequest = PresentationRequestMocks.PresentationRequest

    @Test
    fun testVerifyPresentationRequestSuccess() {
        subject = PresentationRequestByDeepLinkVerifierImpl()

        subject.verifyPresentationRequest(
            presentationRequest,
            DeepLinkMocks.PresentationRequestDeepLinkDevNet,
            DidDocumentMocks.DidDocumentMock
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
        subject = PresentationRequestByDeepLinkVerifierImpl()

        subject.verifyPresentationRequest(
            presentationRequest,
            DeepLinkMocks.CredentialManifestDeepLinkMainNet,
            DidDocumentMocks.DidDocumentWithWrongDidMock
        ) {
            it.handleResult({
                assert(false) { "${VCLErrorCode.MismatchedPresentationRequestInspectorDid.value} error code is expected" }
            }, { error ->
                assert(error.errorCode == VCLErrorCode.MismatchedPresentationRequestInspectorDid.value)
            })
        }
    }
}