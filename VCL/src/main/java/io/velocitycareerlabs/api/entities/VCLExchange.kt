package io.velocitycareerlabs.api.entities

/**
 * Created by Michael Avoyan on 5/4/21.
 */
data class VCLExchange(val id: String, val type: String, val disclosureComplete: Boolean, val exchangeComplete: Boolean) {

    companion object CodingKeys {
        const val KeyId = "id"
        const val KeyType = "type"
        const val KeyDisclosureComplete = "disclosureComplete"
        const val KeyExchangeComplete = "exchangeComplete"
    }
}