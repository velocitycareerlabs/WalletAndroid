/**
 * Created by Michael Avoyan on 5/1/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.keys.KeyServiceImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.PresentationSubmissionRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.PresentationSubmissionUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.PresentationSubmissionUseCase
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.CommonMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationSubmissionMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class PresentationSubmissionUseCaseTest {

    lateinit var subject: PresentationSubmissionUseCase

    lateinit var didJwk: VCLDidJwk
    private val keyService = KeyServiceImpl(SecretStoreServiceMock.Instance)

    @Before
    fun setUp() {
        didJwk = keyService.generateDidJwk()
    }

    @Test
    fun testSubmitPresentationSuccess() {
        subject = PresentationSubmissionUseCaseImpl(
            PresentationSubmissionRepositoryImpl(
                NetworkServiceSuccess(validResponse = PresentationSubmissionMocks.PresentationSubmissionResultJson)
            ),
            JwtServiceRepositoryImpl(
                JwtServiceImpl(keyService)
            ),
            EmptyExecutor()
        )
        val presentationSubmission = VCLPresentationSubmission(
            presentationRequest = VCLPresentationRequest(
                jwt = CommonMocks.JWT,
                jwkPublic = VCLJwkPublic(valueStr = "{}"),
                deepLink = VCLDeepLink(value = "")
            ),
            verifiableCredentials = listOf()
        )
        var result: VCLResult<VCLSubmissionResult>? = null

        subject.submit(
            submission = presentationSubmission,
            didJwk = didJwk
        ) {
            result = it
        }

        val expectedPresentationSubmissionResult =
            expectedPresentationSubmissionResult(
                PresentationSubmissionMocks.PresentationSubmissionResultJson.toJsonObject()!!,
                presentationSubmission.jti, submissionId = presentationSubmission.submissionId
            )

        val presentationSubmissionResult = result?.data

        assert(presentationSubmissionResult!!.token.value == expectedPresentationSubmissionResult.token.value)
        assert(presentationSubmissionResult.exchange.id == expectedPresentationSubmissionResult.exchange.id)
        assert(presentationSubmissionResult.jti == expectedPresentationSubmissionResult.jti)
        assert(presentationSubmissionResult.submissionId == expectedPresentationSubmissionResult.submissionId)
    }

    private fun expectedPresentationSubmissionResult(
        jsonObj: JSONObject,
        jti: String,
        submissionId: String
    ): VCLSubmissionResult {
        val exchangeJsonObj = jsonObj.optJSONObject(VCLSubmissionResult.CodingKeys.KeyExchange)!!
        return VCLSubmissionResult(
            token = VCLToken(value = (jsonObj[VCLSubmissionResult.CodingKeys.KeyToken] as String)),
            exchange = expectedExchange(exchangeJsonObj),
            jti = jti,
            submissionId = submissionId
        )
    }

    private fun expectedExchange(exchangeJsonObj: JSONObject): VCLExchange {
        return VCLExchange(
            id = (exchangeJsonObj.optString(VCLExchange.CodingKeys.KeyId)),
            type = (exchangeJsonObj.optString(VCLExchange.CodingKeys.KeyType)),
            disclosureComplete = (exchangeJsonObj[VCLExchange.CodingKeys.KeyDisclosureComplete] as Boolean),
            exchangeComplete = (exchangeJsonObj[VCLExchange.CodingKeys.KeyExchangeComplete] as Boolean)
        )
    }

    @After
    fun tearDown() {
    }
}