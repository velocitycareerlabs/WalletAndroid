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
import io.velocitycareerlabs.impl.domain.usecases.GenerateOffersUseCase
import io.velocitycareerlabs.infrastructure.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.GenerateOffersMocks
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class GenerateOffersUseCaseTest {

    lateinit var subject: GenerateOffersUseCase
    @Mock
    lateinit var generateOffersDescriptor: VCLGenerateOffersDescriptor
    @Mock
    lateinit var token: VCLToken

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(generateOffersDescriptor.did).thenReturn("")
        Mockito.`when`(generateOffersDescriptor.exchangeId).thenReturn("")
        Mockito.`when`(generateOffersDescriptor.checkOffersUri).thenReturn("")
        Mockito.`when`(generateOffersDescriptor.payload).thenReturn(JSONObject("{}"))
    }

    @Test
    fun testGenerateOffers() {
        // Arrange
        subject = GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(GenerateOffersMocks.GeneratedOffers)
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLOffers>? = null

        // Action
        subject.generateOffers(token, generateOffersDescriptor) {
            result = it
        }

        // Assert
        assert(result!!.data!!.all.toString() == JSONArray(GenerateOffersMocks.GeneratedOffers).toString())
    }

    @Test
    fun testGenerateOffersEmptyJsonObj() {
        // Arrange
        subject = GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(GenerateOffersMocks.GeneratedOffersEmptyJsonObj)
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLOffers>? = null

        // Action
        subject.generateOffers(token, generateOffersDescriptor) {
            result = it
        }

        // Assert
        assert(result!!.data!!.all.toString() == JSONArray("[]").toString())
    }

    @Test
    fun testGenerateOffersEmptyJsonArr() {
        // Arrange
        subject = GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(GenerateOffersMocks.GeneratedOffersEmptyJsonArr)
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLOffers>? = null

        // Action
        subject.generateOffers(token, generateOffersDescriptor) {
            result = it
        }

        // Assert
        assert(result!!.data!!.all.toString() == JSONArray(GenerateOffersMocks.GeneratedOffersEmptyJsonArr).toString())
    }

    @After
    fun tearDown() {
    }
}