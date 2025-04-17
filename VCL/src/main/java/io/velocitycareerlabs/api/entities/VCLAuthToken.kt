/**
 * Created by Michael Avoyan on 09/04/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import org.json.JSONObject

data class VCLAuthToken(
    val payload: JSONObject,
    var authTokenUri: String? = null,
    var walletDid: String? = null,
    var relyingPartyDid: String? = null
) {
    val accessToken: VCLToken = VCLToken(payload.optString(KeyAccessToken))
    val refreshToken: VCLToken = VCLToken(payload.optString(KeyRefreshToken))
    val tokenType: String = payload.optString(KeyTokenType)

    init {
        authTokenUri = authTokenUri ?: payload.optString(KeyAuthTokenUri)
        walletDid = walletDid ?: payload.optString(KeyWalletDid)
        relyingPartyDid = relyingPartyDid ?: payload.optString(KeyRelyingPartyDid)
    }

    companion object CodingKeys {
        const val KeyAccessToken = "access_token"
        const val KeyRefreshToken = "refresh_token"
        const val KeyTokenType = "token_type"
        const val KeyAuthTokenUri = "authTokenUri"
        const val KeyWalletDid = "walletDid"
        const val KeyRelyingPartyDid = "relyingPartyDid"
    }
}
