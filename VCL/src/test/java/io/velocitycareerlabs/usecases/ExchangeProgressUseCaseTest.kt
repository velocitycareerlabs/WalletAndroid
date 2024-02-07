/**
 * Created by Michael Avoyan on 30/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.VCLExchangeDescriptor
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.data.repositories.ExchangeProgressRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.ExchangeProgressUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.ExchangeProgressUseCase
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.ExchangeProgressMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class ExchangeProgressUseCaseTest {

    private lateinit var subject1: ExchangeProgressUseCase
    private lateinit var subject2: ExchangeProgressUseCase
    @Mock
    lateinit var exchangeDescriptor: VCLExchangeDescriptor

    @Before
    fun setUp(){
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(exchangeDescriptor.exchangeId).thenReturn("")
        Mockito.`when`(exchangeDescriptor.processUri).thenReturn("")
        Mockito.`when`(exchangeDescriptor.sessionToken).thenReturn(VCLToken(""))
    }

    @Test
    fun testGetExchangeProgressSuccess() {
        subject1 = ExchangeProgressUseCaseImpl(
            ExchangeProgressRepositoryImpl(
                NetworkServiceSuccess(ExchangeProgressMocks.ExchangeProgressJson)
            ),
            EmptyExecutor()
        )

        subject1.getExchangeProgress(exchangeDescriptor) {
            it.handleResult(
                { exchange ->
                    assert(exchange == expectedExchange(JSONObject(ExchangeProgressMocks.ExchangeProgressJson)))
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testGetExchangeProgressFailure() {
        subject2 = ExchangeProgressUseCaseImpl(
            ExchangeProgressRepositoryImpl(
                NetworkServiceSuccess("wrong payload")
            ),
            EmptyExecutor()
        )

        subject2.getExchangeProgress(exchangeDescriptor) {
            it.handleResult(
                successHandler = {
                    assert(false) { "${VCLErrorCode.SdkError.value} error code is expected" }
                },
                errorHandler = { error ->
                    assert(error.errorCode == VCLErrorCode.SdkError.value)
                }
            )
        }
    }

    private fun expectedExchange(exchangeJsonObj: JSONObject) =
        VCLExchange(
            id = exchangeJsonObj.getString(VCLExchange.KeyId),
            type = exchangeJsonObj.getString(VCLExchange.KeyType),
            disclosureComplete = exchangeJsonObj.getBoolean(VCLExchange.KeyDisclosureComplete),
            exchangeComplete = exchangeJsonObj.getBoolean(VCLExchange.KeyExchangeComplete)
        )
}