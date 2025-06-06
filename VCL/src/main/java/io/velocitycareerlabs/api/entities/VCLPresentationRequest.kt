/**
 * Created by Michael Avoyan on 4/11/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

data class VCLPresentationRequest(
    val jwt: VCLJwt,
    val verifiedProfile: VCLVerifiedProfile,
    val deepLink: VCLDeepLink,
    val pushDelegate: VCLPushDelegate? = null,
    val didJwk: VCLDidJwk,
    val remoteCryptoServicesToken: VCLToken? = null
    ) {

    val feed: Boolean
    val vendorOriginContext: String?

    init {
        this.feed =
            (jwt.payload?.toJSONObject()?.get(KeyMetadata) as? Map<*, *>)?.get(KeyFeed) as? Boolean ?: false
        this.vendorOriginContext = deepLink.vendorOriginContext
    }
    companion object CodingKeys {
        const val KeyId = "id"
        const val KeyIss = "iss"
        const val KeyExchangeId = "exchange_id"
        const val KeyPresentationRequest = "presentation_request"
        const val KeyPresentationDefinition = "presentation_definition"

        const val KeyMetadata = "metadata"
        const val KeyProgressUri = "progress_uri"
        const val KeySubmitPresentationUri = "submit_presentation_uri"
        const val KeyFeed = "feed"
        const val KeyAuthTokenUri = "auth_token_uri";
    }

    val iss: String get() = jwt.payload?.toJSONObject()?.get(KeyIss)?.toString() ?: ""
    val exchangeId: String get() = jwt.payload?.toJSONObject()?.get(KeyExchangeId)?.toString() ?: ""
    val presentationDefinitionId get() =
        (jwt.payload?.toJSONObject()?.get(KeyPresentationDefinition) as? Map<*, *>)?.get(KeyId) as? String ?: ""
    val keyID get() = jwt.header?.keyID ?: ""

    val progressUri: String get() =
        (jwt.payload?.toJSONObject()?.get(KeyMetadata) as? Map<*, *>)?.get(KeyProgressUri)?.toString() ?: ""
    val submitPresentationUri: String get() =
        (jwt.payload?.toJSONObject()?.get(KeyMetadata) as? Map<*, *>)?.get(KeySubmitPresentationUri)?.toString() ?: ""

    val authTokenUri: String get() =
        (jwt.payload?.toJSONObject()?.get(KeyMetadata) as? Map<*, *>)?.get(KeyAuthTokenUri)?.toString() ?: ""
}