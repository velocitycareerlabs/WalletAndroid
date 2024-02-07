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
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.api.jwt.VCLJwtSignService
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class JwtVerifyServiceLocalTest {
    private val subject = VCLJwtVerifyServiceLocalImpl()

    private lateinit var jwtSignServiceLocalImpl: VCLJwtSignService
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
        jwtSignServiceLocalImpl = VCLJwtSignServiceLocalImpl(keyService)
    }

    @Test
    fun testSignAndVerify() {
        jwtSignServiceLocalImpl.sign(
            didJwk = didJwk,
            nonce = nonceMock,
            jwtDescriptor = VCLJwtDescriptor(
                payload = payloadMock,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            )
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                assert(jwt.kid == didJwk.kid)

                subject.verify(jwt, VCLPublicJwk(valueStr = didJwk.publicJwk.valueStr)) {
                    it.handleResult({ verified ->
                        assert(verified)  { "failed to verify jwt: $verified" }
                    }, {
                        assert(false) { "failed to verify jwt: $it" }
                    })
                }

            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }
}