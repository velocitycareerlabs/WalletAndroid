/**
 * Created by Michael Avoyan on 5/1/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.PresentationSubmissionRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.PresentationSubmissionUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.PresentationSubmissionUseCase
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.CommonMocks
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationSubmissionMocks
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class PresentationSubmissionUseCaseTest {

    lateinit var subject: PresentationSubmissionUseCase
    private lateinit var didJwk: VCLDidJwk
    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)

    @Before
    fun setUp() {
        keyService.generateDidJwk(
            VCLDidJwkDescriptor(VCLSignatureAlgorithm.ES256)
        ) { jwkResult ->
            jwkResult.handleResult({
                didJwk = it
            } ,{
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }

        subject = PresentationSubmissionUseCaseImpl(
            PresentationSubmissionRepositoryImpl(
                NetworkServiceSuccess(validResponse = PresentationSubmissionMocks.PresentationSubmissionResultJson)
            ),
            JwtServiceRepositoryImpl(
                VCLJwtSignServiceLocalImpl(keyService),
                VCLJwtVerifyServiceLocalImpl()
            ),
            EmptyExecutor()
        )
    }

    @Test
    fun testSubmitPresentationSuccess() {
        val presentationSubmission = VCLPresentationSubmission(
            presentationRequest = VCLPresentationRequest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile("{}".toJsonObject()!!),
                deepLink = VCLDeepLink(value = ""),
                didJwk = didJwk
            ),
            verifiableCredentials = listOf()
        )
        val expectedPresentationSubmissionResult =
            expectedPresentationSubmissionResult(
                PresentationSubmissionMocks.PresentationSubmissionResultJson.toJsonObject()!!,
                presentationSubmission.jti, submissionId = presentationSubmission.submissionId
            )

        subject.submit(
            submission = presentationSubmission
        ) {
            it.handleResult(
                { presentationSubmissionResult ->
                    assert(presentationSubmissionResult.sessionToken.value == expectedPresentationSubmissionResult.sessionToken.value)
                    assert(presentationSubmissionResult.exchange.id == expectedPresentationSubmissionResult.exchange.id)
                    assert(presentationSubmissionResult.jti == expectedPresentationSubmissionResult.jti)
                    assert(presentationSubmissionResult.submissionId == expectedPresentationSubmissionResult.submissionId)
                },
                {
                    assert(false) { "$it" }
                }
            )
        }
    }

    private fun expectedPresentationSubmissionResult(
        jsonObj: JSONObject,
        jti: String,
        submissionId: String
    ): VCLSubmissionResult {
        val exchangeJsonObj = jsonObj.optJSONObject(VCLSubmissionResult.CodingKeys.KeyExchange)!!
        return VCLSubmissionResult(
            sessionToken = VCLToken(value = (jsonObj[VCLSubmissionResult.CodingKeys.KeyToken] as String)),
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
}