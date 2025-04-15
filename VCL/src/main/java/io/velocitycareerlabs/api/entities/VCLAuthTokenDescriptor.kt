/**
 * Created by Michael Avoyan on 09/04/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject

enum class GrantType(val value: String) {
    AuthorizationCode("authorization_code"),
    RefreshToken("refresh_token")
}

class VCLAuthTokenDescriptor {
    val authTokenUri: String
    val refreshToken: String?
    val walletDid: String?
    val relyingPartyDid: String?
    val vendorOriginContext: String?

    constructor(
        authTokenUri: String,
        refreshToken: String? = null,
        walletDid: String? = null,
        relyingPartyDid: String? = null,
        vendorOriginContext: String? = null
    ) {
        this.authTokenUri = authTokenUri
        this.refreshToken = refreshToken
        this.walletDid = walletDid
        this.relyingPartyDid = relyingPartyDid
        this.vendorOriginContext = vendorOriginContext
    }

    constructor(
        presentationRequest: VCLPresentationRequest,
        refreshToken: String? = null
    ) {
        this.authTokenUri = presentationRequest.authTokenUri
        this.refreshToken = refreshToken
        this.walletDid = presentationRequest.didJwk.did
        this.relyingPartyDid = presentationRequest.iss
        this.vendorOriginContext = presentationRequest.vendorOriginContext
    }

    fun generateRequestBody(): JSONObject {
        return (if (refreshToken != null) {
            mapOf(
                KeyGrantType to GrantType.RefreshToken.value,
                KeyClientId to walletDid,
                GrantType.RefreshToken.value to refreshToken,
                KeyAudience to relyingPartyDid
            )
        } else {
            mapOf(
                KeyGrantType to GrantType.AuthorizationCode.value,
                KeyClientId to walletDid,
                GrantType.AuthorizationCode.value to vendorOriginContext,
                KeyAudience to relyingPartyDid,
                KeyTokenType to KeyTokenTypeValue
            )
        }).toJsonObject()
    }

    companion object CodingKeys {
        const val KeyClientId = "client_id"
        const val KeyAudience = "audience"
        const val KeyGrantType = "grant_type"
        const val KeyTokenType = "token_type"
        const val KeyTokenTypeValue = "Bearer"
    }
}
