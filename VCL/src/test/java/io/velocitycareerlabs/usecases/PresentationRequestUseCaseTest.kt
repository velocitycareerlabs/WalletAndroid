/**
 * Created by Michael Avoyan on 4/30/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.PresentationRequestRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.ResolveKidRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.PresentationRequestUseCaseImpl
import io.velocitycareerlabs.impl.data.verifiers.PresentationRequestByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.domain.usecases.PresentationRequestUseCase
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestMocks
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class PresentationRequestUseCaseTest {

    lateinit var subject: PresentationRequestUseCase
    private lateinit var didJwk: VCLDidJwk
    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)

    @Before
    fun setUp() {
        keyService.generateDidJwk(null) { didJwkResult ->
            didJwkResult.handleResult({
                didJwk = it
            }, {
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }
    }

    @Test
    fun testGetPresentationRequestSuccess() {
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
                VCLJwtSignServiceLocalImpl(VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)),
                VCLJwtVerifyServiceLocalImpl()
            ),
            PresentationRequestByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        subject.getPresentationRequest(
            presentationRequestDescriptor = VCLPresentationRequestDescriptor(
                deepLink = DeepLinkMocks.PresentationRequestDeepLinkDevNet,
                pushDelegate = VCLPushDelegate(
                    pushUrl = pushUrl,
                    pushToken = pushToken
                ),
                didJwk = didJwk,
                remoteCryptoServicesToken = VCLToken("some token")
            )
        ) {
            it.handleResult(
                successHandler = { presentationRequest ->
                    assert(
                        presentationRequest.publicJwk.valueStr.toCharArray().sort() ==
                                VCLPublicJwk(PresentationRequestMocks.JWK.toJsonObject()!!).valueStr.toCharArray().sort()
                    )
                    assert(
                        presentationRequest.publicJwk.valueJson.toString().toCharArray().sort() ==
                                VCLPublicJwk(PresentationRequestMocks.JWK.toJsonObject()!!).valueJson.toString().toCharArray().sort()
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
                    assert(presentationRequest.didJwk?.did == this.didJwk.did)
                    assert(presentationRequest.remoteCryptoServicesToken?.value == "some token")
                },
                errorHandler = {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testGetPresentationRequestFailure() {
        subject = PresentationRequestUseCaseImpl(
            PresentationRequestRepositoryImpl(
                NetworkServiceSuccess(validResponse = "wrong payload")
            ),
            ResolveKidRepositoryImpl(
                NetworkServiceSuccess(validResponse = PresentationRequestMocks.JWK)
            ),
            JwtServiceRepositoryImpl(
                VCLJwtSignServiceLocalImpl(VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)),
                VCLJwtVerifyServiceLocalImpl()
            ),
            PresentationRequestByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        subject.getPresentationRequest(
            presentationRequestDescriptor = VCLPresentationRequestDescriptor(
                deepLink = DeepLinkMocks.PresentationRequestDeepLinkDevNet
            )
        ) {
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
}