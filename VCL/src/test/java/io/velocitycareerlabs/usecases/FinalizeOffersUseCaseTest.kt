/**
 * Created by Michael Avoyan on 12/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.repositories.FinalizeOffersRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.FinalizeOffersUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.FinalizeOffersUseCase
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.FinalizeOffersMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class FinalizeOffersUseCaseTest {

    lateinit var subject: FinalizeOffersUseCase
    @Mock
    lateinit var token: VCLToken
    @Mock
    lateinit var finalizeOffersDescriptor: VCLFinalizeOffersDescriptor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(finalizeOffersDescriptor.exchangeId).thenReturn("")
        Mockito.`when`(finalizeOffersDescriptor.finalizeOffersUri).thenReturn("")
        Mockito.`when`(finalizeOffersDescriptor.approvedOfferIds).thenReturn(listOf())
        Mockito.`when`(finalizeOffersDescriptor.rejectedOfferIds).thenReturn(listOf())
        Mockito.`when`(finalizeOffersDescriptor.payload).thenReturn(JSONObject("{}"))
    }

    @Test
    fun testFailedCredentials() {
        // Arrange
        Mockito.`when`(finalizeOffersDescriptor.did).thenReturn("did:velocity:0xba7d87f9d5e473d7ajhshd87ey438hfn23de8fc0e")

        subject = FinalizeOffersUseCaseImpl(
            FinalizeOffersRepositoryImpl(
                NetworkServiceSuccess(FinalizeOffersMocks.EncodedJwtVerifiableCredentials)
            ),
            JwtServiceRepositoryImpl(
                JwtServiceImpl()
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLJwtVerifiableCredentials>? = null

        // Action
        subject.finalizeOffers(token, finalizeOffersDescriptor) {
            result = it
        }

        // Assert
        assert(result!!.data!!.failedCredentials[0].signedJwt.serialize() == FinalizeOffersMocks.AdamSmithEmailJwt)
        assert(result!!.data!!.failedCredentials[1].signedJwt.serialize() == FinalizeOffersMocks.AdamSmithDriverLicenseJwt)
        assert(result!!.data!!.failedCredentials[2].signedJwt.serialize() == FinalizeOffersMocks.AdamSmithPhoneJwt)

        assert(result!!.data!!.passedCredentials.isEmpty())
    }

    @Test
    fun testPassedCredentials() {
        // Arrange
        Mockito.`when`(finalizeOffersDescriptor.did).thenReturn("did:velocity:0xba7d87f9d5e473d7d3a82d152923adb53de8fc0e")

        subject = FinalizeOffersUseCaseImpl(
            FinalizeOffersRepositoryImpl(
                NetworkServiceSuccess(FinalizeOffersMocks.EncodedJwtVerifiableCredentials)
            ),
            JwtServiceRepositoryImpl(
                JwtServiceImpl()
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLJwtVerifiableCredentials>? = null

        // Action
        subject.finalizeOffers(token, finalizeOffersDescriptor) {
            result = it
        }

        // Assert
        assert(result!!.data!!.passedCredentials[0].signedJwt.serialize() == FinalizeOffersMocks.AdamSmithEmailJwt)
        assert(result!!.data!!.passedCredentials[1].signedJwt.serialize() == FinalizeOffersMocks.AdamSmithDriverLicenseJwt)
        assert(result!!.data!!.passedCredentials[2].signedJwt.serialize() == FinalizeOffersMocks.AdamSmithPhoneJwt)

        assert(result!!.data!!.failedCredentials.isEmpty())
    }

    @After
    fun tearDown() {
    }
}