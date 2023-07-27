/**
 * Created by Michael Avoyan on 3/21/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import org.json.JSONArray
import org.json.JSONObject

data class VCLCredentialType (
    val payload: JSONObject,
    val id: String? = null,
    val schema: String? = null,
    val createdAt: String? = null,
    val schemaName: String? = null,
    val credentialType: String? = null,
    val recommended: Boolean = false,
    val jsonldContext: JSONArray? = null,
    val issuerCategory: String? = null
    ) {

    companion object CodingKeys {
        const val KeyId = "id"
        const val KeySchema = "schema"
        const val KeyCreatedAt = "createdAt"
        const val KeySchemaName = "schemaName"
        const val KeyCredentialType = "credentialType"
        const val KeyRecommended = "recommended"
        const val KeyJsonldContext = "jsonldContext"
        const val KeyIssuerCategory = "issuerCategory"
    }
}