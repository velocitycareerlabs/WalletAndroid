/**
 * Created by Michael Avoyan on 12/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.keys.KeyServiceImpl
import io.velocitycareerlabs.impl.data.repositories.FinalizeOffersRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.GenerateOffersRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.FinalizeOffersUseCaseImpl
import io.velocitycareerlabs.impl.data.usecases.GenerateOffersUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.FinalizeOffersUseCase
import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.CommonMocks
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.FinalizeOffersMocks
import io.velocitycareerlabs.infrastructure.resources.valid.GenerateOffersMocks
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class FinalizeOffersUseCaseTest {

    lateinit var subject: FinalizeOffersUseCase

    lateinit var offers: VCLOffers
    lateinit var token: VCLToken
    lateinit var didJwk: VCLDidJwk
    private val keyService = KeyServiceImpl(SecretStoreServiceMock.Instance)
    lateinit var credentialManifestFailed: VCLCredentialManifest
    lateinit var credentialManifestPassed: VCLCredentialManifest
    lateinit var finalizeOffersDescriptorFailed: VCLFinalizeOffersDescriptor
    lateinit var finalizeOffersDescriptorPassed: VCLFinalizeOffersDescriptor
    private val vclJwtFailed = VCLJwt(encodedJwt = CredentialManifestMocks.CredentialManifestJwt1)
    private val vclJwtPassed = VCLJwt(encodedJwt = CredentialManifestMocks.CredentialManifestJwt2)

    @Before
    fun setUp() {
        didJwk = keyService.generateDidJwk()

        var result: VCLResult<VCLOffers>? = null
        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = VCLCredentialManifest(jwt = CommonMocks.JWT)
        )
        GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = GenerateOffersMocks.GeneratedOffers)
            ),
            EmptyExecutor()
        ).generateOffers(
            token = VCLToken(value = ""),
            generateOffersDescriptor = generateOffersDescriptor
        ) {
            result = it
        }
        offers = result?.data!!
        assert(offers.all.toString().toCharArray().sort() == GenerateOffersMocks.Offers.toCharArray().sort())
        assert(offers.challenge == GenerateOffersMocks.Challenge)

        credentialManifestFailed = VCLCredentialManifest(
            jwt = vclJwtFailed
        )
        credentialManifestPassed = VCLCredentialManifest(
            jwt = vclJwtPassed
        )

        finalizeOffersDescriptorFailed = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifestFailed,
            offers = offers,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )
        finalizeOffersDescriptorPassed = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifestPassed,
            offers = offers,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )
    }

    @Test
    fun testFailedCredentials() {
        // Arrange
        subject = FinalizeOffersUseCaseImpl(
            FinalizeOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = FinalizeOffersMocks.EncodedJwtVerifiableCredentials)
            ),
            JwtServiceRepositoryImpl(
                JwtServiceImpl(keyService)
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLJwtVerifiableCredentials>? = null

        // Action
        subject.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorFailed,
            didJwk = didJwk,
            token = VCLToken(value = "")
        ) {
            result = it
        }

        // Assert
        val finalizeOffers = result?.data
        assert(
            finalizeOffers!!.failedCredentials[0].signedJwt.serialize() ==
                    FinalizeOffersMocks.AdamSmithEmailJwt
        )
        assert(
            finalizeOffers.failedCredentials[1].signedJwt.serialize() ==
                    FinalizeOffersMocks.AdamSmithDriverLicenseJwt
        )
        assert(
            finalizeOffers.failedCredentials[2].signedJwt.serialize() ==
                    FinalizeOffersMocks.AdamSmithPhoneJwt
        )

        assert(finalizeOffers.passedCredentials.isEmpty())
    }

    @Test
    fun testPassedCredentials() {
        // Arrange
        subject = FinalizeOffersUseCaseImpl(
            FinalizeOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = FinalizeOffersMocks.EncodedJwtVerifiableCredentials)
            ),
            JwtServiceRepositoryImpl(
                JwtServiceImpl(keyService)
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLJwtVerifiableCredentials>? = null

        // Action
        subject.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorPassed,
            didJwk = didJwk,
            token = VCLToken(value = "")
        ) {
            result = it
        }

        // Assert
        val finalizeOffers = result?.data
        assert(
            finalizeOffers!!.passedCredentials[0].signedJwt.serialize() ==
                    FinalizeOffersMocks.AdamSmithEmailJwt
        )
        assert(
            finalizeOffers.passedCredentials[1].signedJwt.serialize() ==
                    FinalizeOffersMocks.AdamSmithDriverLicenseJwt
        )
        assert(
            finalizeOffers.passedCredentials[2].signedJwt.serialize() ==
                    FinalizeOffersMocks.AdamSmithPhoneJwt
        )

        assert(finalizeOffers.failedCredentials.isEmpty())
    }

    @Test
    fun testEmptyCredentials() {
        // Arrange
        subject = FinalizeOffersUseCaseImpl(
            FinalizeOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = FinalizeOffersMocks.EmptyVerifiableCredentials)
            ),
            JwtServiceRepositoryImpl(
                JwtServiceImpl(keyService)
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLJwtVerifiableCredentials>? = null

        // Action
        subject.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorPassed,
            didJwk = didJwk,
            token = VCLToken(value = "")
        ) {
            result = it
        }

        // Assert
        val finalizeOffers = result?.data

        assert(finalizeOffers!!.failedCredentials.isEmpty())
        assert(finalizeOffers.passedCredentials.isEmpty())
    }

    @After
    fun tearDown() {
    }
}