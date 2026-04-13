package io.velocitycareerlabs.verifiers

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.verifiers.PresentationRequestByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.domain.verifiers.PresentationRequestByDeepLinkVerifier
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidDocumentMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestMocks
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
                assertTrue(isVerified)
            }, { error ->
                fail("${error.toJsonObject()}")
            })
        }
    }

    @Test
    fun testVerifyPresentationRequestSuccessWithDidDocumentIdInDeepLink() {
        subject = PresentationRequestByDeepLinkVerifierImpl()
        val deepLinkWithDidDocumentId = deepLinkWithInspectorDid(DidDocumentMocks.DidDocumentMock.id)

        subject.verifyPresentationRequest(
            presentationRequest,
            deepLinkWithDidDocumentId,
            DidDocumentMocks.DidDocumentMock
        ) {
            it.handleResult({ isVerified ->
                assertTrue(isVerified)
            }, { error ->
                fail("${error.toJsonObject()}")
            })
        }
    }

    @Test
    fun testVerifyPresentationRequestSuccessWithDidDocumentIdInPresentationRequest() {
        subject = PresentationRequestByDeepLinkVerifierImpl()

        val originalDidDocumentId = DidDocumentMocks.DidDocumentMock.id
        val didDocumentPayload = JSONObject(DidDocumentMocks.DidDocumentMock.payload.toString())
        didDocumentPayload.put(VCLDidDocument.KeyId, presentationRequest.iss)

        val alsoKnownAs = didDocumentPayload.optJSONArray(VCLDidDocument.KeyAlsoKnownAs) ?: JSONArray()
        val alreadyContainsOriginalDidDocumentId = (0 until alsoKnownAs.length())
            .any { alsoKnownAs.optString(it) == originalDidDocumentId }
        if (!alreadyContainsOriginalDidDocumentId) {
            alsoKnownAs.put(originalDidDocumentId)
        }
        didDocumentPayload.put(VCLDidDocument.KeyAlsoKnownAs, alsoKnownAs)

        val didDocumentWithPresentationRequestIss = VCLDidDocument(didDocumentPayload)
        val deepLinkWithDidDocumentAlias = deepLinkWithInspectorDid(originalDidDocumentId)

        subject.verifyPresentationRequest(
            presentationRequest,
            deepLinkWithDidDocumentAlias,
            didDocumentWithPresentationRequestIss
        ) {
            it.handleResult({ isVerified ->
                assertTrue(isVerified)
            }, { error ->
                fail("${error.toJsonObject()}")
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
                fail("${VCLErrorCode.MismatchedPresentationRequestInspectorDid.value} error code is expected")
            }, { error ->
                assertEquals(VCLErrorCode.MismatchedPresentationRequestInspectorDid.value, error.errorCode)
            })
        }
    }

    @Test
    fun testVerifyPresentationRequestErrorWhenDeepLinkDidMissing() {
        subject = PresentationRequestByDeepLinkVerifierImpl()

        subject.verifyPresentationRequest(
            presentationRequest,
            VCLDeepLink(value = "velocity-network://inspect"),
            DidDocumentMocks.DidDocumentMock
        ) {
            it.handleResult({
                fail("${VCLErrorCode.SdkError.value} error code is expected")
            }, { error ->
                assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
                assertTrue(error.message?.contains("DID not found in deep link") == true)
            })
        }
    }

    private fun deepLinkWithInspectorDid(inspectorDid: String): VCLDeepLink {
        val encodedInspectorDid = URLEncoder.encode(inspectorDid, StandardCharsets.UTF_8.toString())
        return VCLDeepLink(value = "velocity-network://inspect?inspectorDid=$encodedInspectorDid")
    }
}
