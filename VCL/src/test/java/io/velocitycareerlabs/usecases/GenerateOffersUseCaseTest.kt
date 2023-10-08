/**
 * Created by Michael Avoyan on 11/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.data.repositories.GenerateOffersRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.GenerateOffersUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.GenerateOffersUseCase
import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.CommonMocks
import io.velocitycareerlabs.infrastructure.resources.valid.GenerateOffersMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class GenerateOffersUseCaseTest {

    lateinit var subject: GenerateOffersUseCase

    @Test
    fun testGenerateOffers() {
        subject = GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = GenerateOffersMocks.GeneratedOffers)
            ),
            ExecutorImpl()
        )

        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = VCLCredentialManifest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!)
            )
        )
        subject.generateOffers(
            generateOffersDescriptor = generateOffersDescriptor,
            issuingToken = VCLToken(value = "")
        ) {
            it.handleResult(
                { offers ->
                    assert(
                        offers.all.toString().toCharArray().sort() ==
                                GenerateOffersMocks.Offers.toJsonArray().toString().toCharArray().sort()
                    )
                    assert(offers.challenge == GenerateOffersMocks.Challenge)
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testGenerateOffersEmptyJsonObj() {
        subject = GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = GenerateOffersMocks.GeneratedOffersEmptyJsonObj)
            ),
            ExecutorImpl()
        )

        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = VCLCredentialManifest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!)
            )
        )

        // Action
        subject.generateOffers(
            generateOffersDescriptor = generateOffersDescriptor,
            issuingToken = VCLToken(value = "")
        ) {
            it.handleResult(
                {offers ->
                    assert(offers.all == "[]".toJsonArray())
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testGenerateOffersEmptyJsonArr() {
        subject = GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = GenerateOffersMocks.GeneratedOffersEmptyJsonArr)
            ),
            ExecutorImpl()
        )

        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = VCLCredentialManifest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!)
            )
        )

        // Action
        subject.generateOffers(
            generateOffersDescriptor = generateOffersDescriptor,
            issuingToken = VCLToken(value = "")
        ) {
            it.handleResult(
                { offers ->
                    assert(offers.all == GenerateOffersMocks.GeneratedOffersEmptyJsonArr.toJsonArray())
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @After
    fun tearDown() {
    }
}