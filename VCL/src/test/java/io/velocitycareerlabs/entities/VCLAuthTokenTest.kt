/**
 * Created by Michael Avoyan on 09/04/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLAuthToken
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.resources.valid.TokenMocks
import junit.framework.TestCase.assertEquals
import org.junit.Test

class VCLAuthTokenTest {
    private val payload = mapOf(
            "access_token" to TokenMocks.TokenJwt1.encodedJwt,
            "refresh_token" to TokenMocks.TokenJwt2.encodedJwt,
            "token_type" to "Bearer",
            "authTokenUri" to "https://default.uri",
            "walletDid" to "did:wallet:default",
            "relyingPartyDid" to "did:party:default"
        ).toJsonObject()

    @Test
    fun testShouldInitializeWithOnlyPayload() {
        val token = VCLAuthToken(payload)

        assertEquals(TokenMocks.TokenJwt1.encodedJwt, token.accessToken.value)
        assertEquals(TokenMocks.TokenJwt2.encodedJwt, token.refreshToken.value)
        assertEquals("Bearer", token.tokenType)
        assertEquals("https://default.uri", token.authTokenUri)
        assertEquals("did:wallet:default", token.walletDid)
        assertEquals("did:party:default", token.relyingPartyDid)
    }

    @Test
    fun testShouldOverrideAuthTokenUriIfPassedInConstructor() {
        val token = VCLAuthToken(payload, authTokenUri = "https://override.uri")
        assertEquals("https://override.uri", token.authTokenUri)
    }

    @Test
    fun testShouldOverrideWalletDidIfPassedInConstructor() {
        val token = VCLAuthToken(payload, walletDid = "did:wallet:override")
        assertEquals("did:wallet:override", token.walletDid)
    }

    @Test
    fun testShouldOverrideRelyingPartyDidIfPassedInConstructor() {
        val token = VCLAuthToken(payload, relyingPartyDid = "did:party:override")
        assertEquals("did:party:override", token.relyingPartyDid)
    }
}