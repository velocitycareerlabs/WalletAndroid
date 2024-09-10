/**
 * Created by Michael Avoyan on 01/11/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.jwt.remote

import android.os.Build
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.remote.VCLJwtSignServiceRemoteImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class VCLJwtSignServiceTest {
    lateinit var subject: VCLJwtSignServiceRemoteImpl
    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)

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
            VCLJwtDescriptor(
                payload = "{\"payload\": \"payload 1\"}".toJsonObject(),
                jti = "jti 1",
                iss = "iss 1",
                aud = "aud 1"
            ),
            nonce = "nonce 1",
            didJwk = DidJwkMocks.DidJwk,
            )
        val header = payloadToSign.optJSONObject("header")
        val options = payloadToSign.optJSONObject("options")
        val payload = payloadToSign.optJSONObject("payload")

        assert(header?.optString("kid") == DidJwkMocks.DidJwk.kid)
        assertEquals(header?.optJSONObject("jwk"), DidJwkMocks.DidJwk.publicJwk.valueJson)

        assert(options?.optString("keyId") == DidJwkMocks.DidJwk.keyId)

        assert(payload?.optString("nonce") == "nonce 1")
        assert(payload?.optString("aud") == "aud 1")
        assert(payload?.optString("iss") == "iss 1")
        assert(payload?.optString("jti") == "jti 1")
    }
}