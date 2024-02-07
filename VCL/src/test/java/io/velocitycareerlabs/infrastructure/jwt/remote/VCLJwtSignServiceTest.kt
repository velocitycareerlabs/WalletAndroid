/**
 * Created by Michael Avoyan on 01/11/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.jwt.remote

import android.os.Build
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.remote.VCLJwtSignServiceRemoteImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import junit.framework.TestCase.assertEquals
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class VCLJwtSignServiceTest {
    lateinit var subject: VCLJwtSignServiceRemoteImpl
    private lateinit var didJwk: VCLDidJwk
    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)

    @Before
    fun setUp() {
        keyService.generateDidJwk(null) { jwkResult ->
            jwkResult.handleResult({
                didJwk = it
            } ,{
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }

        subject = VCLJwtSignServiceRemoteImpl(
            NetworkServiceSuccess(""),
            ""
        )
    }

    @Test
    fun testGenerateJwtPayloadToSign() {
        val payloadToSign = subject.generateJwtPayloadToSign(
            didJwk = didJwk,
            nonce = "nonce 1",
            VCLJwtDescriptor(
                payload = "{\"payload\": \"payload 1\"}".toJsonObject(),
                jti = "jti 1",
                iss = "iss 1",
                aud = "aud 1"
            )
        )
        val header = payloadToSign.optJSONObject("header")
        val options = payloadToSign.optJSONObject("options")
        val payload = payloadToSign.optJSONObject("payload")

        assert(header?.optString("kid") == didJwk.kid)
        assertEquals(header?.optJSONObject("jwk"), didJwk.publicJwk.valueJson)

        assert(options?.optString("keyId") == didJwk.keyId)

        assert(payload?.optString("nonce") == "nonce 1")
        assert(payload?.optString("aud") == "aud 1")
        assert(payload?.optString("iss") == "iss 1")
        assert(payload?.optString("jti") == "jti 1")
    }
}