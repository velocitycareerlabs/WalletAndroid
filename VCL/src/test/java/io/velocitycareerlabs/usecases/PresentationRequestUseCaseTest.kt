package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLPublicKey
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.data
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.PresentationRequestRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.ResolveKidRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.PresentationRequestUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.PresentationRequestUseCase
import io.velocitycareerlabs.infrastructure.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.JwtServiceMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Michael Avoyan on 4/30/21.
 */
internal class PresentationRequestUseCaseTest {

    lateinit var subject: PresentationRequestUseCase

    @Before
    fun setUp() {
    }

    @Test
    fun testGetPresentationRequest() {
//        Arrange
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
        subject.getPresentationRequest(DeepLinkMocks.PresentationRequestDeepLink) {
            result = it
        }

//        Assert
        assert(result!!.data!!.jwt.signedJwt.serialize() == PresentationRequestMocks.EncodedPresentationRequest)
        assert(result!!.data!!.publicKey == VCLPublicKey(JwtServiceMocks.JWK))
    }

    @After
    fun tearDown() {
    }
}