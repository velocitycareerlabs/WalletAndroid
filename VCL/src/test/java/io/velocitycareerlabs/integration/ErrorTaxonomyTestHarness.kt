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
import io.velocitycareerlabs.api.entities.initialization.VCLErrorCodeCompatibilityMode
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

internal val entryPoints = EntryPoint.entries

internal enum class EntryPoint(
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

internal data class ErrorDiagnostics(
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

internal fun assertDiagnostics(
    expected: ErrorDiagnostics,
    actual: VCLError,
) {
    assertEquals(expected.canonicalizePayload(), actual.toDiagnostics())
}

internal fun VCLError.toDiagnostics() = ErrorDiagnostics(
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

internal fun ErrorDiagnostics.canonicalizePayload() =
    copy(payload = payload?.canonicalJsonOrSelf())

internal fun String.canonicalJsonOrSelf(): String =
    runCatching { JSONObject(this).toString() }.getOrDefault(this)

internal fun EntryPoint.expectedDiagnostics(
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

internal val EntryPoint.lastDid: String get() = "did:example:last"

internal fun simpleRequestUri(): String =
    URLEncoder.encode("https://example.com/request", "UTF-8")

internal val EntryPoint.defaultRequestJwt: String
    get() = when (this) {
        EntryPoint.Issuing -> CredentialManifestMocks.JwtCredentialManifest1
        EntryPoint.Presentation -> PresentationRequestMocks.EncodedPresentationRequest
    }

internal fun EntryPoint.requestPayloadForJwt(encodedJwt: String): String =
    when (this) {
        EntryPoint.Issuing -> JSONObject().put(VCLCredentialManifest.KeyIssuingRequest, encodedJwt)
        EntryPoint.Presentation -> JSONObject().put(VCLPresentationRequest.KeyPresentationRequest, encodedJwt)
    }.toString()

internal fun encodedJwtWithoutKid(encodedJwt: String): String {
    val parts = encodedJwt.split(".")
    val headerJson = JSONObject(String(Base64.getUrlDecoder().decode(parts[0])))
    headerJson.remove(VCLJwt.KeyKid)
    return encodedJwtWithHeader(encodedJwt, headerJson)
}

internal fun encodedJwtWithKid(encodedJwt: String, kid: String): String {
    val parts = encodedJwt.split(".")
    val headerJson = JSONObject(String(Base64.getUrlDecoder().decode(parts[0])))
    headerJson.put(VCLJwt.KeyKid, kid)
    return encodedJwtWithHeader(encodedJwt, headerJson)
}

internal fun encodedJwtWithHeader(encodedJwt: String, headerJson: JSONObject): String {
    val parts = encodedJwt.split(".")
    val encodedHeader = Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(headerJson.toString().toByteArray(Charsets.UTF_8))
    return listOf(encodedHeader, parts[1], parts[2]).joinToString(".")
}

internal fun defaultRouter(entryPoint: EntryPoint): BaselineHttpRouter =
    when (entryPoint) {
        EntryPoint.Issuing -> BaselineHttpRouter()
        EntryPoint.Presentation -> BaselineHttpRouter(
            verifiedProfilePayload = VerifiedProfileMocks.VerifiedProfileInspectorJsonStr,
            requestPayload = PresentationRequestMocks.EncodedPresentationRequestResponse,
        )
    }

internal fun getEntryPointError(
    entryPoint: EntryPoint,
    deepLink: VCLDeepLink = entryPoint.defaultDeepLink,
    router: BaselineHttpRouter = defaultRouter(entryPoint),
    jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
    errorCodeCompatibilityMode: VCLErrorCodeCompatibilityMode = VCLErrorCodeCompatibilityMode.Taxonomy,
): VCLError =
    when (entryPoint) {
        EntryPoint.Issuing -> getCredentialManifestError(
            deepLink,
            router,
            jwtVerificationResult,
            errorCodeCompatibilityMode,
        )
        EntryPoint.Presentation -> getPresentationRequestError(
            deepLink,
            router,
            jwtVerificationResult,
            errorCodeCompatibilityMode,
        )
    }

internal fun getLegacyEntryPointError(
    entryPoint: EntryPoint,
    deepLink: VCLDeepLink = entryPoint.defaultDeepLink,
    router: BaselineHttpRouter = defaultRouter(entryPoint),
    jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
): VCLError =
    getEntryPointError(
        entryPoint,
        deepLink,
        router,
        jwtVerificationResult,
        VCLErrorCodeCompatibilityMode.Legacy,
    )

internal fun getCredentialManifestError(
    deepLink: VCLDeepLink,
    router: BaselineHttpRouter = BaselineHttpRouter(),
    jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
    errorCodeCompatibilityMode: VCLErrorCodeCompatibilityMode = VCLErrorCodeCompatibilityMode.Taxonomy,
): VCLError {
    val vcl = initializedVcl(router, jwtVerificationResult, errorCodeCompatibilityMode)
    return awaitCredentialManifestError(vcl, credentialManifestDescriptor(deepLink))
}

internal fun getLegacyCredentialManifestError(
    deepLink: VCLDeepLink,
    router: BaselineHttpRouter = BaselineHttpRouter(),
    jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
): VCLError =
    getCredentialManifestError(
        deepLink,
        router,
        jwtVerificationResult,
        VCLErrorCodeCompatibilityMode.Legacy,
    )

internal fun getPresentationRequestError(
    deepLink: VCLDeepLink,
    router: BaselineHttpRouter = BaselineHttpRouter(
        verifiedProfilePayload = VerifiedProfileMocks.VerifiedProfileInspectorJsonStr,
        requestPayload = PresentationRequestMocks.EncodedPresentationRequestResponse,
    ),
    jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
    errorCodeCompatibilityMode: VCLErrorCodeCompatibilityMode = VCLErrorCodeCompatibilityMode.Taxonomy,
): VCLError {
    val vcl = initializedVcl(router, jwtVerificationResult, errorCodeCompatibilityMode)
    return awaitPresentationRequestError(vcl, presentationDescriptor(deepLink))
}

internal fun getLegacyPresentationRequestError(
    deepLink: VCLDeepLink,
    router: BaselineHttpRouter = BaselineHttpRouter(
        verifiedProfilePayload = VerifiedProfileMocks.VerifiedProfileInspectorJsonStr,
        requestPayload = PresentationRequestMocks.EncodedPresentationRequestResponse,
    ),
    jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
): VCLError =
    getPresentationRequestError(
        deepLink,
        router,
        jwtVerificationResult,
        VCLErrorCodeCompatibilityMode.Legacy,
    )

internal fun getCredentialManifestUseCaseVerificationFalseError(): VCLError {
    var result: VCLError? = null
    CredentialManifestUseCaseImpl(
        credentialManifestRepository = object : CredentialManifestRepository {
            override fun getCredentialManifest(
                credentialManifestDescriptor: VCLCredentialManifestDescriptor,
                completionBlock: (VCLResult<String>) -> Unit,
            ) {
                completionBlock(VCLResult.Success(CredentialManifestMocks.JwtCredentialManifest1))
            }
        },
        resolveDidDocumentRepository = fixedDidDocumentRepository(),
        jwtServiceRepository = successfulJwtServiceRepository(),
        credentialManifestByDeepLinkVerifier = object : CredentialManifestByDeepLinkVerifier {
            override fun verifyCredentialManifest(
                credentialManifest: VCLCredentialManifest,
                deepLink: VCLDeepLink?,
                didDocument: VCLDidDocument,
                completionBlock: (VCLResult<Boolean>) -> Unit,
            ) {
                completionBlock(VCLResult.Success(false))
            }
        },
        executor = EmptyExecutor(),
    ).getCredentialManifest(
        credentialManifestDescriptor = credentialManifestDescriptor(DeepLinkMocks.CredentialManifestDeepLinkDevNet),
        verifiedProfile = VCLVerifiedProfile(JSONObject()),
    ) {
        it.handleResult(
            successHandler = { fail("Credential manifest failure expected: $it") },
            errorHandler = { error -> result = error },
        )
    }
    return result ?: error("getCredentialManifest did not invoke errorHandler")
}

internal fun getPresentationRequestUseCaseVerificationFalseError(): VCLError {
    var result: VCLError? = null
    PresentationRequestUseCaseImpl(
        presentationRequestRepository = object : PresentationRequestRepository {
            override fun getPresentationRequest(
                presentationRequestDescriptor: VCLPresentationRequestDescriptor,
                completionBlock: (VCLResult<String>) -> Unit,
            ) {
                completionBlock(VCLResult.Success(PresentationRequestMocks.EncodedPresentationRequest))
            }
        },
        resolveDidDocumentRepository = fixedDidDocumentRepository(),
        jwtServiceRepository = successfulJwtServiceRepository(),
        presentationRequestByDeepLinkVerifier = object : PresentationRequestByDeepLinkVerifier {
            override fun verifyPresentationRequest(
                presentationRequest: VCLPresentationRequest,
                deepLink: VCLDeepLink,
                didDocument: VCLDidDocument,
                completionBlock: (VCLResult<Boolean>) -> Unit,
            ) {
                completionBlock(VCLResult.Success(false))
            }
        },
        executor = EmptyExecutor(),
    ).getPresentationRequest(
        presentationRequestDescriptor = presentationDescriptor(DeepLinkMocks.PresentationRequestDeepLinkDevNet),
        verifiedProfile = VCLVerifiedProfile(JSONObject()),
    ) {
        it.handleResult(
            successHandler = { fail("Presentation request failure expected: $it") },
            errorHandler = { error -> result = error },
        )
    }
    return result ?: error("getPresentationRequest did not invoke errorHandler")
}

internal fun getCredentialManifestRepositoryNullEndpointError(): VCLError {
    var result: VCLError? = null
    CredentialManifestRepositoryImpl(unusedNetworkService()).getCredentialManifest(
        credentialManifestDescriptor(VCLDeepLink("velocity-network://issue?issuerDid=${EntryPoint.Issuing.requestDid}"))
    ) {
        it.handleResult(
            successHandler = { fail("Credential manifest repository failure expected: $it") },
            errorHandler = { error -> result = error },
        )
    }
    return result ?: error("getCredentialManifest did not invoke errorHandler")
}

internal fun getPresentationRequestRepositoryNullEndpointError(): VCLError {
    var result: VCLError? = null
    PresentationRequestRepositoryImpl(unusedNetworkService()).getPresentationRequest(
        presentationDescriptor(VCLDeepLink("velocity-network://inspect?inspectorDid=${EntryPoint.Presentation.requestDid}"))
    ) {
        it.handleResult(
            successHandler = { fail("Presentation request repository failure expected: $it") },
            errorHandler = { error -> result = error },
        )
    }
    return result ?: error("getPresentationRequest did not invoke errorHandler")
}

internal fun unusedNetworkService() =
    object : NetworkService {
        override fun sendRequest(
            endpoint: String,
            body: String?,
            contentType: String,
            method: Request.HttpMethod,
            headers: List<Pair<String, String>>?,
            useCaches: Boolean,
            completionBlock: (VCLResult<Response>) -> Unit,
        ) {
            fail("Network should not be called for null endpoint")
        }
    }

internal fun fixedDidDocumentRepository() =
    object : ResolveDidDocumentRepository {
        override fun resolveDidDocument(
            did: String,
            completionBlock: (VCLResult<VCLDidDocument>) -> Unit,
        ) {
            completionBlock(VCLResult.Success(DidDocumentMocks.DidDocumentMock))
        }
    }

internal fun successfulJwtServiceRepository() =
    object : JwtServiceRepository {
        override fun decode(
            encodedJwt: String,
            completionBlock: (VCLResult<VCLJwt>) -> Unit,
        ) {
            completionBlock(VCLResult.Success(VCLJwt(encodedJwt)))
        }

        override fun verifyJwt(
            jwt: VCLJwt,
            publicJwk: VCLPublicJwk,
            remoteCryptoServicesToken: VCLToken?,
            completionBlock: (VCLResult<Boolean>) -> Unit,
        ) {
            completionBlock(VCLResult.Success(true))
        }

        override fun generateSignedJwt(
            jwtDescriptor: VCLJwtDescriptor,
            nonce: String?,
            didJwk: VCLDidJwk,
            remoteCryptoServicesToken: VCLToken?,
            completionBlock: (VCLResult<VCLJwt>) -> Unit,
        ) {
            completionBlock(VCLResult.Success(VCLJwt("")))
        }
    }

internal fun initializedVcl(
    router: BaselineHttpRouter,
    jwtVerificationResult: VCLResult<Boolean> = VCLResult.Success(true),
    errorCodeCompatibilityMode: VCLErrorCodeCompatibilityMode = VCLErrorCodeCompatibilityMode.Taxonomy,
): VCLImpl {
    val vcl = VCLImpl(router::connectionFor)
    var initError: VCLError? = null
    val latch = CountDownLatch(1)
    vcl.initialize(
        context = ApplicationProvider.getApplicationContext(),
        initializationDescriptor = VCLInitializationDescriptor(
            cacheSequence = cacheSequence.incrementAndGet(),
            errorCodeCompatibilityMode = errorCodeCompatibilityMode,
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

internal fun awaitCredentialManifestError(
    vcl: VCLImpl,
    descriptor: VCLCredentialManifestDescriptor,
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

internal fun awaitCredentialManifest(
    vcl: VCLImpl,
    descriptor: VCLCredentialManifestDescriptor,
): VCLCredentialManifest {
    var result: VCLCredentialManifest? = null
    var error: VCLError? = null
    val latch = CountDownLatch(1)
    vcl.getCredentialManifest(
        credentialManifestDescriptor = descriptor,
        successHandler = {
            result = it
            latch.countDown()
        },
        errorHandler = {
            error = it
            latch.countDown()
        },
    )
    drainMainThreadUntil(latch)
    error?.let { fail("Credential manifest success expected: $it") }
    return result ?: error("getCredentialManifest did not invoke successHandler")
}

internal fun awaitPresentationRequestError(
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

internal fun drainMainThreadUntil(latch: CountDownLatch) {
    val deadline = System.currentTimeMillis() + 5_000
    while (latch.count > 0 && System.currentTimeMillis() < deadline) {
        shadowOf(android.os.Looper.getMainLooper()).idle()
        latch.await(20, TimeUnit.MILLISECONDS)
    }
    shadowOf(android.os.Looper.getMainLooper()).idle()
    assertEquals("Timed out waiting for callback", 0, latch.count)
}

internal fun credentialManifestDescriptor(deepLink: VCLDeepLink) =
    VCLCredentialManifestDescriptorByDeepLink(
        deepLink = deepLink,
        didJwk = DidJwkMocks.DidJwk,
    )

internal fun credentialManifestDescriptorByService(
    endpoint: String? = DeepLinkMocks.CredentialManifestRequestDecodedUriStr,
    did: String = DeepLinkMocks.IssuerDid,
) =
    VCLCredentialManifestDescriptorByService(
        service = VCLService(
            JSONObject().apply {
                put(VCLService.KeyId, "${DeepLinkMocks.IssuerDid}#credential-agent-issuer-1")
                put(VCLService.KeyType, "VelocityCredentialAgentIssuer_v1.0")
                if (endpoint != null) {
                    put(VCLService.KeyServiceEndpoint, endpoint)
                }
            }
        ),
        issuingType = VCLIssuingType.Career,
        didJwk = DidJwkMocks.DidJwk,
        did = did,
    )

internal fun presentationDescriptor(deepLink: VCLDeepLink) =
    VCLPresentationRequestDescriptor(
        deepLink = deepLink,
        didJwk = DidJwkMocks.DidJwk,
    )

internal class FixedJwtVerifyService(
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

internal data class BaselineHttpRouter(
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

internal data class Route(
    val statusCode: Int,
    val contentType: String?,
    val payload: String,
)

internal class FakeHttpURLConnection(
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

private val cacheSequence = AtomicInteger(1000)

internal val EntryPoint.endpointNullMessage: String
    get() = when (this) {
        EntryPoint.Issuing -> "credentialManifestDescriptor.endpoint = null"
        EntryPoint.Presentation -> "presentationRequestDescriptor.endpoint = null"
    }

internal val EntryPoint.mismatchErrorCode: String
    get() = legacyMismatchErrorCode
