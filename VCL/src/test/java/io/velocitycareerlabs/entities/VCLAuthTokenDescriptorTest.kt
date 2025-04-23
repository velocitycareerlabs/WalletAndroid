/**
 * Created by Michael Avoyan on 09/04/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.GrantType
import io.velocitycareerlabs.api.entities.VCLAuthTokenDescriptor
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.TokenMocks
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

class VCLAuthTokenDescriptorTest {

    companion object {
        const val authTokenUri =
            "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:web:devregistrar.velocitynetwork.foundation:d:example-21.com-8b82ce9a/oauth/token"
        const val walletDid =
            "did:jwk:eyJrdHkiOiJFQyIsInVzZSI6InNpZyIsImNydiI6InNlY3AyNTZrMSIsImtpZCI6IjNkODdhZGFmLWQ0ZmEtNDBkZS1iNGYzLTExNGFhOGZmOTMyOCIsIngiOiJvZThGN1ZPWmtOZGpnUTNLdHVmenlwRjBkTWh2QjZVanpYQVRVQ1d2NlRjIiwieSI6IjRQNFZJRFJtYWM2ZlJFY0hkR2tDdVRqdDJMSnNoYVZ2WWpjMGVVZEdpaHcifQ"
        const val relyingPartyDid =
            "did:web:devregistrar.velocitynetwork.foundation:d:example-21.com-8b82ce9a"
        const val authorizationCode = "authorization code"
        val refreshToken = VCLToken(TokenMocks.TokenJwt1)
    }

    @Test
    fun testInitWithPresentationRequestAndOptionalParameters() {
        val descriptor = VCLAuthTokenDescriptor(
            presentationRequest = PresentationRequestMocks.PresentationRequestFeed,
            refreshToken = refreshToken
        )

        assertEquals(authTokenUri, descriptor.authTokenUri)
        assertEquals(walletDid, descriptor.walletDid)
        assertEquals(relyingPartyDid, descriptor.relyingPartyDid)
        assertNull(descriptor.authorizationCode)
        assertEquals(refreshToken.value, descriptor.refreshToken?.value)
    }

    @Test
    fun testInitWithPresentationRequestOnly() {
        val descriptor = VCLAuthTokenDescriptor(PresentationRequestMocks.PresentationRequestFeed)

        assertEquals(authTokenUri, descriptor.authTokenUri)
        assertEquals(walletDid, descriptor.walletDid)
        assertEquals(relyingPartyDid, descriptor.relyingPartyDid)
        assertNull(descriptor.authorizationCode)
        assertNull(descriptor.refreshToken)
    }

    @Test
    fun testInitWithAllManualParams() {
        val descriptor = VCLAuthTokenDescriptor(
            authTokenUri,
            refreshToken,
            walletDid,
            relyingPartyDid,
            authorizationCode
        )

        assertEquals(authTokenUri, descriptor.authTokenUri)
        assertEquals(walletDid, descriptor.walletDid)
        assertEquals(relyingPartyDid, descriptor.relyingPartyDid)
        assertEquals(authorizationCode, descriptor.authorizationCode)
        assertEquals(refreshToken.value, descriptor.refreshToken?.value)
    }

    @Test
    fun testGenerateRequestBodyForRefreshTokenOnly() {
        val descriptor = VCLAuthTokenDescriptor(
            authTokenUri,
            refreshToken,
            walletDid,
            relyingPartyDid,
            null
        )

        val expected = mapOf(
            VCLAuthTokenDescriptor.KeyGrantType to GrantType.RefreshToken.value,
            VCLAuthTokenDescriptor.KeyClientId to walletDid,
            GrantType.RefreshToken.value to refreshToken.value,
            VCLAuthTokenDescriptor.KeyAudience to relyingPartyDid
        ).toJsonObject()

        JSONAssert.assertEquals(expected, descriptor.generateRequestBody(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testGenerateRequestBodyForVendorOriginContextOnly() {
        val descriptor = VCLAuthTokenDescriptor(
            authTokenUri,
            null,
            walletDid,
            relyingPartyDid,
            authorizationCode
        )

        val expected = mapOf(
            VCLAuthTokenDescriptor.KeyGrantType to GrantType.AuthorizationCode.value,
            VCLAuthTokenDescriptor.KeyClientId to walletDid,
            GrantType.AuthorizationCode.value to authorizationCode,
            VCLAuthTokenDescriptor.KeyAudience to relyingPartyDid,
            VCLAuthTokenDescriptor.KeyTokenType to VCLAuthTokenDescriptor.KeyTokenTypeValue
        ).toJsonObject()

        JSONAssert.assertEquals(expected, descriptor.generateRequestBody(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testGenerateRequestBodyWithBothRefreshTokenAndVendorOriginContext() {
        val descriptor = VCLAuthTokenDescriptor(
            authTokenUri,
            refreshToken,
            walletDid,
            relyingPartyDid,
            authorizationCode
        )

        val expected = mapOf(
            VCLAuthTokenDescriptor.KeyGrantType to GrantType.RefreshToken.value,
            VCLAuthTokenDescriptor.KeyClientId to walletDid,
            GrantType.RefreshToken.value to refreshToken.value,
            VCLAuthTokenDescriptor.KeyAudience to relyingPartyDid
        ).toJsonObject()

        JSONAssert.assertEquals(expected, descriptor.generateRequestBody(), JSONCompareMode.LENIENT)
    }
}
