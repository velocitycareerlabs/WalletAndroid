/**
 * Created by Michael Avoyan on 26/05/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.repositories

import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLDidJwkDescriptor
import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLPresentationSubmission
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.repositories.HeaderKeys
import io.velocitycareerlabs.impl.data.repositories.HeaderValues
import io.velocitycareerlabs.impl.data.repositories.PresentationSubmissionRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.SubmissionRepositoryImpl
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.CommonMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationSubmissionMocks
import io.velocitycareerlabs.infrastructure.resources.valid.TokenMocks
import io.velocitycareerlabs.infrastructure.utils.expectedSubmissionResult
import org.junit.Before
import org.junit.Test
import io.mockk.verify
import io.mockk.spyk

class SubmissionRepositoryTest {
    private lateinit var subject: SubmissionRepositoryImpl

    private val authToken = TokenMocks.AuthToken
    private lateinit var didJwk: VCLDidJwk
    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)

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

        subject = spyk(
            PresentationSubmissionRepositoryImpl(
                NetworkServiceSuccess(validResponse = PresentationSubmissionMocks.PresentationSubmissionResultJson)
            )
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
            submission = presentationSubmission,
            jwt = CommonMocks.JWT
        ) {
            it.handleResult(
                { submissionResult ->
                    assert(submissionResult.sessionToken.value == expectedSubmissionResult.sessionToken.value)
                    assert(submissionResult.exchange.id == expectedSubmissionResult.exchange.id)
                    assert(submissionResult.jti == expectedSubmissionResult.jti)
                    assert(submissionResult.submissionId == expectedSubmissionResult.submissionId)
                },
                {
                    assert(false) { "$it" }
                }
            )
        }

        verify(exactly = 1) {
            subject.generateHeader()
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
            jwt = CommonMocks.JWT,
            authToken = authToken
        ) {
            it.handleResult(
                { submissionResult ->
                    assert(submissionResult.sessionToken.value == expectedSubmissionResult.sessionToken.value)
                    assert(submissionResult.exchange.id == expectedSubmissionResult.exchange.id)
                    assert(submissionResult.jti == expectedSubmissionResult.jti)
                    assert(submissionResult.submissionId == expectedSubmissionResult.submissionId)
                },
                {
                    assert(false) { "$it" }
                }
            )
        }

        verify(exactly = 1) {
            subject.generateHeader(authToken.accessToken)
        }
    }

    @Test
    fun testGenerateHeaderWithAuthToken() {
        val header = subject.generateHeader(authToken.accessToken)

        assert(header.size == 2)

        assert(header[0].first == HeaderKeys.XVnfProtocolVersion)
        assert(header[0].second == HeaderValues.XVnfProtocolVersion)

        assert(header[1].first == HeaderKeys.Authorization)
        assert(header[1].second == "${HeaderValues.PrefixBearer} ${authToken.accessToken.value}")
    }

    @Test
    fun testGenerateHeaderWithoutAuthToken() {
        val header = subject.generateHeader()

        assert(header.size == 1)

        assert(header[0].first == HeaderKeys.XVnfProtocolVersion)
        assert(header[0].second == HeaderValues.XVnfProtocolVersion)
    }
}