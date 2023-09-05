/**
 * Created by Michael Avoyan on 4/30/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.jwt.VCLJwtServiceImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.PresentationRequestRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.ResolveKidRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.PresentationRequestUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.PresentationRequestUseCase
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestMocks
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class PresentationRequestUseCaseTest {

    lateinit var subject: PresentationRequestUseCase

    @Test
    fun testCountryCodesSuccess() {
        // Arrange
        val pushUrl = "push_url"
        val pushToken = "push_token"
        subject = PresentationRequestUseCaseImpl(
            PresentationRequestRepositoryImpl(
                NetworkServiceSuccess(validResponse = PresentationRequestMocks.EncodedPresentationRequestResponse)
            ),
            ResolveKidRepositoryImpl(
                NetworkServiceSuccess(validResponse = PresentationRequestMocks.JWK)
            ),
            JwtServiceRepositoryImpl(
                VCLJwtServiceImpl(VCLKeyServiceImpl(SecretStoreServiceMock.Instance))
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLPresentationRequest>? = null

        // Action
        subject.getPresentationRequest(
            presentationRequestDescriptor = VCLPresentationRequestDescriptor(
                deepLink = DeepLinkMocks.PresentationRequestDeepLinkDevNet,
                pushDelegate = VCLPushDelegate(
                    pushUrl = pushUrl,
                    pushToken = pushToken
                )
            )
        ) {
            result = it
        }

        // Assert
        val presentationRequest = result?.data

        assert(
            presentationRequest!!.jwkPublic.valueStr.toCharArray().sort() ==
                    VCLJwkPublic(PresentationRequestMocks.JWK.toJsonObject()!!).valueStr.toCharArray().sort()
        )
        assert(
            presentationRequest.jwkPublic.valueJson.toString().toCharArray().sort() ==
                    VCLJwkPublic(PresentationRequestMocks.JWK.toJsonObject()!!).valueJson.toString().toCharArray().sort()
        )
        assert(presentationRequest.jwt.encodedJwt == PresentationRequestMocks.PresentationRequestJwt.encodedJwt)
        assert(
            presentationRequest.jwt.header.toString() ==
                    PresentationRequestMocks.PresentationRequestJwt.header.toString()
        )
        assert(
            presentationRequest.jwt.payload.toString().toCharArray().sort() ==
                    PresentationRequestMocks.PresentationRequestJwt.payload.toString().toCharArray().sort()
        )
        assert(presentationRequest.pushDelegate!!.pushUrl == pushUrl)
        assert(presentationRequest.pushDelegate!!.pushToken == pushToken)
    }
}