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
import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLIssuingType
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLPresentationRequestDescriptor
import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLService
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.error.VCLStatusCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.api.entities.initialization.VCLCryptoServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLInjectedCryptoServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLInitializationDescriptor
import io.velocitycareerlabs.api.jwt.VCLJwtVerifyService
import io.velocitycareerlabs.impl.VCLImpl
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.infrastructure.network.Response
import io.velocitycareerlabs.impl.data.repositories.CredentialManifestRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.PresentationRequestRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.CredentialManifestUseCaseImpl
import io.velocitycareerlabs.impl.data.usecases.PresentationRequestUseCaseImpl
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.CredentialManifestRepository
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.repositories.PresentationRequestRepository
import io.velocitycareerlabs.impl.domain.repositories.ResolveDidDocumentRepository
import io.velocitycareerlabs.impl.domain.verifiers.CredentialManifestByDeepLinkVerifier
import io.velocitycareerlabs.impl.domain.verifiers.PresentationRequestByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.ErrorTaxonomy
import io.velocitycareerlabs.impl.utils.ProfileServiceTypeVerifier
import io.velocitycareerlabs.impl.utils.VelocityDeepLinkValidator
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
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
import java.net.URLEncoder
import java.net.UnknownHostException
import java.util.Base64
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class ErrorTaxonomyContractTest {
    // Link validation -> invalid_link

    @Test
    fun malformedLinksAndMissingRequiredParamsReturnInvalidLink() {
        entryPoints.forEach { entryPoint ->
            val missingDidDeepLink = VCLDeepLink("velocity-network://${entryPoint.schemePath}")

            mapOf(
                VCLDeepLink("not a url") to VelocityDeepLinkValidator.SourceUnparseablePayload,
                missingDidDeepLink to VelocityDeepLinkValidator.SourceInvalidOrMissingDid,
            ).forEach { (deepLink, sourceErrorCode) ->
                val error = getEntryPointError(entryPoint, deepLink)

                assertDiagnostics(
                    expected = entryPoint.expectedDiagnostics(
                        errorCode = VCLErrorCode.InvalidLink.value,
                        sourceErrorCode = sourceErrorCode,
                        validationPhase = "link_validation",
                        requestUri = deepLink.requestUri,
                    ),
                    actual = error,
                )
            }
        }
    }

    @Test
    fun opaqueVelocityUrisReturnUnparseableInvalidLink() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "velocity-network:${entryPoint.schemePath}?request_uri=${entryPoint.encodedRequestUri}" +
                    "&${entryPoint.didParam}=did:example:entity"
            )
            val error = getEntryPointError(entryPoint, deepLink)

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = VCLErrorCode.InvalidLink.value,
                    sourceErrorCode = VelocityDeepLinkValidator.SourceUnparseablePayload,
                    validationPhase = "link_validation",
                    requestUri = deepLink.requestUri,
                ),
                actual = error,
            )
        }
    }

    @Test
    fun unsupportedSchemeWithKnownQueryParamsReturnsInvalidLink() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "https://example.com/${entryPoint.schemePath}?${entryPoint.didParam}=did:example:entity"
            )
            val error = getEntryPointError(entryPoint, deepLink)

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = VCLErrorCode.InvalidLink.value,
                    sourceErrorCode = VelocityDeepLinkValidator.SourceUnsupportedVelocityLink,
                    validationPhase = "link_validation",
                    requestUri = deepLink.requestUri,
                ),
                actual = error,
            )
        }
    }

    @Test
    fun unsupportedFlowPathReturnsInvalidLink() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "velocity-network://unknown-flow?request_uri=${entryPoint.encodedRequestUri}" +
                    "&${entryPoint.didParam}=did:example:entity"
            )
            val error = getEntryPointError(entryPoint, deepLink)

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = VCLErrorCode.InvalidLink.value,
                    sourceErrorCode = VelocityDeepLinkValidator.SourceUnsupportedVelocityLink,
                    validationPhase = "link_validation",
                    requestUri = deepLink.requestUri,
                ),
                actual = error,
            )
        }
    }

    @Test
    fun wrongFlowDidParamIsAcceptedByLaxDidParsingAndFailsRequestVerification() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?request_uri=${entryPoint.encodedRequestUri}" +
                    "&${entryPoint.otherDidParam}=did:example:entity"
            )
            val error = getEntryPointError(entryPoint, deepLink)

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = entryPoint.legacyMismatchErrorCode,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = deepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
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
    fun missingRequestUriProducesInvalidLink() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?${entryPoint.didParam}=did:example:entity"
            )
            val error = getEntryPointError(entryPoint, deepLink)

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = VCLErrorCode.InvalidLink.value,
                    sourceErrorCode = VelocityDeepLinkValidator.SourceInvalidOrMissingRequestUri,
                    validationPhase = "link_validation",
                    requestUri = null,
                ),
                actual = error,
            )
        }
    }

    @Test
    fun malformedAndDisallowedRequestUriValuesReturnInvalidLink() {
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
            val malformedRequestUri = getEntryPointError(entryPoint, malformedRequestUriDeepLink)
            val disallowedSchemeRequestUri = getEntryPointError(entryPoint, disallowedSchemeDeepLink)

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = VCLErrorCode.InvalidLink.value,
                    sourceErrorCode = VelocityDeepLinkValidator.SourceInvalidOrMissingRequestUri,
                    validationPhase = "link_validation",
                    requestUri = malformedRequestUriDeepLink.requestUri,
                ),
                actual = malformedRequestUri,
            )
            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = VCLErrorCode.InvalidLink.value,
                    sourceErrorCode = VelocityDeepLinkValidator.SourceInvalidOrMissingRequestUri,
                    validationPhase = "link_validation",
                    requestUri = disallowedSchemeDeepLink.requestUri,
                ),
                actual = disallowedSchemeRequestUri,
            )
        }
    }

    @Test
    fun invalidDirectRequestEndpointReturnsInvalidLink() {
        val endpoint = "ftp://example.com/request"
        val vcl = initializedVcl(defaultRouter(EntryPoint.Issuing))
        val error = awaitCredentialManifestError(
            vcl,
            credentialManifestDescriptorByService(endpoint = endpoint),
        )

        assertDiagnostics(
            expected = EntryPoint.Issuing.expectedDiagnostics(
                errorCode = VCLErrorCode.InvalidLink.value,
                sourceErrorCode = VelocityDeepLinkValidator.SourceInvalidOrMissingRequestEndpoint,
                validationPhase = "link_validation",
                requestUri = endpoint,
            ),
            actual = error,
        )
    }

    @Test
    fun missingDirectRequestEndpointReturnsInvalidLink() {
        val vcl = initializedVcl(defaultRouter(EntryPoint.Issuing))
        val error = awaitCredentialManifestError(
            vcl,
            credentialManifestDescriptorByService(endpoint = ""),
        )

        assertDiagnostics(
            expected = EntryPoint.Issuing.expectedDiagnostics(
                errorCode = VCLErrorCode.InvalidLink.value,
                sourceErrorCode = VelocityDeepLinkValidator.SourceInvalidOrMissingRequestEndpoint,
                validationPhase = "link_validation",
                requestUri = "",
            ),
            actual = error,
        )
    }

    @Test
    fun missingDirectRequestDidReturnsInvalidLink() {
        val vcl = initializedVcl(defaultRouter(EntryPoint.Issuing))
        val error = awaitCredentialManifestError(
            vcl,
            credentialManifestDescriptorByService(did = ""),
        )

        assertDiagnostics(
            expected = EntryPoint.Issuing.expectedDiagnostics(
                errorCode = VCLErrorCode.InvalidLink.value,
                validationPhase = "link_validation",
            ),
            actual = error,
        )
        assertTrue(error.message!!.contains("did was not found"))
    }

    @Test
    fun malformedDidSyntaxReturnsInvalidLink() {
        entryPoints.forEach { entryPoint ->
            listOf("not-a-did", "did:", "did:example", "did:Example:entity").forEach { did ->
                val deepLink = VCLDeepLink(
                    "velocity-network://${entryPoint.schemePath}?request_uri=${entryPoint.encodedRequestUri}" +
                        "&${entryPoint.didParam}=$did"
                )
                val error = getEntryPointError(entryPoint, deepLink)

                assertDiagnostics(
                    expected = entryPoint.expectedDiagnostics(
                        errorCode = VCLErrorCode.InvalidLink.value,
                        sourceErrorCode = VelocityDeepLinkValidator.SourceInvalidOrMissingDid,
                        validationPhase = "link_validation",
                        requestUri = deepLink.requestUri,
                    ),
                    actual = error,
                )
            }
        }
    }

    @Test
    fun didValidationDoesNotUseBacktrackingRegex() {
        entryPoints.forEach { entryPoint ->
            val did = "did:example:" + ":".repeat(10_000)
            val deepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?request_uri=${entryPoint.encodedRequestUri}" +
                    "&${entryPoint.didParam}=$did"
            )
            val error = getEntryPointError(entryPoint, deepLink)

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = entryPoint.legacyMismatchErrorCode,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = deepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
        }
    }

    // Client request fetch -> client_request_unauthorized / client_request_rejected

    @Test
    fun transportFailureReturnsConnectivityFailureWithNetworkStatusOnly() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    requestFailure = UnknownHostException("offline"),
                ),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                        errorCode = VCLErrorCode.ConnectivityFailure.value,
                        statusCode = VCLStatusCode.NetworkError.value,
                        validationPhase = "client_request_fetch",
                        requestUri = entryPoint.defaultDeepLink.requestUri,
                ),
                actual = error,
            )
            assertTrue(error.message!!.contains("offline"))
        }
    }

    @Test
    fun requestEndpoint401And403PreserveHttpStatusAndPayloadErrorCode() {
        entryPoints.forEach { entryPoint ->
            listOf(401, 403).forEach { statusCode ->
                val error = getEntryPointError(
                    entryPoint,
                    router = defaultRouter(entryPoint).copy(
                        requestStatusCode = statusCode,
                        requestPayload = ErrorMocks.Payload,
                        requestContentType = Request.ContentTypeApplicationJson,
                    ),
                )

                assertDiagnostics(
                    expected = entryPoint.expectedDiagnostics(
                        payload = ErrorMocks.Payload,
                        error = ErrorMocks.Error,
                        errorCode = VCLErrorCode.ClientRequestUnauthorized.value,
                        sourceErrorCode = ErrorMocks.ErrorCode,
                        requestId = ErrorMocks.RequestId,
                        statusCode = statusCode,
                        validationPhase = "client_request_fetch",
                        requestUri = entryPoint.defaultDeepLink.requestUri,
                    ),
                    actual = error,
                )
                assertEquals(ErrorMocks.Message, error.message)
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
                val error = getEntryPointError(
                    entryPoint,
                    router = defaultRouter(entryPoint).copy(
                        requestStatusCode = statusCode,
                        requestPayload = payloadWithoutStatusCode,
                        requestContentType = Request.ContentTypeApplicationJson,
                    ),
                )

                assertDiagnostics(
                    expected = entryPoint.expectedDiagnostics(
                        payload = payloadWithoutStatusCode,
                        error = ErrorMocks.Error,
                        errorCode = VCLErrorCode.ClientRequestRejected.value,
                        sourceErrorCode = ErrorMocks.ErrorCode,
                        requestId = ErrorMocks.RequestId,
                        statusCode = statusCode,
                        validationPhase = "client_request_fetch",
                        requestUri = entryPoint.defaultDeepLink.requestUri,
                    ),
                    actual = error,
                )
            }
        }
    }

    @Test
    fun plainTextRequestEndpointRejectionsReturnClientRequestRejectedWithHttpStatusAndPayloadMessage() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    requestStatusCode = 500,
                    requestPayload = "plain text failure",
                    requestContentType = "text/plain",
                ),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    payload = "plain text failure",
                    errorCode = VCLErrorCode.ClientRequestRejected.value,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    statusCode = 500,
                    validationPhase = "client_request_fetch",
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                ),
                actual = error,
            )
            assertEquals("plain text failure", error.message)
            assertEquals("plain text failure", error.payload)
        }
    }

    @Test
    fun jsonRequestEndpointRejectionsWithoutErrorCodeReturnClientRequestRejected() {
        val payloadWithoutErrorCode = JSONObject(ErrorMocks.Payload).apply {
            remove(VCLError.KeyErrorCode)
        }.toString()

        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    requestStatusCode = 422,
                    requestPayload = payloadWithoutErrorCode,
                    requestContentType = Request.ContentTypeApplicationJson,
                ),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    payload = payloadWithoutErrorCode,
                    error = ErrorMocks.Error,
                    errorCode = VCLErrorCode.ClientRequestRejected.value,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    requestId = ErrorMocks.RequestId,
                    statusCode = 422,
                    validationPhase = "client_request_fetch",
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                ),
                actual = error,
            )
            assertEquals(ErrorMocks.Message, error.message)
        }
    }

    @Test
    fun emptyRequestEndpointResponseReturnsRequestInvalid() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(requestPayload = ""),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertEquals(
                when (entryPoint) {
                    EntryPoint.Issuing -> "Missing issuing_request"
                    EntryPoint.Presentation -> "Missing presentation_request"
                },
                error.message,
            )
        }
    }

    @Test
    fun malformedRequestEndpointResponseReturnsRequestInvalid() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(requestPayload = "not json"),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertEquals(
                when (entryPoint) {
                    EntryPoint.Issuing -> "Missing issuing_request"
                    EntryPoint.Presentation -> "Missing presentation_request"
                },
                error.message,
            )
        }
    }

    @Test
    fun missingExpectedRequestFieldsReturnRequestInvalid() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(requestPayload = "{}"),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertEquals(
                when (entryPoint) {
                    EntryPoint.Issuing -> "Missing issuing_request"
                    EntryPoint.Presentation -> "Missing presentation_request"
                },
                error.message,
            )
        }
    }

    @Test
    fun malformedRequestJwtReturnsRequestInvalid() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    requestPayload = entryPoint.requestPayloadForJwt("not-a-jwt"),
                ),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertEquals("Malformed JWT", error.message)
        }
    }

    @Test
    fun emptyIssuingRequestReturnsRequestInvalid() {
        val error = getEntryPointError(
            EntryPoint.Issuing,
            router = BaselineHttpRouter(
                requestPayload = JSONObject()
                    .put(VCLCredentialManifest.KeyIssuingRequest, "")
                    .toString(),
            ),
        )

        assertDiagnostics(
            expected = EntryPoint.Issuing.expectedDiagnostics(
                errorCode = VCLErrorCode.IssuerRequestInvalid.value,
                sourceErrorCode = VCLErrorCode.SdkError.value,
                validationPhase = "request_validation",
                requestDid = EntryPoint.Issuing.requestDid,
                requestUri = EntryPoint.Issuing.defaultDeepLink.requestUri,
                requestKind = EntryPoint.Issuing.requestKind,
            ),
            actual = error,
        )
        assertEquals("Missing issuing_request", error.message)
    }

    @Test
    fun credentialManifestByServiceSucceedsWithoutDeepLinkVerification() {
        val router = defaultRouter(EntryPoint.Issuing)
        val vcl = initializedVcl(router)
        val credentialManifest = awaitCredentialManifest(
            vcl,
            credentialManifestDescriptorByService(),
        )

        assertNull(credentialManifest.deepLink)
        assertEquals(DeepLinkMocks.IssuerDid, credentialManifest.did)
        assertTrue(router.requestedEndpoints.any { it.contains("get-credential-manifest") })
    }

    @Test
    fun didResolutionNetworkFailureReturnsDidUnresolvableWithStatusFromNetwork() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    didDocumentStatusCode = 404,
                    didDocumentPayload = """{"message":"resolve failed","errorCode":"sdk_error"}""",
                    didDocumentContentType = Request.ContentTypeApplicationJson,
                ),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.didUnresolvableErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    statusCode = 404,
                    validationPhase = "did_resolution",
                    requestDid = entryPoint.requestDid,
                    payload = """{"message":"resolve failed","errorCode":"sdk_error"}""",
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertEquals("resolve failed", error.message)
        }
    }

    @Test
    fun invalidDidDocumentShapeReturnsDidUnresolvable() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(didDocumentPayload = "not json"),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.didUnresolvableErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "did_resolution",
                    requestDid = entryPoint.requestDid,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertTrue(error.message!!.contains("public jwk not found for kid"))
        }
    }

    @Test
    fun missingDidDocumentVerificationMaterialReturnsDidUnresolvable() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(didDocumentPayload = "{}"),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.didUnresolvableErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "did_resolution",
                    requestDid = entryPoint.requestDid,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertTrue(error.message!!.contains("public jwk not found for kid"))
        }
    }

    @Test
    fun emptyDidDocumentVerificationMethodsReturnsDidUnresolvable() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(didDocumentPayload = """{"verificationMethod":[]}"""),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.didUnresolvableErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "did_resolution",
                    requestDid = entryPoint.requestDid,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertTrue(error.message!!.contains("public jwk not found for kid"))
        }
    }

    @Test
    fun missingJwtKidReturnsRequestInvalid() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    requestPayload = entryPoint.requestPayloadForJwt(
                        encodedJwtWithoutKid(entryPoint.defaultRequestJwt)
                    ),
                ),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertEquals("JWT kid is missing", error.message)
        }
    }

    @Test
    fun jwtKidMissingFromDidDocumentVerificationMethodsReturnsRequestInvalid() {
        entryPoints.forEach { entryPoint ->
            val missingVerificationMethodKid = "${entryPoint.requestDid}#missing-key"
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    requestPayload = entryPoint.requestPayloadForJwt(
                        encodedJwtWithKid(entryPoint.defaultRequestJwt, missingVerificationMethodKid)
                    ),
                ),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertEquals("public jwk not found for kid: $missingVerificationMethodKid", error.message)
        }
    }

    @Test
    fun verifiedProfileLookupFailurePropagatesNetworkErrorDetails() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(
                    verifiedProfileStatusCode = 404,
                    verifiedProfilePayload = """{"message":"profile missing","errorCode":"sdk_error"}""",
                    verifiedProfileContentType = Request.ContentTypeApplicationJson,
                ),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.notRegisteredErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    statusCode = 404,
                    validationPhase = "registration_check",
                    payload = """{"message":"profile missing","errorCode":"sdk_error"}""",
                    requestKind = entryPoint.requestKind,
                    requestDid = entryPoint.requestDid,
                ),
                actual = error,
            )
            assertEquals("profile missing", error.message)
        }
    }

    // Request authorization -> issuer_request_unauthorized / verifier_request_unauthorized

    @Test
    fun emptyVerifiedProfileFailsServiceTypeVerification() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(verifiedProfilePayload = "{}"),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestUnauthorizedErrorCode,
                    sourceErrorCode = ProfileServiceTypeVerifier.SourceWrongServiceType,
                    statusCode = VCLStatusCode.VerificationError.value,
                    validationPhase = "request_authorization",
                    requestKind = entryPoint.requestKind,
                    requestDid = entryPoint.requestDid,
                ),
                actual = error,
            )
            assertTrue(error.message!!.contains("Wrong service type"))
        }
    }

    @Test
    fun wrongIssuerOrVerifierServiceTypeReturnsRequestUnauthorizedWithVerificationStatus() {
        entryPoints.forEach { entryPoint ->
            val wrongServiceProfile = when (entryPoint) {
                EntryPoint.Issuing -> VerifiedProfileMocks.VerifiedProfileInspectorJsonStr
                EntryPoint.Presentation -> VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1
            }
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(verifiedProfilePayload = wrongServiceProfile),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestUnauthorizedErrorCode,
                    sourceErrorCode = ProfileServiceTypeVerifier.SourceWrongServiceType,
                    statusCode = VCLStatusCode.VerificationError.value,
                    validationPhase = "request_authorization",
                    requestKind = entryPoint.requestKind,
                    requestDid = entryPoint.requestDid,
                ),
                actual = error,
            )
            assertTrue(error.message!!.contains("Wrong service type"))
        }
    }

    // Request validation -> issuer_request_invalid / verifier_request_invalid

    @Test
    fun duplicateQueryParamsUseLastDidValueAtSdkEntryPoint() {
        entryPoints.forEach { entryPoint ->
            val router = defaultRouter(entryPoint)
            val deepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?request_uri=${entryPoint.encodedRequestUri}" +
                    "&${entryPoint.didParam}=did:example:first&${entryPoint.didParam}=${entryPoint.lastDid}"
            )
            val error = getEntryPointError(entryPoint, deepLink, router)

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = entryPoint.legacyMismatchErrorCode,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = deepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertTrue(router.requestedEndpoints.any { it.contains(entryPoint.lastDid) })
        }
    }

    @Test
    fun requestValidationFailuresUseTaxonomyCodes() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?request_uri=${entryPoint.encodedRequestUri}" +
                    "&${entryPoint.didParam}=did:example:wrong"
            )
            val error = getEntryPointError(entryPoint, deepLink)

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = entryPoint.legacyMismatchErrorCode,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = deepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
        }
    }

    @Test
    fun jwtVerificationFailureReturnsRequestInvalidFromInjectedJwtService() {
        val expectedError = VCLError(
            errorCode = VCLErrorCode.SdkError.value,
            message = "jwt signature verification failed",
        )

        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                jwtVerificationResult = VCLResult.Failure(expectedError),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertEquals("jwt signature verification failed", error.message)
        }
    }

}
