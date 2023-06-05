/**
 * Created by Michael Avoyan on 01/06/2023.
 */

package io.velocitycareerlabs.infrastructure.keys

import android.os.Build
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.impl.data.infrastructure.keys.KeyServiceImpl
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

    internal lateinit var subject: KeyServiceImpl

    @Before
    fun setUp() {
        subject = KeyServiceImpl(SecretStoreServiceMock.Instance)
    }

    @Test
    fun testGenerateDidJwk() {
        val didJwk = subject.generateDidJwk()

        val jwkJson =
            didJwk.value.removePrefix(VCLDidJwk.DidJwkPrefix).decodeBase64().toJsonObject()

        assert(didJwk.value.startsWith(VCLDidJwk.DidJwkPrefix))
        assert(didJwk.kid.startsWith(VCLDidJwk.DidJwkPrefix))
        assert(didJwk.kid.endsWith(VCLDidJwk.DidJwkSuffix))

        assert(jwkJson?.optString("kty") == "EC")
        assert(jwkJson?.optString("use") == "sig")
        assert(jwkJson?.optString("crv") == "secp256k1")
//        assert(jwkJson?.optString("alg") == "ES256K")
        assert(jwkJson?.optString("use") == "sig")
        assert(jwkJson?.optString("x") != null)
        assert(jwkJson?.optString("y") != null)
    }
}