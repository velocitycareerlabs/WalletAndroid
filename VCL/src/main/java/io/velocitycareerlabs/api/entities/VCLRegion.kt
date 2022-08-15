package io.velocitycareerlabs.api.entities

import org.json.JSONObject

data class VCLRegion(
    override val payload: JSONObject,
    override val code: String,
    override val name: String
    ): VCLPlace {

    companion object Codes {
        const val KeyCode = "code"
        const val KeyName = "name"
    }
}