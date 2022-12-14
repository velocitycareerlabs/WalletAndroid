/**
 * Created by Michael Avoyan on 30/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.VCLExchangeDescriptor
import io.velocitycareerlabs.impl.data.repositories.ExchangeProgressRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.ExchangeProgressUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.ExchangeProgressUseCase
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.ExchangeProgressMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class ExchangeProgressUseCaseTest {

    lateinit var subject: ExchangeProgressUseCase
    @Mock
    lateinit var exchangeDescriptor: VCLExchangeDescriptor

    @Before
    fun setUp(){
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(exchangeDescriptor.exchangeId).thenReturn("")
        Mockito.`when`(exchangeDescriptor.processUri).thenReturn("")
        Mockito.`when`(exchangeDescriptor.token).thenReturn(VCLToken(""))
    }

    @Test
    fun testGetExchangeProgress() {
        subject = ExchangeProgressUseCaseImpl(
            ExchangeProgressRepositoryImpl(
                NetworkServiceSuccess(ExchangeProgressMocks.ExchangeProgressJson)
            ),
            EmptyExecutor()
        )

        var result: VCLResult<VCLExchange>? = null

//        Action
        subject.getExchangeProgress(exchangeDescriptor) {
            result = it
        }

//        Assert
        assert(result!!.data == expectedExchange(JSONObject(ExchangeProgressMocks.ExchangeProgressJson)))
    }

    private fun expectedExchange(exchangeJsonObj: JSONObject) =
        VCLExchange(
            id = exchangeJsonObj.getString(VCLExchange.KeyId),
            type = exchangeJsonObj.getString(VCLExchange.KeyType),
            disclosureComplete = exchangeJsonObj.getBoolean(VCLExchange.KeyDisclosureComplete),
            exchangeComplete = exchangeJsonObj.getBoolean(VCLExchange.KeyExchangeComplete)
        )

    @After
    fun tearDown() {
    }
}