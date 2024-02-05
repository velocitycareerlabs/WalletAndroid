/**
 * Created by Michael Avoyan on 09/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

data class VCLCredentialManifest(
    val jwt: VCLJwt,
    val vendorOriginContext: String? = null,
    val verifiedProfile: VCLVerifiedProfile,
    val deepLink: VCLDeepLink? = null,
    val didJwk: VCLDidJwk? = null,
    val remoteCryptoServicesToken: VCLToken? = null
) {
    val iss: String get() = jwt.payload?.toJSONObject()?.get(KeyIss) as? String ?: ""
    val did: String get() = iss
    val aud: String get() = retrieveAud()
    val issuerId: String get() = jwt.payload?.toJSONObject()?.get(CodingKeys.KeyIssuer) as? String
        ?: (jwt.payload?.toJSONObject()?.get(CodingKeys.KeyIssuer) as? Map<*, *>)?.get(CodingKeys.KeyId) as? String
        ?: ""
    val exchangeId: String get() = jwt.payload?.toJSONObject()?.get(KeyExchangeId) as? String ?: ""
    val presentationDefinitionId: String
        get() = (jwt.payload?.toJSONObject()?.get(KeyPresentationDefinitionId) as? Map<*, *>)?.get(
            KeyId
        ) as? String ?: ""

    val finalizeOffersUri: String
        get() =
            (jwt.payload?.toJSONObject()
                ?.get(VCLCredentialManifest.KeyMetadata) as? Map<*, *>)?.get(
                VCLCredentialManifest.KeyFinalizeOffersUri
            )?.toString() ?: ""

    val checkOffersUri: String
        get() =
            (jwt.payload?.toJSONObject()
                ?.get(VCLCredentialManifest.KeyMetadata) as? Map<*, *>)?.get(
                VCLCredentialManifest.KeyCheckOffersUri
            )?.toString() ?: ""

    val submitPresentationUri: String
        get() =
            (jwt.payload?.toJSONObject()
                ?.get(VCLCredentialManifest.KeyMetadata) as? Map<*, *>)?.get(
                VCLCredentialManifest.KeySubmitIdentificationUri
            )?.toString() ?: ""

    private fun retrieveAud() =
        ((jwt.payload?.toJSONObject()?.getOrDefault(CodingKeys.KeyMetadata, HashMap<String, Any>()) as? Map<String, Any> )?.getOrDefault(CodingKeys.KeyFinalizeOffersUri, "") as? String ?: "").substringBefore("/issue/")
    companion object CodingKeys {
        const val KeyIssuingRequest = "issuing_request"

        const val KeyId = "id"
        const val KeyDid = "did"
        const val KeyIss = "iss"
        const val KeyIssuer = "issuer"
        const val KeyExchangeId = "exchange_id"
        const val KeyPresentationDefinitionId = "presentation_definition"

        const val KeyMetadata = "metadata"
        const val KeyCheckOffersUri = "check_offers_uri"
        const val KeyFinalizeOffersUri = "finalize_offers_uri"
        const val KeySubmitIdentificationUri = "submit_presentation_uri"
    }
}
