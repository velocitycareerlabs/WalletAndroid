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
    }

    @Test
    fun testGetVerifiedProfile() {
//        Arrange
        subject = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileJsonStr)
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLVerifiedProfile>? = null

//        Action
        subject.getVerifiedProfile(VerifiedProfileMocks.VerifiedProfileDescriptor) {
            result = it
        }

        val verifiedProfile = result!!.data!!
//        Assert
        assert(verifiedProfile.id == VerifiedProfileMocks.ExpectedId)
        assert(verifiedProfile.logo == VerifiedProfileMocks.ExpectedLogo)
        assert(verifiedProfile.name == VerifiedProfileMocks.ExpectedName)
    }

    @After
    fun tearDown() {
    }
}
