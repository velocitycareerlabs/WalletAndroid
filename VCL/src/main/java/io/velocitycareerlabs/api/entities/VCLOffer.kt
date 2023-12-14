/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.api.entities

import org.json.JSONObject

data class VCLOffer(val payload: JSONObject) {
    val issuerId: String get() = payload.optJSONObject(CodingKeys.KeyIssuer)?.optString(KeyId)
        ?: payload.optString(CodingKeys.KeyIssuer)
        ?: ""
    val id: String get() = payload.optString(CodingKeys.KeyId) ?: ""

    companion object CodingKeys {
        const val KeyId = "id"
        const val KeyDid = "did"
        const val KeyIssuer = "issuer"
    }
}
