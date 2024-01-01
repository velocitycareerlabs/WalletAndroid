/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.verifiers

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLOffer
import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.utils.Utils
import io.velocitycareerlabs.impl.data.verifiers.OffersByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.GenerateOffersMocks
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

class OffersByDeepLinkVerifierTest {
    val subject = OffersByDeepLinkVerifierImpl()

    private val offersPayload = GenerateOffersMocks.RealOffers.toJsonObject() ?: JSONObject()
    private val offers = VCLOffers(
        payload = offersPayload,
        all = Utils.offersFromJsonArray(
            offersPayload.optJSONArray(VCLOffers.CodingKeys.KeyOffers) ?: JSONArray()
        ),
        responseCode = 0,
        sessionToken = VCLToken(""),
        challenge = ""
    )
    private val correctDeepLink = DeepLinkMocks.CredentialManifestDeepLinkDevNet
    private val wrongDeepLink = VCLDeepLink(
        "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbt%2Fissue%2Fget-credential-manifest%3Fid%3D611b5836e93d08000af6f1bc%26credential_types%3DPastEmploymentPosition%26issuerDid%3Ddid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbt"
    )

    @Test
    fun verifyOffersSuccess() {
        subject.verifyOffers(
            offers,
            correctDeepLink
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
        subject.verifyOffers(
            offers,
            wrongDeepLink
        ) {
            it.handleResult({
                assert(false) { "${VCLErrorCode.MismatchedOfferIssuerDid.value} error code is expected" }
            }, { error ->
                assert(error.errorCode == VCLErrorCode.MismatchedOfferIssuerDid.value)
            })
        }
    }
}