/**
 * Created by Michael Avoyan on 31/05/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import android.os.Build
import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class VCLFinalizeOffersDescriptorTest {
    lateinit var subject: VCLFinalizeOffersDescriptor

    private val offers = VCLOffers(
        payload = JSONObject(),
        all = listOf(),
        responseCode = 200,
        sessionToken = VCLToken(value = ""),
        challenge = ""
    )

    private val jtiMock = "some jti"
    private val issMock = "some iss"
    private val audMock = "some sud"
    private val nonceMock = "some nonce"

    private val approvedOfferIds = listOf("approvedOfferId1", "approvedOfferId2")
    private val rejectedOfferIds = listOf("rejectedOfferId1", "rejectedOfferId2")

    @Before
    fun setUp() {
        val credentialManifest =
            VCLCredentialManifest(
                jwt = VCLJwt(encodedJwt = CredentialManifestMocks.JwtCredentialManifest1),
                verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!)
            )

        subject = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifest,
            offers = offers,
            approvedOfferIds = approvedOfferIds,
            rejectedOfferIds = rejectedOfferIds
        )
    }

    @Test
    fun testGenerateRequestBody() {
        val payload = "{\"key1\": \"value1\"}".toJsonObject()
        VCLJwtSignServiceLocalImpl(VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)).sign(
            nonce = nonceMock,
            jwtDescriptor = VCLJwtDescriptor(
                payload = payload,
                jti = jtiMock,
                iss = issMock,
                aud = audMock
            )
        ) { jwtResult ->
            jwtResult.handleResult({ jwt ->
                val requestBody = subject.generateRequestBody(jwt = jwt)

                assert((requestBody["exchangeId"] as? String) == "645e315309237c760ac022b1")
                assert(requestBody["approvedOfferIds"] as? JSONArray == approvedOfferIds.toJsonArray())
                assert(requestBody["rejectedOfferIds"] as? JSONArray == rejectedOfferIds.toJsonArray())
                val proof = requestBody["proof"] as? JSONObject
                assert(proof?.optString("proof_type") == "jwt")
                assert(proof?.optString("jwt") == jwt.encodedJwt)
//        equivalent to checking nonce in proof jwt
                assert(jwt.payload?.toJSONObject()?.get( "nonce") as? String == nonceMock)
            }, {
                assert(false) { "failed to generate jwt: $it" }
            })
        }
    }
}