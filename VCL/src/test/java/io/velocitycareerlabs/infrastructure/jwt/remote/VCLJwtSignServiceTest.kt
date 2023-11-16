/**
 * Created by Michael Avoyan on 01/11/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.jwt.remote

import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.remote.VCLJwtSignServiceRemoteImpl
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

internal class VCLJwtSignServiceTest {
    lateinit var subject: VCLJwtSignServiceRemoteImpl

    @Before
    fun setUp() {
        subject = VCLJwtSignServiceRemoteImpl(
            NetworkServiceSuccess(""),
            ""
        )
    }

    @Test
    fun testGenerateJwtPayloadToSign() {
        val payloadToSign = subject.generateJwtPayloadToSign(
            kid = "kid 1",
            nonce = "nonce 1",
            VCLJwtDescriptor(
                keyId = "keyId 1",
                payload = "{\"payload\": \"payload 1\"}".toJsonObject(),
                jti = "jti 1",
                iss = "iss 1",
                aud = "aud 1"
            )
        )
        val header = payloadToSign.optJSONObject("header")
        val options = payloadToSign.optJSONObject("options")
        val payload = payloadToSign.optJSONObject("payload")

        assert(header?.optString("kid") == "kid 1")

        assert(options?.optString("keyId") == "keyId 1")

        assert(payload?.optString("nonce") == "nonce 1")
        assert(payload?.optString("aud") == "aud 1")
        assert(payload?.optString("iss") == "iss 1")
        assert(payload?.optString("jti") == "jti 1")
    }
}