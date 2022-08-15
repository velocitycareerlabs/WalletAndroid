package io.velocitycareerlabs.api.entities

/**
 * Created by Michael Avoyan on 4/13/21.
 */
data class VCLSubmissionResult(val token: VCLToken, val exchange: VCLExchange) {

    companion object CodingKeys {
        const val KeyToken = "token"
        const val KeyExchange = "exchange"
    }
}