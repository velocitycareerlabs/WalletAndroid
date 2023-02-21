/**
 * Created by Michael Avoyan on 10/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.repositories.CredentialManifestRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.ResolveKidRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.CredentialManifestUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.CredentialManifestUseCase
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class CredentialManifestUseCaseTest {

    lateinit var subject: CredentialManifestUseCase

    @Before
    fun setUp() {
    }

    @Test
    fun testGetCredentialManifest() {
        // Arrange
        subject = CredentialManifestUseCaseImpl(
            CredentialManifestRepositoryImpl(
                NetworkServiceSuccess(CredentialManifestMocks.CredentialManifestEncodedJwt)
            ),
            ResolveKidRepositoryImpl(
                NetworkServiceSuccess(CredentialManifestMocks.JWK)
            ),
            JwtServiceRepositoryImpl(
                JwtServiceImpl()
            ),
            EmptyExecutor()
        )
        var result: VCLResult<VCLCredentialManifest>? = null

        // Action
        subject.getCredentialManifest(
            VCLCredentialManifestDescriptorByDeepLink(
                DeepLinkMocks.CredentialManifestDeepLinkDevNet,
                issuingType = VCLIssuingType.Career
            )
        ) {
            result = it
        }

        // Assert
        assert(
            result!!.data!!.jwt.signedJwt.serialize() ==
                    JSONObject(CredentialManifestMocks.CredentialManifestEncodedJwt).optString("issuing_request")
        )
    }

    @After
    fun tearDown() {
    }
}