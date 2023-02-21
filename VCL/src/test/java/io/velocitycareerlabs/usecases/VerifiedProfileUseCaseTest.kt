/**
 * Created by Michael Avoyan on 10/28/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.VCLServiceType
import io.velocitycareerlabs.api.entities.VCLServiceTypes
import io.velocitycareerlabs.impl.data.repositories.VerifiedProfileRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.VerifiedProfileUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.VerifiedProfileUseCase
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class VerifiedProfileUseCaseTest {

    lateinit var subject: VerifiedProfileUseCase

    @Test
    fun testGetVerifiedProfileIssuerSuccess() {
        subject = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr)
            ),
            EmptyExecutor()
        )
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
    fun testGetVerifiedProfileIssuerInspector1Success() {
        subject = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileIssuerInspectorJsonStr)
            ),
            EmptyExecutor()
        )

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
    fun testGetVerifiedProfileIssuerInspector2Success() {
        subject = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileIssuerInspectorJsonStr)
            ),
            EmptyExecutor()
        )

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
    fun testGetVerifiedProfileIssuerNotaryIssuer2Success() {
        subject = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileNotaryIssuerJsonStr)
            ),
            EmptyExecutor()
        )

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
    fun testGetVerifiedProfileIssuerNotaryIssuerSuccess() {
        subject = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileNotaryIssuerJsonStr)
            ),
            EmptyExecutor()
        )

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
}
