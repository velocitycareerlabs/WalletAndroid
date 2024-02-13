/**
 * Created by Michael Avoyan on 10/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import org.json.JSONArray
import org.json.JSONObject

data class VCLOffers (
    val payload: JSONObject,
    val all: List<VCLOffer>,
    val responseCode: Int,
    val sessionToken: VCLToken,
    val challenge: String? = null,
) {
    companion object CodingKeys {
        const val KeyOffers = "offers"
        const val KeyChallenge = "challenge"
    }
}