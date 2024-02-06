/**
 * Created by Michael Avoyan on 10/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.repositories.CredentialManifestRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.ResolveKidRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.CredentialManifestUseCaseImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialManifestByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.domain.usecases.CredentialManifestUseCase
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class CredentialManifestUseCaseTest {

    lateinit var subject: CredentialManifestUseCase

    @Before
    fun setUp() {
    }

    @Test
    fun testGetCredentialManifestSuccess() {
        // Arrange
        subject = CredentialManifestUseCaseImpl(
            CredentialManifestRepositoryImpl(
                NetworkServiceSuccess(CredentialManifestMocks.CredentialManifest1)
            ),
            ResolveKidRepositoryImpl(
                NetworkServiceSuccess(CredentialManifestMocks.JWK)
            ),
            JwtServiceRepositoryImpl(
                VCLJwtSignServiceLocalImpl(VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)),
                VCLJwtVerifyServiceLocalImpl()
            ),
            CredentialManifestByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        subject.getCredentialManifest(
            credentialManifestDescriptor = VCLCredentialManifestDescriptorByDeepLink(
                deepLink = DeepLinkMocks.CredentialManifestDeepLinkDevNet,
                issuingType = VCLIssuingType.Career
            ),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!),
            remoteCryptoServicesToken = null
        ) {
            it.handleResult(
                { credentialManifest ->
                    assert(credentialManifest.jwt.encodedJwt == CredentialManifestMocks.JwtCredentialManifest1)
                    JSONAssert.assertEquals(
                        credentialManifest.jwt.header.toString(),
                        CredentialManifestMocks.Header,
                        JSONCompareMode.LENIENT
                    )
                    assert(
                        credentialManifest.jwt.payload.toString()
                            .replace("$", "")
                            .toCharArray()
                            .sort().toString() ==
                                CredentialManifestMocks.Payload.toString()
                                    .replace("$", "")
                                    .toCharArray()
                                    .sort().toString()
                    ) //removed $ to compare
                    assert(credentialManifest.jwt.signature.toString() == CredentialManifestMocks.Signature)
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testGetCredentialManifestFailure() {
        // Arrange
        subject = CredentialManifestUseCaseImpl(
            CredentialManifestRepositoryImpl(
                NetworkServiceSuccess("wrong payload")
            ),
            ResolveKidRepositoryImpl(
                NetworkServiceSuccess(CredentialManifestMocks.JWK)
            ),
            JwtServiceRepositoryImpl(
                VCLJwtSignServiceLocalImpl(VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)),
                VCLJwtVerifyServiceLocalImpl()
            ),
            CredentialManifestByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        subject.getCredentialManifest(
            credentialManifestDescriptor = VCLCredentialManifestDescriptorByDeepLink(
                deepLink = DeepLinkMocks.CredentialManifestDeepLinkDevNet,
                issuingType = VCLIssuingType.Career
            ),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!),
            remoteCryptoServicesToken = null
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

    @After
    fun tearDown() {
    }
}