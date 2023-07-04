/**
 * Created by Michael Avoyan on 01/06/2023.
 */

package io.velocitycareerlabs.infrastructure.jwt

import android.os.Build
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.keys.KeyServiceImpl
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class JwtServiceTest {

    private lateinit var subject: JwtServiceImpl

    private lateinit var didJwk: VCLDidJwk
    private val keyService = KeyServiceImpl(SecretStoreServiceMock.Instance)

    private val payloadMock = "{\"key1\":\"value1\",\"key2\":\"value2\"}".toJsonObject()
    private val jtiMock = "some jti"
    private val issMock = "some iss"
    private val audMock = "some sud"
    private val nonceMock = "some nonce"

    private val sevenDaysInSeconds = TimeUnit.DAYS.toMillis(7)

    @Before
    fun setUp() {
        keyService.generateDidJwk { jwkResult ->
            jwkResult.handleResult({
                didJwk = it
            } ,{
                assert(false) { "Failed to generate did jwk" }
            })
        }
        subject = JwtServiceImpl(keyService)
    }

    @Test
    fun testSignFullParams() {
        subject.sign(
            kid = didJwk.kid,
            nonce = nonceMock,
            jwtDescriptor = VCLJwtDescriptor(
                keyId = didJwk.keyId,
                payload = payloadMock,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            )
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid == didJwk.kid)

                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIss] == issMock)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyAud] == audMock)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyJti] == jtiMock)
                val iat = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIat] as Long
                val nbf = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNbf] as Long
                val exp = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyExp] as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNonce] == nonceMock)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }

    @Test
    fun testSignPartParams1() {
        subject.sign(
            nonce = nonceMock,
            jwtDescriptor = VCLJwtDescriptor(
                keyId = didJwk.keyId,
                payload = payloadMock,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            )
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid?.isBlank() == false)

                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIss] == issMock)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyAud] == audMock)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyJti] == jtiMock)
                val iat = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIat] as Long
                val nbf = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNbf] as Long
                val exp = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyExp] as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNonce] == nonceMock)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }

    @Test
    fun testSignPartParams2() {
        subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                keyId = didJwk.keyId,
                payload = payloadMock,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            )
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid?.isBlank() == false)

                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIss] == issMock)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyAud] == audMock)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyJti] == jtiMock)
                val iat = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIat] as Long
                val nbf = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNbf] as Long
                val exp = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyExp] as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNonce] == null)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }

    @Test
    fun testSignPartParams3() {
        subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                payload = payloadMock,
                iss = issMock,
                aud = audMock
            )
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid?.isBlank() == false)

                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIss] == issMock)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyAud] == audMock)
                assert((jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyJti] as? String)?.isBlank() == false)
                val iat = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIat] as Long
                val nbf = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNbf] as Long
                val exp = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyExp] as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNonce] == null)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }

    @Test
    fun testSignPartParams4() {
        subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                payload = payloadMock,
                iss = issMock
            )
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid?.isBlank() == false)

                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIss] == issMock)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyAud] == null)
                assert((jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyJti] as? String)?.isBlank() == false)
                val iat = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIat] as Long
                val nbf = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNbf] as Long
                val exp = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyExp] as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNonce] == null)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }

    @Test
    fun testSignPartParams5() {
        subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                iss = issMock
            )
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid?.isBlank() == false)

                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIss] == issMock)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyAud] == null)
                assert((jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyJti] as? String)?.isBlank() == false)
                val iat = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyIat] as Long
                val nbf = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNbf] as Long
                val exp = jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyExp] as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload.toJSONObject()[JwtServiceImpl.CodingKeys.KeyNonce] == null)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }
}