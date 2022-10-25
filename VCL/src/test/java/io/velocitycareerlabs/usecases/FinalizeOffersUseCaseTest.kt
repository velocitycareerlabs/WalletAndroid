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
    lateinit var finalizeOffersDescriptor: VCLFinalizeOffersDescriptor
    @Mock
    lateinit var token: VCLToken

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(finalizeOffersDescriptor.did).thenReturn("")
        Mockito.`when`(finalizeOffersDescriptor.exchangeId).thenReturn("")
        Mockito.`when`(finalizeOffersDescriptor.finalizeOffersUri).thenReturn("")
        Mockito.`when`(finalizeOffersDescriptor.approvedOfferIds).thenReturn(listOf())
        Mockito.`when`(finalizeOffersDescriptor.rejectedOfferIds).thenReturn(listOf())
        Mockito.`when`(finalizeOffersDescriptor.payload).thenReturn(JSONObject("{}"))
    }

    @Test
    fun testGenerateOffers() {
        // Arrange
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
        assert(result!!.data!!.all[0].signedJwt.serialize() == FinalizeOffersMocks.EncodedJwtVerifiableCredential)
    }

    @After
    fun tearDown() {
    }
}