/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.verifiers

import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.repositories.ResolveDidDocumentRepositoryImpl
import io.velocitycareerlabs.impl.data.verifiers.OffersByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.domain.verifiers.OffersByDeepLinkVerifier
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidDocumentMocks
import io.velocitycareerlabs.infrastructure.resources.valid.GenerateOffersMocks
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

class OffersByDeepLinkVerifierTest {
    private lateinit var subject: OffersByDeepLinkVerifier

    private val offersPayload = GenerateOffersMocks.RealOffers.toJsonObject() ?: JSONObject()
    private val offers = VCLOffers(
        payload = offersPayload,
        all = VCLOffers.offersFromJsonArray(
            offersPayload.optJSONArray(VCLOffers.Companion.CodingKeys.KeyOffers) ?: JSONArray()
        ),
        responseCode = 0,
        sessionToken = VCLToken(""),
        challenge = ""
    )
    private val deepLink = DeepLinkMocks.CredentialManifestDeepLinkDevNet

    @Test
    fun verifyOffersSuccess() {
        subject = OffersByDeepLinkVerifierImpl(
            ResolveDidDocumentRepositoryImpl(
                NetworkServiceSuccess(DidDocumentMocks.DidDocumentMockStr)
            )
        )

        subject.verifyOffers(
            offers,
            deepLink
        ) {
            it.handleResult({ isVerified ->
                assert(isVerified)
            }, { error ->
                assert(false) { "${error.toJsonObject()}" }
            })
        }
    }

    @Test
    fun verifyOffersError() {
        subject = OffersByDeepLinkVerifierImpl(
            ResolveDidDocumentRepositoryImpl(
                NetworkServiceSuccess(DidDocumentMocks.DidDocumentWithWrongDidMockStr)
            )
        )

        subject.verifyOffers(
            offers,
            deepLink
        ) {
            it.handleResult({
                assert(false) { "${VCLErrorCode.MismatchedOfferIssuerDid.value} error code is expected" }
            }, { error ->
                assert(error.errorCode == VCLErrorCode.MismatchedOfferIssuerDid.value)
            })
        }
    }
}