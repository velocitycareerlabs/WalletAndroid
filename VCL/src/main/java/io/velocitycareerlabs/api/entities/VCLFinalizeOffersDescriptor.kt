/**
 * Created by Michael Avoyan on 11/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toJsonArray
import org.json.JSONObject

data class VCLFinalizeOffersDescriptor(
    val credentialManifest: VCLCredentialManifest,
    val offers: VCLOffers,
    val approvedOfferIds: List<String>,
    val rejectedOfferIds: List<String>
) {
    val payload: JSONObject =
        JSONObject()
            .putOpt(KeyExchangeId, exchangeId)
            .putOpt(KeyApprovedOfferIds, approvedOfferIds.toJsonArray())
            .putOpt(KeyRejectedOfferIds, rejectedOfferIds.toJsonArray())

    val didJwk: VCLDidJwk get() = credentialManifest.didJwk
    val remoteCryptoServicesToken: VCLToken? get() = credentialManifest.remoteCryptoServicesToken
    val issuerId: String get() = credentialManifest.issuerId
    val aud: String get() = credentialManifest.aud
    val exchangeId: String get() = credentialManifest.exchangeId
    val finalizeOffersUri: String get() = credentialManifest.finalizeOffersUri
    val serviceTypes: VCLServiceTypes get() = credentialManifest.verifiedProfile.serviceTypes

    fun generateRequestBody(proof: VCLJwt?): JSONObject {
        val retVal = JSONObject(this.payload.toString())
        proof?.let {
            val proofJson = JSONObject()
            proofJson.put(CodingKeys.KeyProofType, CodingKeys.KeyJwt)
            proofJson.put(CodingKeys.KeyJwt, it.encodedJwt)
            retVal.put(CodingKeys.KeyProof, proofJson)
        }
        return retVal
    }

    companion object CodingKeys {
        const val KeyExchangeId = "exchangeId"
        const val KeyApprovedOfferIds = "approvedOfferIds"
        const val KeyRejectedOfferIds = "rejectedOfferIds"

        const val KeyJwt = "jwt"
        const val KeyProof = "proof"
        const val KeyProofType = "proof_type"
    }
}
