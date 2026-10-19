/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.integration

import android.os.Build
import com.nimbusds.jose.jwk.ECKey
import io.velocitycareerlabs.api.VCLCryptoServiceType
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLDidJwkDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.initialization.VCLCryptoServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLInjectedCryptoServicesDescriptor
import io.velocitycareerlabs.impl.VCLImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.resources.valid.SignedRequestMocks
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.security.Security
import java.util.concurrent.CountDownLatch

/**
 * Exercises ES256 (P-256) and ES256K (secp256k1) organization keys through the public VCL entry points,
 * with the SDK's local JWT services doing the cryptography.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class SignatureAlgorithmIntegrationTest {

    @Test
    fun es256KRequestsDoNotVerifyAgainstDifferentSecp256k1Key() = withSecp256k1Provider {
        assertRequestsDoNotVerifyAgainstDifferentKeyOnSameCurve(VCLSignatureAlgorithm.SECP256k1)
    }

    @Test
    fun es256KRequestsFailWhenDidDocumentKeyIsP256() = withSecp256k1Provider {
        assertRequestsFailWhenDidDocumentKeyIsOnOtherCurve(
            signatureAlgorithm = VCLSignatureAlgorithm.SECP256k1,
            didDocumentAlgorithm = VCLSignatureAlgorithm.ES256,
        )
    }

    @Test
    fun es256KRequestsVerifyAgainstMatchingSecp256k1DidDocumentKey() = withSecp256k1Provider {
        assertRequestsVerifyAgainstMatchingDidDocumentKey(VCLSignatureAlgorithm.SECP256k1)
    }

    @Test
    fun es256RequestsDoNotVerifyAgainstDifferentP256Key() {
        assertRequestsDoNotVerifyAgainstDifferentKeyOnSameCurve(VCLSignatureAlgorithm.ES256)
    }

    @Test
    fun es256RequestsFailWhenDidDocumentKeyIsSecp256k1() = withSecp256k1Provider {
        assertRequestsFailWhenDidDocumentKeyIsOnOtherCurve(
            signatureAlgorithm = VCLSignatureAlgorithm.ES256,
            didDocumentAlgorithm = VCLSignatureAlgorithm.SECP256k1,
        )
    }

    @Test
    fun es256RequestsVerifyAgainstMatchingP256DidDocumentKey() {
        assertRequestsVerifyAgainstMatchingDidDocumentKey(VCLSignatureAlgorithm.ES256)
    }

    @Test
    fun localP256DidJwkSignsES256() {
        assertLocalDidJwkSigns(VCLSignatureAlgorithm.ES256)
    }

    @Test
    fun localSecp256k1DidJwkSignsES256K() = withSecp256k1Provider {
        assertLocalDidJwkSigns(VCLSignatureAlgorithm.SECP256k1)
    }

    @Test
    fun localSecp256k1DidJwkSignsES256KWithoutPublicJwkCurve() = withSecp256k1Provider {
        val vcl = initializedVcl(BaselineHttpRouter(), cryptoServicesDescriptor = localCryptoServicesWithTestSecretStore())
        val didJwk = vcl.awaitDidJwk(VCLSignatureAlgorithm.SECP256k1)

        val jwt = vcl.awaitSignedJwt(didJwk.copy(publicJwk = VCLPublicJwk(valueStr = "{}")))

        assertEquals(VCLSignatureAlgorithm.SECP256k1.jwsAlgorithm, jwt.header?.algorithm)
        assertTrue(vcl.awaitJwtVerification(jwt, didJwk.publicJwk))
    }

    /**
     * SunEC dropped secp256k1 in JDK 16, so the JVM running these tests needs BouncyCastle as the
     * preferred provider for the SDK's ES256K signing and verification. Any provider already registered
     * under the BouncyCastle name is restored to its original position afterwards.
     */
    private fun withSecp256k1Provider(block: () -> Unit) {
        val providerName = BouncyCastleProvider.PROVIDER_NAME
        val registeredProvider = Security.getProvider(providerName)
        val registeredPosition = Security.getProviders().indexOf(registeredProvider) + 1
        Security.removeProvider(providerName)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
        try {
            block()
        } finally {
            Security.removeProvider(providerName)
            registeredProvider?.let { Security.insertProviderAt(it, registeredPosition) }
        }
    }

    private fun assertRequestsDoNotVerifyAgainstDifferentKeyOnSameCurve(signatureAlgorithm: VCLSignatureAlgorithm) {
        entryPoints.forEach { entryPoint ->
            val requestJwt = VCLJwt(
                encodedJwt = entryPoint.signedRequestJwt(SignedRequestMocks.generateKey(signatureAlgorithm), signatureAlgorithm)
            )
            val otherDidDocument = VCLDidDocument(
                SignedRequestMocks.didDocument(SignedRequestMocks.generateKey(signatureAlgorithm))
            )
            val vcl = initializedVcl(BaselineHttpRouter(), cryptoServicesDescriptor = VCLCryptoServicesDescriptor())

            assertFalse(
                "$entryPoint ${signatureAlgorithm.jwsAlgorithm} request should not verify against another key",
                vcl.awaitJwtVerification(requestJwt, otherDidDocument.publicJwkFor(requestJwt)),
            )
        }
    }

    private fun EntryPoint.signedRequestJwt(signingKey: ECKey, signatureAlgorithm: VCLSignatureAlgorithm): String =
        when (this) {
            EntryPoint.Issuing -> SignedRequestMocks.credentialManifestJwt(signingKey, signatureAlgorithm)
            EntryPoint.Presentation -> SignedRequestMocks.presentationRequestJwt(signingKey, signatureAlgorithm)
        }

    private fun VCLImpl.awaitJwtVerification(jwt: VCLJwt, publicJwk: VCLPublicJwk): Boolean {
        var verified: Boolean? = null
        var error: VCLError? = null
        val latch = CountDownLatch(1)
        verifyJwt(
            jwt = jwt,
            publicJwk = publicJwk,
            successHandler = {
                verified = it
                latch.countDown()
            },
            errorHandler = {
                error = it
                latch.countDown()
            },
        )
        drainMainThreadUntil(latch)
        assertNull("JWT verification error: ${error?.toJsonObject()}", error)
        return verified ?: error("verifyJwt did not invoke successHandler")
    }

    private fun VCLDidDocument.publicJwkFor(jwt: VCLJwt): VCLPublicJwk =
        getPublicJwk(jwt.kid.orEmpty()) ?: error("DID document has no key for kid ${jwt.kid}")

    private fun assertRequestsFailWhenDidDocumentKeyIsOnOtherCurve(
        signatureAlgorithm: VCLSignatureAlgorithm,
        didDocumentAlgorithm: VCLSignatureAlgorithm,
    ) {
        entryPoints.forEach { entryPoint ->
            val requestJwt = entryPoint.signedRequestJwt(SignedRequestMocks.generateKey(signatureAlgorithm), signatureAlgorithm)
            val didDocument = SignedRequestMocks.didDocument(SignedRequestMocks.generateKey(didDocumentAlgorithm))
            val vcl = initializedVcl(
                entryPoint.routerFor(requestJwt, didDocument),
                cryptoServicesDescriptor = VCLCryptoServicesDescriptor(),
            )

            val error = entryPoint.awaitRequestError(vcl)

            assertEquals(entryPoint.requestInvalidErrorCode, error.errorCode)
            assertEquals("request_validation", error.validationPhase)
            assertEquals(entryPoint.requestKind, error.requestKind)
            assertTrue(
                "Unexpected failure: ${error.toJsonObject()}",
                error.message.orEmpty().contains("Unsupported JWS algorithm ${signatureAlgorithm.jwsAlgorithm}"),
            )
        }
    }

    private fun EntryPoint.routerFor(requestJwt: String, didDocument: String): BaselineHttpRouter =
        defaultRouter(this).copy(
            requestPayload = requestPayloadForJwt(requestJwt),
            didDocumentPayload = didDocument,
        )

    private fun EntryPoint.awaitRequestError(vcl: VCLImpl): VCLError =
        when (this) {
            EntryPoint.Issuing -> awaitCredentialManifestError(vcl, credentialManifestDescriptor(defaultDeepLink))
            EntryPoint.Presentation -> awaitPresentationRequestError(vcl, presentationDescriptor(defaultDeepLink))
        }

    private fun assertRequestsVerifyAgainstMatchingDidDocumentKey(signatureAlgorithm: VCLSignatureAlgorithm) {
        entryPoints.forEach { entryPoint ->
            val signingKey = SignedRequestMocks.generateKey(signatureAlgorithm)
            val didDocument = SignedRequestMocks.didDocument(signingKey)
            val requestJwt = entryPoint.signedRequestJwt(signingKey, signatureAlgorithm)
            val vcl = initializedVcl(
                entryPoint.routerFor(requestJwt, didDocument),
                cryptoServicesDescriptor = VCLCryptoServicesDescriptor(),
            )

            val fetchedJwt = entryPoint.awaitRequestJwt(vcl)

            assertEquals(requestJwt, fetchedJwt.encodedJwt)
            assertEquals(signatureAlgorithm.jwsAlgorithm, fetchedJwt.header?.algorithm)
            assertTrue(
                "$entryPoint ${signatureAlgorithm.jwsAlgorithm} request should verify against its DID document key",
                vcl.awaitJwtVerification(fetchedJwt, VCLDidDocument(didDocument).publicJwkFor(fetchedJwt)),
            )
        }
    }

    private fun EntryPoint.awaitRequestJwt(vcl: VCLImpl): VCLJwt =
        when (this) {
            EntryPoint.Issuing -> awaitCredentialManifest(vcl, credentialManifestDescriptor(defaultDeepLink)).jwt
            EntryPoint.Presentation -> awaitPresentationRequest(vcl, presentationDescriptor(defaultDeepLink)).jwt
        }

    private fun assertLocalDidJwkSigns(signatureAlgorithm: VCLSignatureAlgorithm) {
        val vcl = initializedVcl(BaselineHttpRouter(), cryptoServicesDescriptor = localCryptoServicesWithTestSecretStore())
        val didJwk = vcl.awaitDidJwk(signatureAlgorithm)

        val jwt = vcl.awaitSignedJwt(didJwk)

        assertEquals(signatureAlgorithm.value, didJwk.curve)
        assertEquals(signatureAlgorithm.jwsAlgorithm, jwt.header?.algorithm)
        assertTrue(vcl.awaitJwtVerification(jwt, didJwk.publicJwk))
    }

    /**
     * The SDK's local key, signing and verification services, with an in-memory secret store
     * in place of EncryptedSharedPreferences, which Robolectric cannot back with the Android keystore.
     */
    private fun localCryptoServicesWithTestSecretStore(): VCLCryptoServicesDescriptor {
        val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)
        return VCLCryptoServicesDescriptor(
            cryptoServiceType = VCLCryptoServiceType.Injected,
            injectedCryptoServicesDescriptor = VCLInjectedCryptoServicesDescriptor(
                keyService = keyService,
                jwtSignService = VCLJwtSignServiceLocalImpl(keyService),
                jwtVerifyService = VCLJwtVerifyServiceLocalImpl(),
            ),
        )
    }

    private fun VCLImpl.awaitDidJwk(signatureAlgorithm: VCLSignatureAlgorithm): VCLDidJwk {
        var didJwk: VCLDidJwk? = null
        var error: VCLError? = null
        val latch = CountDownLatch(1)
        generateDidJwk(
            didJwkDescriptor = VCLDidJwkDescriptor(signatureAlgorithm = signatureAlgorithm),
            successHandler = {
                didJwk = it
                latch.countDown()
            },
            errorHandler = {
                error = it
                latch.countDown()
            },
        )
        drainMainThreadUntil(latch)
        assertNull("did:jwk generation error: ${error?.toJsonObject()}", error)
        return didJwk ?: error("generateDidJwk did not invoke successHandler")
    }

    private fun VCLImpl.awaitSignedJwt(didJwk: VCLDidJwk): VCLJwt {
        var jwt: VCLJwt? = null
        var error: VCLError? = null
        val latch = CountDownLatch(1)
        generateSignedJwt(
            jwtDescriptor = VCLJwtDescriptor(iss = "iss123", jti = "jti123"),
            didJwk = didJwk,
            successHandler = {
                jwt = it
                latch.countDown()
            },
            errorHandler = {
                error = it
                latch.countDown()
            },
        )
        drainMainThreadUntil(latch)
        assertNull("JWT signing error: ${error?.toJsonObject()}", error)
        return jwt ?: error("generateSignedJwt did not invoke successHandler")
    }
}
