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

data class VCLDidDocument(val payload: JSONObject) {
    val id: String
        get() = payload.optString(KeyId)
    val alsoKnownAs: List<String>
        get() =payload.optJSONArray(KeyAlsoKnownAs)?.toList() as? List<String> ?: emptyList()

    constructor(payloadStr: String) : this(payloadStr.toJsonObject() ?: JSONObject())

    companion object CodingKeys {
        const val KeyId = "id"
        const val KeyAlsoKnownAs = "alsoKnownAs"
    }
}
