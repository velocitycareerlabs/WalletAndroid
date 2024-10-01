/**
 * Created by Michael Avoyan on 3/11/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toList
import org.json.JSONObject

open class VCLService(val payload: JSONObject) {
    val id: String = payload.optString(VCLService.KeyId)
    val type: String = payload.optString(VCLService.KeyType)
    val serviceEndpoint: String = payload.optString(VCLService.KeyServiceEndpoint)
    val credentialTypes: List<String>? = payload.optJSONArray(VCLService.KeyCredentialTypes)?.toList() as? List<String>

    open fun toPropsString() =
        StringBuilder()
            .append("\npayload: $payload")
            .append("\nid: $id")
            .append("\ntype: $type")
            .append("\nserviceEndpoint: $serviceEndpoint")
            .append("\ncredentialTypes: $credentialTypes")
            .toString()

    companion object CodingKeys {
        const val KeyId = "id"
        const val KeyType = "type"
        const val KeyCredentialTypes = "credentialTypes"
        const val KeyServiceEndpoint = "serviceEndpoint"
    }
}