/**
 * Created by Michael Avoyan on 02/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.jwt.local

import android.os.Build
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.api.jwt.VCLJwtSignService
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class JwtSignServiceLocalTest {
    private lateinit var subject: VCLJwtSignService

    private lateinit var didJwk: VCLDidJwk
    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)

    private val payloadMock = "{\"key1\":\"value1\",\"key2\":\"value2\"}".toJsonObject()
    private val jtiMock = "some jti"
    private val issMock = "some iss"
    private val audMock = "some sud"
    private val nonceMock = "some nonce"

    @Before
    fun setUp() {
        keyService.generateDidJwk(null) { jwkResult ->
            jwkResult.handleResult({
                didJwk = it
            } ,{
                assert(false) { "Failed to generate did jwk" }
            })
        }
        subject = VCLJwtSignServiceLocalImpl(keyService)
    }
    @Test
    fun testSignFullParams() {
        subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                payload = payloadMock,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            ),
            nonce = nonceMock,
            didJwk = didJwk
            ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid == didJwk.kid)

                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIss) == issMock)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyAud) == audMock)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyJti) == jtiMock)
                val iat = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIat) as Long
                val nbf = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNbf) as Long
//                val exp = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyExp) as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNonce) == nonceMock)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }

    @Test
    fun testSignPartialParams1() {
        subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                payload = payloadMock,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            ),
            nonce = nonceMock,
            didJwk = didJwk
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid?.isBlank() == false)

                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIss) == issMock)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyAud) == audMock)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyJti) == jtiMock)
                val iat = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIat) as Long
                val nbf = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNbf) as Long
//                val exp = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyExp) as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNonce) == nonceMock)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }

    @Test
    fun testSignPartialParams2() {
        subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                payload = payloadMock,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            ),
            didJwk = didJwk
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid?.isBlank() == false)

                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIss) == issMock)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyAud) == audMock)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyJti) == jtiMock)
                val iat = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIat) as Long
                val nbf = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNbf) as Long
//                val exp = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyExp) as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNonce) == null)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }

    @Test
    fun testSignPartialParams3() {
        subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                payload = payloadMock,
                iss = issMock,
                aud = audMock
            ),
            didJwk = didJwk
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid?.isBlank() == false)

                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIss) == issMock)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyAud) == audMock)
                assert((jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyJti) as? String)?.isBlank() == false)
                val iat = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIat) as Long
                val nbf = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNbf) as Long
//                val exp = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyExp) as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNonce) == null)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }

    @Test
    fun testSignPartialParams4() {
        subject.sign(
            jwtDescriptor = VCLJwtDescriptor(
                payload = payloadMock,
                iss = issMock
            ),
            didJwk = didJwk
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid?.isBlank() == false)

                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIss) == issMock)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyAud) == null)
                assert((jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyJti) as? String)?.isBlank() == false)
                val iat = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIat) as Long
                val nbf = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNbf) as Long
//                val exp = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyExp) as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNonce) == null)
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
            ),
            didJwk = didJwk
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid?.isBlank() == false)

                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIss) == issMock)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyAud) == null)
                assert((jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyJti) as? String)?.isBlank() == false)
                val iat = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyIat) as Long
                val nbf = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNbf) as Long
//                val exp = jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyExp) as Long
                assert(iat == nbf)
//        assert(exp - iat == sevenDaysInSeconds)
                assert(jwt.payload?.toJSONObject()?.get(VCLJwtSignServiceLocalImpl.CodingKeys.KeyNonce) == null)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }
}