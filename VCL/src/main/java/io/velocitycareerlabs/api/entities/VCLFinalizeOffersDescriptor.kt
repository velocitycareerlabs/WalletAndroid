package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toJsonArray
import org.json.JSONObject

/**
 * Created by Michael Avoyan on 11/05/2021.
 */
data class VCLFinalizeOffersDescriptor(
    val credentialManifest: VCLCredentialManifest,
    val approvedOfferIds: List<String>,
    val rejectedOfferIds: List<String>
) {
    val payload: JSONObject =
        JSONObject()
            .putOpt(KeyExchangeId, exchangeId)
            .putOpt(KeyApprovedOfferIds, approvedOfferIds.toJsonArray())
            .putOpt(KeyRejectedOfferIds, rejectedOfferIds.toJsonArray())

    val did: String get() = credentialManifest.did
    val exchangeId: String get() = credentialManifest.exchangeId

    val finalizeOffersUri: String get() = credentialManifest.finalizeOffersUri

    companion object CodingKeys {
        const val KeyExchangeId = "exchangeId"
        const val KeyApprovedOfferIds = "approvedOfferIds"
        const val KeyRejectedOfferIds = "rejectedOfferIds"
    }
}
