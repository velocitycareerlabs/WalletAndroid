package io.velocitycareerlabs.api.entities

import org.json.JSONObject

data class VCLCountry
    (
    override val payload: JSONObject,
    override val code: String,
    override val name: String,
    val regions: VCLRegions?
    ): VCLPlace {
//    val code: String? get() = payload.optString(KeyCode)
//    val name: String? get() = payload.optString(KeyName)
//    val regions: JSONArray? get() = payload.optJSONArray(KeyRegions)

    companion object Codes {
        const val KeyCode = "code"
        const val KeyName = "name"
        const val KeyRegions = "regions"
    }
}