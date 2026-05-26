/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.backwardscompatibility

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import io.velocitycareerlabs.api.VCLCryptoServiceType
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptorByDeepLink
import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLPresentationRequestDescriptor
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.error.VCLStatusCode
import io.velocitycareerlabs.api.entities.initialization.VCLCryptoServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLInjectedCryptoServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLInitializationDescriptor
import io.velocitycareerlabs.api.jwt.VCLJwtVerifyService
import io.velocitycareerlabs.impl.VCLImpl
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.utils.ProfileServiceTypeVerifier
import io.velocitycareerlabs.impl.utils.VelocityDeepLinkValidator
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
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class ErrorTaxonomyContractTest {
    // Link validation -> invalid_link

    @Test
    fun malformedLinksAndMissingRequiredParamsReturnSdkError() {
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
    fun unsupportedSchemeWithKnownQueryParamsReturnsNullEndpointSdkError() {
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
    fun wrongFlowDidParamReturnsInvalidLink() {
        entryPoints.forEach { entryPoint ->
            val deepLink = VCLDeepLink(
                "velocity-network://${entryPoint.schemePath}?request_uri=${simpleRequestUri()}" +
                    "&${entryPoint.otherDidParam}=did:example:entity"
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

    // Client request fetch -> client_request_unauthorized / client_request_rejected

    @Test
    fun transportFailureReturnsSdkErrorWithNetworkStatusOnly() {
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
    fun plainTextRequestEndpointRejectionsDefaultToSdkErrorWithHttpStatusAndPayloadMessage() {
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
    fun jsonRequestEndpointRejectionsWithoutErrorCodeDefaultToSdkError() {
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
    fun emptyRequestEndpointResponseReturnsSdkError() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(requestPayload = ""),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = VCLErrorCode.ClientRequestRejected.value,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "client_request_fetch",
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                ),
                actual = error,
            )
        }
    }

    @Test
    fun malformedRequestEndpointResponseReturnsSdkError() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(requestPayload = "not json"),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = VCLErrorCode.ClientRequestRejected.value,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "client_request_fetch",
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                ),
                actual = error,
            )
        }
    }

    @Test
    fun missingExpectedRequestFieldsReturnSdkErrorAfterEmptyJwtIsDecoded() {
        entryPoints.forEach { entryPoint ->
            val error = getEntryPointError(
                entryPoint,
                router = defaultRouter(entryPoint).copy(requestPayload = "{}"),
            )

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = VCLErrorCode.ClientRequestRejected.value,
                    sourceErrorCode = VCLErrorCode.SdkError.value,
                    validationPhase = "client_request_fetch",
                    requestUri = entryPoint.defaultDeepLink.requestUri,
                ),
                actual = error,
            )
        }
    }

    // DID resolution -> issuer_did_unresolvable / verifier_did_unresolvable

    @Test
    fun didResolutionNetworkFailurePropagatesSdkErrorAndStatusFromNetwork() {
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
    fun invalidDidDocumentShapeReturnsSdkErrorAtRequestValidation() {
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
    fun missingDidDocumentVerificationMaterialReturnsSdkError() {
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

    // Registration / profile check -> issuer_not_registered / verifier_not_registered

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
                ),
                actual = error,
            )
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
            val vcl = initializedVcl(router)
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

            assertDiagnostics(
                expected = entryPoint.expectedDiagnostics(
                    errorCode = entryPoint.requestInvalidErrorCode,
                    sourceErrorCode = entryPoint.legacyMismatchErrorCode,
                    validationPhase = "request_validation",
                    requestDid = entryPoint.requestDid,
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
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
        }
    }

    @Test
    fun jwtVerificationFailurePropagatesSdkErrorFromInjectedJwtService() {
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
                    requestKind = entryPoint.requestKind,
                ),
                actual = error,
            )
            assertEquals("jwt signature verification failed", error.message)
        }
    }

    private enum class EntryPoint(
        val defaultDeepLink: VCLDeepLink,
        val requestKind: String,
        val requestDid: String,
        val legacyMismatchErrorCode: String,
        val requestInvalidErrorCode: String,
        val didUnresolvableErrorCode: String,
        val notRegisteredErrorCode: String,
        val requestUnauthorizedErrorCode: String,
        val didParam: String,
        val otherDidParam: String,
        val schemePath: String,
        val encodedRequestUri: String,
    ) {
        Issuing(
            defaultDeepLink = DeepLinkMocks.CredentialManifestDeepLinkDevNet,
            requestKind = "issuing_request",
            requestDid = DeepLinkMocks.IssuerDid,
            legacyMismatchErrorCode = VCLErrorCode.MismatchedRequestIssuerDid.value,
            requestInvalidErrorCode = VCLErrorCode.IssuerRequestInvalid.value,
            didUnresolvableErrorCode = VCLErrorCode.IssuerDidUnresolvable.value,
            notRegisteredErrorCode = VCLErrorCode.IssuerNotRegistered.value,
            requestUnauthorizedErrorCode = VCLErrorCode.IssuerRequestUnauthorized.value,
            didParam = "issuerDid",
            otherDidParam = "inspectorDid",
            schemePath = "issue",
            encodedRequestUri = DeepLinkMocks.CredentialManifestRequestUriStr,
        ),
        Presentation(
            defaultDeepLink = DeepLinkMocks.PresentationRequestDeepLinkDevNet,
            requestKind = "presentation_request",
            requestDid = DeepLinkMocks.InspectorDid,
            legacyMismatchErrorCode = VCLErrorCode.MismatchedPresentationRequestInspectorDid.value,
            requestInvalidErrorCode = VCLErrorCode.VerifierRequestInvalid.value,
            didUnresolvableErrorCode = VCLErrorCode.VerifierDidUnresolvable.value,
            notRegisteredErrorCode = VCLErrorCode.VerifierNotRegistered.value,
            requestUnauthorizedErrorCode = VCLErrorCode.VerifierRequestUnauthorized.value,
            didParam = "inspectorDid",
            otherDidParam = "issuerDid",
            schemePath = "inspect",
            encodedRequestUri = DeepLinkMocks.PresentationRequestRequestUriStr,
        ),
    }

    private val entryPoints = EntryPoint.entries

    private data class ErrorDiagnostics(
        val payload: String? = null,
        val error: String? = null,
        val errorCode: String,
        val sourceErrorCode: String? = null,
        val requestId: String? = null,
        val statusCode: Int? = null,
        val validationPhase: String? = null,
        val requestDid: String? = null,
        val requestUri: String? = null,
        val requestKind: String? = null,
    )

    private fun assertDiagnostics(
        expected: ErrorDiagnostics,
        actual: VCLError,
    ) {
        assertEquals(expected.canonicalizePayload(), actual.toDiagnostics())
    }

    private fun VCLError.toDiagnostics() = ErrorDiagnostics(
        payload = payload?.canonicalJsonOrSelf(),
        error = error,
        errorCode = errorCode,
        sourceErrorCode = sourceErrorCode,
        requestId = requestId,
        statusCode = statusCode,
        validationPhase = validationPhase,
        requestDid = requestDid,
        requestUri = requestUri,
        requestKind = requestKind,
    )

    private fun ErrorDiagnostics.canonicalizePayload() =
        copy(payload = payload?.canonicalJsonOrSelf())

    private fun String.canonicalJsonOrSelf(): String =
        runCatching { JSONObject(this).toString() }.getOrDefault(this)

    private fun EntryPoint.expectedDiagnostics(
        errorCode: String,
        payload: String? = null,
        error: String? = null,
        sourceErrorCode: String? = null,
        requestId: String? = null,
        statusCode: Int? = null,
        validationPhase: String? = null,
        requestDid: String? = null,
        requestUri: String? = null,
        requestKind: String? = this.requestKind,
    ) = ErrorDiagnostics(
        payload = payload,
        error = error,
        errorCode = errorCode,
        sourceErrorCode = sourceErrorCode,
        requestId = requestId,
        statusCode = statusCode,
        validationPhase = validationPhase,
        requestDid = requestDid,
        requestUri = requestUri,
        requestKind = requestKind,
    )

    private val EntryPoint.lastDid: String get() = "did:example:last"

    private fun simpleRequestUri(): String =
        URLEncoder.encode("https://example.com/request", "UTF-8")

    private fun defaultRouter(entryPoint: EntryPoint): BaselineHttpRouter =
        when (entryPoint) {
            EntryPoint.Issuing -> BaselineHttpRouter()
            EntryPoint.Presentation -> BaselineHttpRouter(
                verifiedProfilePayload = VerifiedProfileMocks.VerifiedProfileInspectorJsonStr,
                requestPayload = PresentationRequestMocks.EncodedPresentationRequestResponse,
            )
        }

    private fun getEntryPointError(
        entryPoint: EntryPoint,
        deepLink: VCLDeepLink = entryPoint.defaultDeepLink,
        router: BaselineHttpRouter = defaultRouter(entryPoint),
        jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
    ): VCLError =
        when (entryPoint) {
            EntryPoint.Issuing -> getCredentialManifestError(deepLink, router, jwtVerificationResult)
            EntryPoint.Presentation -> getPresentationRequestError(deepLink, router, jwtVerificationResult)
        }

    private fun getCredentialManifestError(
        deepLink: VCLDeepLink,
        router: BaselineHttpRouter = BaselineHttpRouter(),
        jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
    ): VCLError {
        val vcl = initializedVcl(router, jwtVerificationResult)
        return awaitCredentialManifestError(vcl, credentialManifestDescriptor(deepLink))
    }

    private fun getPresentationRequestError(
        deepLink: VCLDeepLink,
        router: BaselineHttpRouter = BaselineHttpRouter(
            verifiedProfilePayload = VerifiedProfileMocks.VerifiedProfileInspectorJsonStr,
            requestPayload = PresentationRequestMocks.EncodedPresentationRequestResponse,
        ),
        jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
    ): VCLError {
        val vcl = initializedVcl(router, jwtVerificationResult)
        return awaitPresentationRequestError(vcl, presentationDescriptor(deepLink))
    }

    private fun initializedVcl(
        router: BaselineHttpRouter,
        jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
    ): VCLImpl {
        val vcl = VCLImpl(router::connectionFor)
        var initError: VCLError? = null
        val latch = CountDownLatch(1)
        vcl.initialize(
            context = ApplicationProvider.getApplicationContext(),
            initializationDescriptor = VCLInitializationDescriptor(
                cacheSequence = cacheSequence.incrementAndGet(),
                cryptoServicesDescriptor = VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Injected,
                    injectedCryptoServicesDescriptor = VCLInjectedCryptoServicesDescriptor(
                        keyService = VCLKeyServiceMock(),
                        jwtSignService = VCLJwtSignServiceMock(),
                        jwtVerifyService = FixedJwtVerifyService(jwtVerificationResult),
                    ),
                ),
            ),
            successHandler = {
                latch.countDown()
            },
            errorHandler = {
                initError = it
                latch.countDown()
            },
        )
        drainMainThreadUntil(latch)
        assertNull("VCL initialization failed: ${initError?.toJsonObject()}", initError)
        return vcl
    }

    private fun awaitCredentialManifestError(
        vcl: VCLImpl,
        descriptor: VCLCredentialManifestDescriptorByDeepLink,
    ): VCLError {
        var result: VCLError? = null
        val latch = CountDownLatch(1)
        vcl.getCredentialManifest(
            credentialManifestDescriptor = descriptor,
            successHandler = { manifest ->
                fail("Credential manifest failure expected: $manifest")
            },
            errorHandler = {
                result = it
                latch.countDown()
            },
        )
        drainMainThreadUntil(latch)
        return result ?: error("getCredentialManifest did not invoke errorHandler")
    }

    private fun awaitPresentationRequestError(
        vcl: VCLImpl,
        descriptor: VCLPresentationRequestDescriptor,
    ): VCLError {
        var result: VCLError? = null
        val latch = CountDownLatch(1)
        vcl.getPresentationRequest(
            presentationRequestDescriptor = descriptor,
            successHandler = { presentationRequest ->
                fail("Presentation request failure expected: $presentationRequest")
            },
            errorHandler = {
                result = it
                latch.countDown()
            },
        )
        drainMainThreadUntil(latch)
        return result ?: error("getPresentationRequest did not invoke errorHandler")
    }

    private fun drainMainThreadUntil(latch: CountDownLatch) {
        val deadline = System.currentTimeMillis() + 5_000
        while (latch.count > 0 && System.currentTimeMillis() < deadline) {
            shadowOf(android.os.Looper.getMainLooper()).idle()
            latch.await(20, TimeUnit.MILLISECONDS)
        }
        shadowOf(android.os.Looper.getMainLooper()).idle()
        assertEquals("Timed out waiting for callback", 0, latch.count)
    }

    private fun credentialManifestDescriptor(deepLink: VCLDeepLink) =
        VCLCredentialManifestDescriptorByDeepLink(
            deepLink = deepLink,
            didJwk = DidJwkMocks.DidJwk,
        )

    private fun presentationDescriptor(deepLink: VCLDeepLink) =
        VCLPresentationRequestDescriptor(
            deepLink = deepLink,
            didJwk = DidJwkMocks.DidJwk,
        )

    private class FixedJwtVerifyService(
        private val result: VCLResult<Boolean>,
    ) : VCLJwtVerifyService {
        override fun verify(
            jwt: VCLJwt,
            publicJwk: VCLPublicJwk,
            remoteCryptoServicesToken: VCLToken?,
            completionBlock: (VCLResult<Boolean>) -> Unit,
        ) {
            completionBlock(result)
        }
    }

    private data class BaselineHttpRouter(
        private val verifiedProfilePayload: String = VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1,
        private val verifiedProfileStatusCode: Int = 200,
        private val verifiedProfileContentType: String? = Request.ContentTypeApplicationJson,
        private val requestPayload: String = CredentialManifestMocks.CredentialManifest1,
        private val requestStatusCode: Int = 200,
        private val requestContentType: String? = Request.ContentTypeApplicationJson,
        private val requestFailure: Exception? = null,
        private val didDocumentPayload: String = DidDocumentMocks.DidDocumentMockStr,
        private val didDocumentStatusCode: Int = 200,
        private val didDocumentContentType: String? = Request.ContentTypeApplicationJson,
    ) {
        val requestedEndpoints: MutableList<String> = Collections.synchronizedList(mutableListOf())

        fun connectionFor(request: Request): HttpURLConnection {
            requestedEndpoints.add(request.endpoint)
            if (isSdkRequestEndpoint(request.endpoint)) {
                requestFailure?.let { throw it }
                if (request.endpoint.startsWith("not-a-url")) {
                    throw MalformedURLException("no protocol: ${request.endpoint}")
                }
                if (request.endpoint.startsWith("ftp://")) {
                    throw MalformedURLException("unknown protocol: ftp")
                }
            }
            val route = routeFor(request.endpoint)
            return FakeHttpURLConnection(
                url = URL(request.endpoint),
                responseCodeValue = route.statusCode,
                contentTypeValue = route.contentType,
                payload = route.payload,
            )
        }

        private fun routeFor(endpoint: String): Route =
            when {
                endpoint.contains("/reference/countries") ->
                    Route(200, Request.ContentTypeApplicationJson, CountriesMocks.CountriesJson)
                endpoint.contains("/api/v0.6/credential-types") ->
                    Route(200, Request.ContentTypeApplicationJson, CredentialTypesMocks.CredentialTypesJson)
                endpoint.contains("/schemas/") ->
                    Route(200, Request.ContentTypeApplicationJson, CredentialTypeSchemaMocks.CredentialTypeSchemaJson)
                endpoint.contains("/verified-profile") ->
                    Route(verifiedProfileStatusCode, verifiedProfileContentType, verifiedProfilePayload)
                endpoint.contains("/resolve-did/") ->
                    Route(didDocumentStatusCode, didDocumentContentType, didDocumentPayload)
                isSdkRequestEndpoint(endpoint) ->
                    Route(requestStatusCode, requestContentType, requestPayload)
                else ->
                    error("Unhandled HTTP endpoint in baseline test: $endpoint")
            }

        private fun isSdkRequestEndpoint(endpoint: String): Boolean =
            endpoint.contains("get-credential-manifest") ||
                endpoint.contains("get-presentation-request") ||
                endpoint.startsWith("not-a-url") ||
                endpoint.startsWith("ftp://")
    }

    private data class Route(
        val statusCode: Int,
        val contentType: String?,
        val payload: String,
    )

    private class FakeHttpURLConnection(
        url: URL,
        private val responseCodeValue: Int,
        private val contentTypeValue: String?,
        private val payload: String,
    ) : HttpURLConnection(url) {
        override fun connect() = Unit

        override fun disconnect() = Unit

        override fun usingProxy() = false

        override fun getResponseCode(): Int = responseCodeValue

        override fun getContentType(): String? = contentTypeValue

        override fun getErrorStream(): InputStream? =
            if (responseCodeValue >= HTTP_OK && responseCodeValue <= 299) {
                null
            } else {
                ByteArrayInputStream(payload.toByteArray())
            }

        override fun getInputStream(): InputStream =
            ByteArrayInputStream(payload.toByteArray())
    }

    private companion object {
        val cacheSequence = AtomicInteger(1000)
    }
}
