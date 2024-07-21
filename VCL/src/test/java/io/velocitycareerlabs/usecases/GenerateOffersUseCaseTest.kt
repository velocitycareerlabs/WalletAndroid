/**
 * Created by Michael Avoyan on 11/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.repositories.GenerateOffersRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.GenerateOffersUseCaseImpl
import io.velocitycareerlabs.impl.data.verifiers.OffersByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.domain.usecases.GenerateOffersUseCase
import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.CommonMocks
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.GenerateOffersMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

internal class GenerateOffersUseCaseTest {

    private lateinit var subject1: GenerateOffersUseCase
    private lateinit var subject2: GenerateOffersUseCase
    private lateinit var subject3: GenerateOffersUseCase

    @Test
    fun testGenerateOffers() {
        subject1 = GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = GenerateOffersMocks.GeneratedOffers)
            ),
            OffersByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = VCLCredentialManifest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!),
                didJwk = DidJwkMocks.DidJwk
            )
        )

        subject1.generateOffers(
            generateOffersDescriptor = generateOffersDescriptor,
            sessionToken = CommonMocks.Token
        ) {
            it.handleResult(
                { offers ->
                    JSONAssert.assertEquals(
                        offers.all.map { it.payload }.toJsonArray(),
                        GenerateOffersMocks.Offers.toJsonArray(),
                        JSONCompareMode.LENIENT
                    )
                    assert(offers.challenge == GenerateOffersMocks.Challenge)
                    assert(offers.sessionToken.value == CommonMocks.Token.value)
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testGenerateOffersEmptyJsonObj() {
        subject2 = GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = GenerateOffersMocks.GeneratedOffersEmptyJsonObj)
            ),
            OffersByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = VCLCredentialManifest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!),
                didJwk = DidJwkMocks.DidJwk
            )
        )

        subject2.generateOffers(
            generateOffersDescriptor = generateOffersDescriptor,
            sessionToken = CommonMocks.Token
        ) {
            it.handleResult(
                { offers ->
                    assert(offers.all == listOf<VCLOffer>())
                    assert(offers.sessionToken.value == CommonMocks.Token.value)
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testGenerateOffersEmptyJsonArr() {
        subject3 = GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = GenerateOffersMocks.GeneratedOffersEmptyJsonArr)
            ),
            OffersByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = VCLCredentialManifest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!),
                didJwk = DidJwkMocks.DidJwk
            )
        )

        subject3.generateOffers(
            generateOffersDescriptor = generateOffersDescriptor,
            sessionToken = CommonMocks.Token
        ) {
            it.handleResult(
                { offers ->
                    assert(offers.all == listOf<VCLOffer>())
                    assert(offers.sessionToken.value == CommonMocks.Token.value)
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }
}