package io.velocitycareerlabs.api.entities

import org.json.JSONObject

interface VCLPlace {
    val payload: JSONObject
    val code: String
    val name: String
}