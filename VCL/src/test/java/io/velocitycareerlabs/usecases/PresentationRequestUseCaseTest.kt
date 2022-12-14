/**
 * Created by Michael Avoyan on 4/30/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.PresentationRequestRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.ResolveKidRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.PresentationRequestUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.PresentationRequestUseCase
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.JwtServiceMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestMocks
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class PresentationRequestUseCaseTest {

    lateinit var subject: PresentationRequestUseCase

    @Before
    fun setUp() {
    }

    @Test
    fun testGetPresentationRequestDevNet() {
//        Arrange
        val pushUrl = "push_url"
        val pushToken = "push_token"
        subject = PresentationRequestUseCaseImpl(
                PresentationRequestRepositoryImpl(
                        NetworkServiceSuccess(PresentationRequestMocks.EncodedPresentationRequestResponse)
                ),
                ResolveKidRepositoryImpl(
                        NetworkServiceSuccess(JwtServiceMocks.JWK)
                ),
                JwtServiceRepositoryImpl(
                        JwtServiceImpl()
                ),
                EmptyExecutor()
        )
        var result: VCLResult<VCLPresentationRequest>? = null

//        Action
        subject.getPresentationRequest(VCLPresentationRequestDescriptor(
            deepLink = DeepLinkMocks.PresentationRequestDeepLinkDevNet,
            pushDelegate = VCLPushDelegate(
                pushUrl = pushUrl,
                pushToken = pushToken
            )
        )) {
            result = it
        }

//        Assert
        assert(result!!.data!!.jwt.signedJwt.serialize() == PresentationRequestMocks.EncodedPresentationRequest)
        assert(result!!.data!!.jwkPublic.valueStr == VCLJwkPublic(JwtServiceMocks.JWK).valueStr)

        assert(result!!.data!!.pushDelegate!!.pushUrl == pushUrl)
        assert(result!!.data!!.pushDelegate!!.pushToken == pushToken)
    }

    @After
    fun tearDown() {
    }
}