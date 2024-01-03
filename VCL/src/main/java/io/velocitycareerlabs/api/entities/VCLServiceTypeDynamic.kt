/**
 * Created by Michael Avoyan on 25/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import org.json.JSONObject

data class VCLServiceTypeDynamic(
    val payload: JSONObject
) {
    val serviceType: String get() = payload.optString(KeyServiceType)
    val serviceCategory: String get() = payload.optString(KeyServiceCategory)
    val notary: Boolean get() = payload.optBoolean(KeyNotary)
    val credentialGroup: String get() = payload.optString(KeyCredentialGroup)

    companion object CodingKeys {
        const val KeyServiceType = "serviceType"
        const val KeyServiceCategory = "serviceCategory"
        const val KeyNotary = "notary"
        const val KeyCredentialGroup = "credentialGroup"
    }
}
