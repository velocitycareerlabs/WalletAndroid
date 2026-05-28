/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.integration

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import io.velocitycareerlabs.api.VCLCryptoServiceType
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptor
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptorByDeepLink
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptorByService
import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLIssuingType
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLPresentationRequestDescriptor
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLService
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.error.VCLStatusCode
import io.velocitycareerlabs.api.entities.initialization.VCLCryptoServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLErrorCodeCompatibilityMode
import io.velocitycareerlabs.api.entities.initialization.VCLInjectedCryptoServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLInitializationDescriptor
import io.velocitycareerlabs.api.jwt.VCLJwtVerifyService
import io.velocitycareerlabs.impl.VCLImpl
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.infrastructure.resources.valid.CountriesMocks
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialTypeSchemaMocks
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialTypesMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidDocumentMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.ErrorMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VCLJwtSignServiceMock
import io.velocitycareerlabs.infrastructure.resources.valid.VCLKeyServiceMock
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.UnknownHostException
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class ErrorTaxonomyBackwardCompatibilityBaselineTest {
    // Link validation -> invalid_link

    @Test
    fun malformedLinksAndMissingRequiredParamsReturnSdkError() {
        entryPoints.forEach { entryPoint ->
            val missingDidDeepLink = VCLDeepLink("velocity-network://${entryPoint.schemePath}")

            listOf(VCLDeepLink("not a url"), missingDidDeepLink).forEach { deepLink ->
                val error = getLegacyEntryPointError(entryPoint, deepLink)

                assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
                assertTrue(error.message!!.contains("did was not found"))
            }
        }
    }

    @Test
    fun unsupportedSchemeWithKnownQueryParamsReturnsNullEndpointSdkError() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "https://example.com/${entryPoint.schemePath}?${entryPoint.didParam}=did:example:entity"
            )
            val error = getLegacyEntryPointError(entryPoint, deepLink)

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertEquals(entryPoint.endpointNullMessage, error.message)
        }
    }

    @Test
    fun undecodableQueryParamsThrowBeforeSdkEntryPoint() {
        listOf(
            "velocity-network://issue?request_uri=%",
            "velocity-network://inspect?request_uri=%",
        ).forEach { deepLinkValue ->
            try {
                VCLDeepLink(deepLinkValue)
                fail("Invalid URL encoding should throw")
            } catch (error: IllegalArgumentException) {
                assertTrue(error.message!!.contains("URLDecoder"))
            }
        }
    }

    @Test
    fun missingRequestUriProducesEndpointNullSdkErrors() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?${entryPoint.didParam}=did:example:entity"
            )
            val error = getLegacyEntryPointError(entryPoint, deepLink)

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertEquals(entryPoint.endpointNullMessage, error.message)
        }
    }

    @Test
    fun missingDirectRequestDidPreservesLegacyDidMessage() {
        val error = getLegacyCredentialManifestDescriptorError(
            descriptor = credentialManifestDescriptorByService(did = ""),
        )

        assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
        assertTrue(error.message!!.contains("did was not found"))
    }

    @Test
    fun malformedAndDisallowedRequestUriValuesReachTransportAsRawEndpointText() {
        entryPoints.forEach { entryPoint ->
            val malformedRequestUriDeepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?request_uri=not-a-url" +
                    "&${entryPoint.didParam}=did:example:entity"
            )
            val disallowedSchemeDeepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?" +
                    "request_uri=ftp%3A%2F%2Fexample.com%2Frequest" +
                    "&${entryPoint.didParam}=did:example:entity"
            )
            val malformedRequestUri = getLegacyEntryPointError(entryPoint, malformedRequestUriDeepLink)
            val disallowedSchemeRequestUri = getLegacyEntryPointError(entryPoint, disallowedSchemeDeepLink)

            assertEquals(VCLErrorCode.SdkError.value, malformedRequestUri.errorCode)
            assertTrue(malformedRequestUri.message!!.contains("no protocol: not-a-url"))
            assertEquals(VCLErrorCode.SdkError.value, disallowedSchemeRequestUri.errorCode)
            assertTrue(disallowedSchemeRequestUri.message!!.contains("unknown protocol: ftp"))
        }
    }

    // Client request fetch -> client_request_unauthorized / client_request_rejected

    @Test
    fun transportFailureReturnsSdkErrorWithNetworkStatusOnly() {
        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    requestFailure = UnknownHostException("offline"),
                ),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertEquals(VCLStatusCode.NetworkError.value, error.statusCode)
            assertTrue(error.message!!.contains("offline"))
        }
    }

    @Test
    fun requestEndpoint401And403PreserveHttpStatusAndPayloadErrorCode() {
        entryPoints.forEach { entryPoint ->
            listOf(401, 403).forEach { statusCode ->
                val error = getLegacyEntryPointError(
                    entryPoint,
                    router = defaultRouter(entryPoint).copy(
                        requestStatusCode = statusCode,
                        requestPayload = ErrorMocks.Payload,
                        requestContentType = Request.ContentTypeApplicationJson,
                    ),
                )

                assertEquals(ErrorMocks.ErrorCode, error.errorCode)
                assertEquals(ErrorMocks.RequestId, error.requestId)
                assertEquals(ErrorMocks.Message, error.message)
                assertEquals(statusCode, error.statusCode)
            }
        }
    }

    @Test
    fun requestEndpointRejectionsPreserveHttpStatusWhenPayloadHasNoStatusCode() {
        val payloadWithoutStatusCode = JSONObject(ErrorMocks.Payload).apply {
            remove(VCLError.KeyStatusCode)
        }.toString()

        entryPoints.forEach { entryPoint ->
            listOf(400, 404, 409, 410, 422, 500, 502).forEach { statusCode ->
                val error = getLegacyEntryPointError(
                    entryPoint,
                    router = defaultRouter(entryPoint).copy(
                        requestStatusCode = statusCode,
                        requestPayload = payloadWithoutStatusCode,
                        requestContentType = Request.ContentTypeApplicationJson,
                    ),
                )

                assertEquals(ErrorMocks.ErrorCode, error.errorCode)
                assertEquals(statusCode, error.statusCode)
            }
        }
    }

    @Test
    fun plainTextRequestEndpointRejectionsDefaultToSdkErrorWithHttpStatusAndPayloadMessage() {
        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    requestStatusCode = 500,
                    requestPayload = "plain text failure",
                    requestContentType = "text/plain",
                ),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertEquals(500, error.statusCode)
            assertEquals("plain text failure", error.message)
            assertEquals("plain text failure", error.payload)
        }
    }

    @Test
    fun jsonRequestEndpointRejectionsWithoutErrorCodeDefaultToSdkError() {
        val payloadWithoutErrorCode = JSONObject(ErrorMocks.Payload).apply {
            remove(VCLError.KeyErrorCode)
        }.toString()

        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    requestStatusCode = 422,
                    requestPayload = payloadWithoutErrorCode,
                    requestContentType = Request.ContentTypeApplicationJson,
                ),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertEquals(ErrorMocks.RequestId, error.requestId)
            assertEquals(ErrorMocks.Message, error.message)
            assertEquals(422, error.statusCode)
        }
    }

    @Test
    fun emptyRequestEndpointResponseReturnsSdkError() {
        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(requestPayload = ""),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
        }
    }

    @Test
    fun malformedRequestEndpointResponseReturnsSdkError() {
        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(requestPayload = "not json"),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
        }
    }

    @Test
    fun missingExpectedRequestFieldsReturnSdkErrorAfterEmptyJwtIsDecoded() {
        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(requestPayload = "{}"),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
        }
    }

    // DID resolution -> issuer_did_unresolvable / verifier_did_unresolvable

    @Test
    fun didResolutionNetworkFailurePropagatesSdkErrorAndStatusFromNetwork() {
        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    didDocumentStatusCode = 404,
                    didDocumentPayload = """{"message":"resolve failed","errorCode":"sdk_error"}""",
                    didDocumentContentType = Request.ContentTypeApplicationJson,
                ),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertEquals(404, error.statusCode)
            assertEquals("resolve failed", error.message)
        }
    }

    @Test
    fun invalidDidDocumentShapeReturnsSdkErrorAtRequestValidation() {
        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(didDocumentPayload = "not json"),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertTrue(error.message!!.contains("public jwk not found for kid"))
        }
    }

    @Test
    fun missingDidDocumentVerificationMaterialReturnsSdkError() {
        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(didDocumentPayload = "{}"),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertTrue(error.message!!.contains("public jwk not found for kid"))
        }
    }

    // Registration / profile check -> issuer_not_registered / verifier_not_registered

    @Test
    fun verifiedProfileLookupFailurePropagatesNetworkErrorDetails() {
        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    verifiedProfileStatusCode = 404,
                    verifiedProfilePayload = """{"message":"profile missing","errorCode":"sdk_error"}""",
                    verifiedProfileContentType = Request.ContentTypeApplicationJson,
                ),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertEquals(404, error.statusCode)
            assertEquals("profile missing", error.message)
        }
    }

    // Request authorization -> issuer_request_unauthorized / verifier_request_unauthorized

    @Test
    fun emptyVerifiedProfileFailsServiceTypeVerification() {
        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(verifiedProfilePayload = "{}"),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertEquals(VCLStatusCode.VerificationError.value, error.statusCode)
            assertTrue(error.message!!.contains("Wrong service type"))
        }
    }

    @Test
    fun wrongIssuerOrVerifierServiceTypeReturnsSdkErrorWithVerificationStatus() {
        entryPoints.forEach { entryPoint ->
            val wrongServiceProfile = when (entryPoint) {
                EntryPoint.Issuing -> VerifiedProfileMocks.VerifiedProfileInspectorJsonStr
                EntryPoint.Presentation -> VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1
            }
            val error = getLegacyEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(verifiedProfilePayload = wrongServiceProfile),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertEquals(VCLStatusCode.VerificationError.value, error.statusCode)
            assertTrue(error.message!!.contains("Wrong service type"))
        }
    }

    // Request validation -> issuer_request_invalid / verifier_request_invalid

    @Test
    fun duplicateQueryParamsUseLastDidValueAtSdkEntryPoint() {
        entryPoints.forEach { entryPoint ->
            val router = defaultRouter(entryPoint)
            val vcl = initializedVcl(router, errorCodeCompatibilityMode = VCLErrorCodeCompatibilityMode.Legacy)
            val deepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?request_uri=${entryPoint.encodedRequestUri}" +
                    "&${entryPoint.didParam}=did:example:first&${entryPoint.didParam}=${entryPoint.lastDid}"
            )
            val error = when (entryPoint) {
                EntryPoint.Issuing -> awaitCredentialManifestError(
                    vcl,
                    credentialManifestDescriptor(deepLink),
                )
                EntryPoint.Presentation -> awaitPresentationRequestError(
                    vcl,
                    presentationDescriptor(deepLink),
                )
            }

            assertEquals(entryPoint.mismatchErrorCode, error.errorCode)
            assertTrue(router.requestedEndpoints.any { it.contains(entryPoint.lastDid) })
        }
    }

    @Test
    fun malformedDidSyntaxIsAcceptedUntilRequestValidation() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?request_uri=${entryPoint.encodedRequestUri}" +
                    "&${entryPoint.didParam}=not-a-did"
            )
            val error = getLegacyEntryPointError(entryPoint, deepLink)

            assertEquals(entryPoint.mismatchErrorCode, error.errorCode)
        }
    }

    @Test
    fun requestValidationFailuresUseLegacyMismatchErrorCodes() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?request_uri=${entryPoint.encodedRequestUri}" +
                    "&${entryPoint.didParam}=did:example:wrong"
            )
            val error = getLegacyEntryPointError(entryPoint, deepLink)

            assertEquals(entryPoint.mismatchErrorCode, error.errorCode)
        }
    }

    @Test
    fun jwtVerificationFailurePropagatesSdkErrorFromInjectedJwtService() {
        val expectedError = VCLError(
            errorCode = VCLErrorCode.SdkError.value,
            message = "jwt signature verification failed",
        )

        entryPoints.forEach { entryPoint ->
            val error = getLegacyEntryPointError(
                entryPoint,
                jwtVerificationResult = VCLResult.Failure(expectedError),
            )

            assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
            assertEquals("jwt signature verification failed", error.message)
        }
    }

}
