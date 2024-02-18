/**
 * Created by Michael Avoyan on 01/06/2023.
 */

package io.velocitycareerlabs.infrastructure.keys

import android.os.Build
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLDidJwkDescriptor
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.extensions.decodeBase64
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class KeyServiceTest {

    internal lateinit var subject: VCLKeyServiceLocalImpl

    @Before
    fun setUp() {
        subject = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)
    }

    @Test
    fun testGenerateDidJwkES256() {
        subject.generateDidJwk(VCLDidJwkDescriptor()) { didJwkResult ->
            didJwkResult.handleResult({ didJwk ->
                val jwkJson = didJwk.publicJwk.valueJson

                assert(didJwk.did.startsWith(VCLDidJwk.DidJwkPrefix))
                assert(didJwk.kid.startsWith(VCLDidJwk.DidJwkPrefix))
                assert(didJwk.kid.endsWith(VCLDidJwk.DidJwkSuffix))

                assert(jwkJson.optString("kty") == "EC")
                assert(jwkJson.optString("use") == "sig")
                assert(jwkJson.optString("crv") == VCLSignatureAlgorithm.ES256.curve.name)
                assert(jwkJson.optString("use") == "sig")
            }, {
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }
    }

    @Test
    fun testGenerateDidJwkSECP256k1() {
        subject.generateDidJwk(VCLDidJwkDescriptor(VCLSignatureAlgorithm.SECP256k1)) { didJwkResult ->
            didJwkResult.handleResult({ didJwk ->
                val jwkJson = didJwk.publicJwk.valueJson

                assert(didJwk.did.startsWith(VCLDidJwk.DidJwkPrefix))
                assert(didJwk.kid.startsWith(VCLDidJwk.DidJwkPrefix))
                assert(didJwk.kid.endsWith(VCLDidJwk.DidJwkSuffix))

                assert(jwkJson.optString("kty") == "EC")
                assert(jwkJson.optString("use") == "sig")
                assert(jwkJson.optString("crv") == VCLSignatureAlgorithm.SECP256k1.curve.name)
                assert(jwkJson.optString("use") == "sig")
            }, {
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }
    }
}