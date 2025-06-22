/**
 * Created by Michael Avoyan on 06/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toList
import org.json.JSONObject
import java.util.HashMap

data class VCLDidDocument(val payload: JSONObject) {
    constructor(payloadStr: String) : this(payloadStr.toJsonObject() ?: JSONObject())

    val id: String
        get() = payload.optString(KeyId)

    val alsoKnownAs: List<String>
        get() = payload.optJSONArray(KeyAlsoKnownAs)
            ?.toList()
            ?.filterIsInstance<String>()
            ?: emptyList()

    fun getPublicJwk(kid: String): VCLPublicJwk? {
        if (!kid.contains("#")) return null

        val publicJwkId = "#${kid.substringAfter("#")}"
        val verificationMethod = payload.optJSONArray(KeyVerificationMethod)?.toList().orEmpty()

        val publicJwkPayload = verificationMethod
            .filterIsInstance<Map<*, *>>()
            .find { it[KeyId] == publicJwkId }

        val publicJwk = publicJwkPayload?.get(KeyPublicKeyJwk) as? Map<*, *>
        return publicJwk?.let { VCLPublicJwk(it.toJsonObject()) }
    }

    companion object CodingKeys {
        const val KeyId = "id"
        const val KeyAlsoKnownAs = "alsoKnownAs"
        const val KeyVerificationMethod = "verificationMethod"
        const val KeyPublicKeyJwk = "publicKeyJwk"
    }
}
