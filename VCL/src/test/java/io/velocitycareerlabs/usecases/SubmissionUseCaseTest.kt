/**
 * Created by Michael Avoyan on 5/1/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.mockk.spyk
import io.mockk.verify
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.repositories.HeaderKeys
import io.velocitycareerlabs.impl.data.repositories.HeaderValues
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.PresentationSubmissionRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.PresentationSubmissionUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.SubmissionUseCase
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.CommonMocks
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationSubmissionMocks
import io.velocitycareerlabs.infrastructure.resources.valid.TokenMocks
import io.velocitycareerlabs.infrastructure.utils.expectedSubmissionResult
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class SubmissionUseCaseTest {

    private lateinit var subject: SubmissionUseCase

    private val authToken = TokenMocks.AuthToken
    private lateinit var didJwk: VCLDidJwk
    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)
    private val networkServiceSuccessSpy = spyk(
        NetworkServiceSuccess(
            validResponse = PresentationSubmissionMocks.PresentationSubmissionResultJson
        )
    )

    private val expectedHeadersWithAccessToken =
        listOf(
            Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion),
            Pair(
                HeaderKeys.Authorization,
                "${HeaderValues.PrefixBearer} ${authToken.accessToken.value}"
            )
        )
    private val expectedHeadersWithoutAccessToken =
        listOf(
            Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion),
        )

    @Before
    fun setUp() {
        keyService.generateDidJwk(
            VCLDidJwkDescriptor(VCLSignatureAlgorithm.ES256)
        ) { jwkResult ->
            jwkResult.handleResult({
                didJwk = it
            }, {
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }

        subject = PresentationSubmissionUseCaseImpl(
            PresentationSubmissionRepositoryImpl(
                networkServiceSuccessSpy
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
        val expectedSubmissionResult =
            expectedSubmissionResult(
                PresentationSubmissionMocks.PresentationSubmissionResultJson.toJsonObject()!!,
                presentationSubmission.jti, submissionId = presentationSubmission.submissionId
            )

        subject.submit(
            submission = presentationSubmission
        ) {
            it.handleResult(
                { presentationSubmissionResult ->
                    assert(presentationSubmissionResult.sessionToken.value == expectedSubmissionResult.sessionToken.value)
                    assert(presentationSubmissionResult.exchange.id == expectedSubmissionResult.exchange.id)
                    assert(presentationSubmissionResult.jti == expectedSubmissionResult.jti)
                    assert(presentationSubmissionResult.submissionId == expectedSubmissionResult.submissionId)
                },
                {
                    assert(false) { "$it" }
                }
            )
        }

        verify(exactly = 1) {
            networkServiceSuccessSpy.sendRequest(
                endpoint = any(),
                body = any(),
                contentType = any(),
                method = any(),
                headers = expectedHeadersWithoutAccessToken,
                useCaches = any(),
                completionBlock = any()
            )
        }
    }

    @Test
    fun testSubmitPresentationTypeFeedSuccess() {
        val presentationSubmission = VCLPresentationSubmission(
            presentationRequest = VCLPresentationRequest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile("{}".toJsonObject()!!),
                deepLink = VCLDeepLink(value = ""),
                didJwk = didJwk
            ),
            verifiableCredentials = listOf()
        )

        val expectedSubmissionResult = expectedSubmissionResult(
            PresentationSubmissionMocks.PresentationSubmissionResultJson.toJsonObject()!!,
            presentationSubmission.jti,
            submissionId = presentationSubmission.submissionId
        )

        subject.submit(
            submission = presentationSubmission,
            authToken = authToken
        ) {
            it.handleResult(
                { presentationSubmissionResult ->
                    assert(presentationSubmissionResult.sessionToken.value == expectedSubmissionResult.sessionToken.value)
                    assert(presentationSubmissionResult.exchange.id == expectedSubmissionResult.exchange.id)
                    assert(presentationSubmissionResult.jti == expectedSubmissionResult.jti)
                    assert(presentationSubmissionResult.submissionId == expectedSubmissionResult.submissionId)
                },
                {
                    assert(false) { "$it" }
                }
            )
        }

        verify(exactly = 1) {
            networkServiceSuccessSpy.sendRequest(
                endpoint = any(),
                body = any(),
                contentType = any(),
                method = any(),
                headers = expectedHeadersWithAccessToken,
                useCaches = any(),
                completionBlock = any()
            )
        }
    }
}