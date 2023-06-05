/**
 * Created by Michael Avoyan on 01/06/2023.
 */

package io.velocitycareerlabs.infrastructure.jwt

import android.os.Build
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.keys.KeyServiceImpl
import io.velocitycareerlabs.impl.extensions.addDays
import io.velocitycareerlabs.impl.extensions.addSeconds
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar
import java.util.Date
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
        didJwk = keyService.generateDidJwk()
        subject = JwtServiceImpl(keyService)
    }

    @Test
    fun testSignFullParams() {
        val jwt = subject.sign(
            kid = didJwk.kid,
            nonce = nonceMock,
            jwtDescriptor = VCLJwtDescriptor(
                keyId = didJwk.keyId,
                payload = payloadMock,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            )
        )
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
    }

    @Test
    fun testSignPartParams1() {
        val jwt = subject.sign(
            nonce = nonceMock,
            jwtDescriptor = VCLJwtDescriptor(
                keyId = didJwk.keyId,
                payload = payloadMock,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            )
        )
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
    }

    @Test
    fun testSignPartParams2() {
        val jwt = subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                keyId = didJwk.keyId,
                payload = payloadMock,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            )
        )
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
    }

    @Test
    fun testSignPartParams3() {
        val jwt = subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                payload = payloadMock,
                iss = issMock,
                aud = audMock
            )
        )
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
    }

    @Test
    fun testSignPartParams4() {
        val jwt = subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                payload = payloadMock,
                iss = issMock
            )
        )
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
    }

    @Test
    fun testSignPartParams5() {
        val jwt = subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                iss = issMock
            )
        )
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
    }
}