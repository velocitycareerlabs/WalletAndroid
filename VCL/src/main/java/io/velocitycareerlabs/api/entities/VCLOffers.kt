package io.velocitycareerlabs.api.entities

import org.json.JSONArray

/**
 * Created by Michael Avoyan on 10/05/2021.
 */
data class VCLOffers(val all: JSONArray, val responseCode: Int, val token: VCLToken)
