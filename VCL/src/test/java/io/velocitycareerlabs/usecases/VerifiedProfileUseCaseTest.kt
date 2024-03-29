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
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.junit.Test

internal class VerifiedProfileUseCaseTest {

    private lateinit var subject1: VerifiedProfileUseCase
    private lateinit var subject2: VerifiedProfileUseCase
    private lateinit var subject3: VerifiedProfileUseCase
    private lateinit var subject4: VerifiedProfileUseCase
    private lateinit var subject5: VerifiedProfileUseCase

    @Test
    fun testGetVerifiedProfileIssuerSuccess() {
        subject1 = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1)
            ),
            EmptyExecutor()
        )

        subject1.getVerifiedProfile(
            VCLVerifiedProfileDescriptor(
                did = "did123"
            )
        ) {
            it.handleResult(
                { verifiedProfile ->
                    assert(verifiedProfile.id == VerifiedProfileMocks.ExpectedId)
                    assert(verifiedProfile.logo == VerifiedProfileMocks.ExpectedLogo)
                    assert(verifiedProfile.name == VerifiedProfileMocks.ExpectedName)
                },
                {
                    assert(false) { "$it" }
                }
            )
        }
    }

    @Test
    fun testGetVerifiedProfileIssuerInspector1Success() {
        subject2 = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileIssuerInspectorJsonStr)
            ),
            EmptyExecutor()
        )

        subject2.getVerifiedProfile(
            VCLVerifiedProfileDescriptor(
                did = "did123"
            )
        ) {
            it.handleResult(
                { verifiedProfile ->
                    assert(verifiedProfile.id == VerifiedProfileMocks.ExpectedId)
                    assert(verifiedProfile.logo == VerifiedProfileMocks.ExpectedLogo)
                    assert(verifiedProfile.name == VerifiedProfileMocks.ExpectedName)
                },
                {
                    assert(false) { "$it" }
                }
            )
        }
    }

    @Test
    fun testGetVerifiedProfileIssuerInspector2Success() {
        subject3 = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileIssuerInspectorJsonStr)
            ),
            EmptyExecutor()
        )

        subject3.getVerifiedProfile(
            VCLVerifiedProfileDescriptor(
                did = "did123"
            )
        ) {
            it.handleResult(
                { verifiedProfile ->
                    assert(verifiedProfile.id == VerifiedProfileMocks.ExpectedId)
                    assert(verifiedProfile.logo == VerifiedProfileMocks.ExpectedLogo)
                    assert(verifiedProfile.name == VerifiedProfileMocks.ExpectedName)
                },
                {
                    assert(false) { "$it" }
                }
            )
        }
    }

    @Test
    fun testGetVerifiedProfileIssuerNotaryIssuer2Success() {
        subject4 = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileNotaryIssuerJsonStr)
            ),
            EmptyExecutor()
        )

        subject4.getVerifiedProfile(
            VCLVerifiedProfileDescriptor(
                did = "did123"
            )
        ) {
            it.handleResult(
                { verifiedProfile ->
                    assert(verifiedProfile.id == VerifiedProfileMocks.ExpectedId)
                    assert(verifiedProfile.logo == VerifiedProfileMocks.ExpectedLogo)
                    assert(verifiedProfile.name == VerifiedProfileMocks.ExpectedName)
                },
                {
                    assert(false) { "$it" }
                }
            )
        }
    }

    @Test
    fun testGetVerifiedProfileIssuerNotaryIssuerSuccess() {
        subject5 = VerifiedProfileUseCaseImpl(
            VerifiedProfileRepositoryImpl(
                NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileNotaryIssuerJsonStr)
            ),
            EmptyExecutor()
        )

        subject5.getVerifiedProfile(
            VCLVerifiedProfileDescriptor(
                did = "did123"
            )
        ) {
            it.handleResult(
                { verifiedProfile ->
                    assert(verifiedProfile.id == VerifiedProfileMocks.ExpectedId)
                    assert(verifiedProfile.logo == VerifiedProfileMocks.ExpectedLogo)
                    assert(verifiedProfile.name == VerifiedProfileMocks.ExpectedName)
                },
                {
                    assert(false) { "$it" }
                }
            )
        }
    }
}
