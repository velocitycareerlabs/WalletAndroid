/**
 * Created by Michael Avoyan on 10/28/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.repositories.VerifiedProfileRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.VerifiedProfileUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.VerifiedProfileUseCase
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class VerifiedProfileUseCaseTest {

    lateinit var subject: VerifiedProfileUseCase

    @Before
    fun setUp() {
        subject = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileJsonStr)
            ),
            EmptyExecutor()
        )
    }

    @Test
    fun testGetVerifiedProfileAnyServiceSuccess() {
        var result: VCLResult<VCLVerifiedProfile>? = null

        subject.getVerifiedProfile(
            VCLVerifiedProfileDescriptor(
                did = "did123"
            )
        ) {
            result = it
        }

        val verifiedProfile = result!!.data!!

        assert(verifiedProfile.id == VerifiedProfileMocks.ExpectedId)
        assert(verifiedProfile.logo == VerifiedProfileMocks.ExpectedLogo)
        assert(verifiedProfile.name == VerifiedProfileMocks.ExpectedName)
    }

    @Test
    fun testGetVerifiedProfileSuccess() {
        var result: VCLResult<VCLVerifiedProfile>? = null

        subject.getVerifiedProfile(
            VCLVerifiedProfileDescriptor(
                did = "did123",
                serviceType = VCLServiceType.Issuer
            )
        ) {
            result = it
        }

        val verifiedProfile = result!!.data!!

        assert(verifiedProfile.id == VerifiedProfileMocks.ExpectedId)
        assert(verifiedProfile.logo == VerifiedProfileMocks.ExpectedLogo)
        assert(verifiedProfile.name == VerifiedProfileMocks.ExpectedName)
    }

    @Test
    fun testGetVerifiedProfileError() {
        var result: VCLResult<VCLVerifiedProfile>? = null

        subject.getVerifiedProfile(
            VCLVerifiedProfileDescriptor(
                did = "did123",
                serviceType = VCLServiceType.IdentityIssuer
            )
        ) { verifiedProfileResult ->
            verifiedProfileResult.handleResult(
                {},
                { error ->
                    assert(error.code == VCLErrorCode.VerificationError.value)
                }
            )
        }
    }

    @After
    fun tearDown() {
    }
}
