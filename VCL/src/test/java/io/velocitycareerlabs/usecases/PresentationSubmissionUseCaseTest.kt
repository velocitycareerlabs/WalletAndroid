/**
 * Created by Michael Avoyan on 5/1/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.SubmissionRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.PresentationSubmissionUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.PresentationSubmissionUseCase
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationSubmissionMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class PresentationSubmissionUseCaseTest {

    lateinit var subject: PresentationSubmissionUseCase

    @Before
    fun setUp() {
    }

    @Test
    fun testSubmitPresentationSuccess() {
//        Arrange
        subject = PresentationSubmissionUseCaseImpl(
            SubmissionRepositoryImpl(
                NetworkServiceSuccess(PresentationSubmissionMocks.PresentationSubmissionResultJson)
            ),
            JwtServiceRepositoryImpl(
                JwtServiceImpl()
            ),
            EmptyExecutor()
        )
        val presentationSubmission = VCLPresentationSubmission(
            PresentationSubmissionMocks.PresentationRequest,
            PresentationSubmissionMocks.SelectionsList
        )
        var result: VCLResult<VCLSubmissionResult>? = null

//        Action
        subject.submit(presentationSubmission) {
            result = it
        }
        val expectedPresentationSubmissionResult =
            expectedPresentationSubmissionResult(
                JSONObject(PresentationSubmissionMocks.PresentationSubmissionResultJson),
                presentationSubmission.jti,
                presentationSubmission.submissionId
            )

//        Assert
        assert(result!!.data is VCLSubmissionResult)
        assert(result!!.data == expectedPresentationSubmissionResult)
        assert(result!!.data!!.exchange.id == expectedPresentationSubmissionResult.exchange.id)
        assert(result!!.data!!.token.value == expectedPresentationSubmissionResult.token.value)
        assert(result!!.data!!.jti == expectedPresentationSubmissionResult.jti)
        assert(result!!.data!!.submissionId == expectedPresentationSubmissionResult.submissionId)
    }

    private fun expectedPresentationSubmissionResult(
        jsonObj: JSONObject,
        jti: String,
        submissionId: String
    ): VCLSubmissionResult {
        val exchangeJsonObj = jsonObj.getJSONObject(VCLSubmissionResult.KeyExchange)
        return VCLSubmissionResult(
            token = VCLToken(jsonObj.getString(VCLSubmissionResult.KeyToken)),
            exchange = expectedExchange(exchangeJsonObj),
            jti = jti,
            submissionId = submissionId
        )
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