/**
 * Created by Michael Avoyan on 10/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONArray
import org.json.JSONObject

data class VCLOffers (
    val payload: JSONObject,
    val all: List<VCLOffer>,
    val responseCode: Int,
    val sessionToken: VCLToken,
    val challenge: String? = null,
) {
    companion object {
        public object CodingKeys {
            val KeyOffers = "offers"
            val KeyChallenge = "challenge"
        }

        fun fromPayload(
            payloadStr: String,
            responseCode: Int,
            sessionToken: VCLToken
        ): VCLOffers {
//        VCLXVnfProtocolVersion.XVnfProtocolVersion2
            payloadStr.toJsonObject()?.let { offersJsonObject ->
                return VCLOffers(
                    payload = offersJsonObject,
                    all = offersFromJsonArray(
                        offersJsonObject.optJSONArray(CodingKeys.KeyOffers) ?: JSONArray()
                    ),
                    responseCode = responseCode,
                    sessionToken = sessionToken,
                    challenge = offersJsonObject.optString(CodingKeys.KeyChallenge)
                )
            } ?: run {
//            VCLXVnfProtocolVersion.XVnfProtocolVersion1
                payloadStr.toJsonArray()?.let { offersJsonArray ->
                    return VCLOffers(
                        payload = "{\"${CodingKeys.KeyOffers}\":$offersJsonArray}".toJsonObject() ?: JSONObject(),
                        all = offersFromJsonArray(offersJsonArray),
                        responseCode = responseCode,
                        sessionToken = sessionToken,
                    )
                } ?: run {
//                No offers
                    return VCLOffers(
                        payload = JSONObject(),
                        all = listOf(),
                        responseCode = responseCode,
                        sessionToken = sessionToken,
                    )
                }
            }
        }
        internal fun offersFromJsonArray(offersJsonArray: JSONArray): List<VCLOffer> {
            val allOffers = mutableListOf<VCLOffer>()
            for (i in 0 until offersJsonArray.length()) {
                offersJsonArray.optJSONObject(i)?.let { offerJsonObject ->
                    allOffers.add(VCLOffer(offerJsonObject))
                }
            }
            return allOffers
        }
    }
}