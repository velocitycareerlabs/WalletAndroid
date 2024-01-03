/**
 * Created by Michael Avoyan on 12/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.repositories.FinalizeOffersRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.GenerateOffersRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.FinalizeOffersUseCaseImpl
import io.velocitycareerlabs.impl.data.usecases.GenerateOffersUseCaseImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialDidVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialIssuerVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialsByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.OffersByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.domain.usecases.FinalizeOffersUseCase
import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.CommonMocks
import io.velocitycareerlabs.infrastructure.resources.CredentialTypesModelMock
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialMocks
import io.velocitycareerlabs.infrastructure.resources.valid.GenerateOffersMocks
import io.velocitycareerlabs.infrastructure.resources.valid.JsonLdMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class FinalizeOffersUseCaseTest {

    lateinit var subject: FinalizeOffersUseCase

    private lateinit var didJwk: VCLDidJwk
    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)
    private lateinit var credentialManifestFailed: VCLCredentialManifest
    private lateinit var credentialManifestPassed: VCLCredentialManifest
    private lateinit var finalizeOffersDescriptorFailed: VCLFinalizeOffersDescriptor
    private lateinit var finalizeOffersDescriptorPassed: VCLFinalizeOffersDescriptor
    private val vclJwtFailed = VCLJwt(encodedJwt = CredentialManifestMocks.JwtCredentialManifest1)
    private val vclJwtPassed =
        VCLJwt(encodedJwt = CredentialManifestMocks.JwtCredentialManifestFromRegularIssuer)

    private val credentialsAmount =
        CredentialMocks.JwtCredentialsFromRegularIssuer.toJsonArray()?.length()

    @Before
    fun setUp() {
        keyService.generateDidJwk(null) { didJwkResult ->
            didJwkResult.handleResult({
                didJwk = it
            }, {
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }
        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = VCLCredentialManifest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr2.toJsonObject()!!)
            )
        )
        GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = GenerateOffersMocks.GeneratedOffers)
            ),
            OffersByDeepLinkVerifierImpl(),
            EmptyExecutor()
        ).generateOffers(
            sessionToken = VCLToken(value = ""),
            generateOffersDescriptor = generateOffersDescriptor
        ) { result ->
            result.handleResult(
                successHandler = { offers ->
                    assert(
                        offers.all.toString().toCharArray().sort()
                                == GenerateOffersMocks.Offers.toCharArray().sort()
                    )
                    assert(offers.challenge == GenerateOffersMocks.Challenge)

                    credentialManifestFailed = VCLCredentialManifest(
                        jwt = vclJwtFailed,
                        verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr2.toJsonObject()!!)
                    )
                    credentialManifestPassed = VCLCredentialManifest(
                        jwt = vclJwtPassed,
                        verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr2.toJsonObject()!!)
                    )

                    finalizeOffersDescriptorFailed = VCLFinalizeOffersDescriptor(
                        credentialManifest = credentialManifestFailed,
                        offers = offers,
                        approvedOfferIds = listOf(),
                        rejectedOfferIds = listOf()
                    )
                    finalizeOffersDescriptorPassed = VCLFinalizeOffersDescriptor(
                        credentialManifest = credentialManifestPassed,
                        offers = offers,
                        approvedOfferIds = listOf(),
                        rejectedOfferIds = listOf()
                    )
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                })
        }
    }

    @Test
    fun testFailedCredentials() {
        subject = FinalizeOffersUseCaseImpl(
            FinalizeOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = CredentialMocks.JwtCredentialsFromRegularIssuer)
            ),
            JwtServiceRepositoryImpl(
                VCLJwtSignServiceLocalImpl(keyService),
                VCLJwtVerifyServiceLocalImpl()
            ),
            CredentialIssuerVerifierImpl(
                CredentialTypesModelMock(
                    issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
                ),
                NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
            ),
            CredentialDidVerifierImpl(),
            CredentialsByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        subject.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorFailed,
            didJwk = didJwk,
            sessionToken = VCLToken(value = ""),
            remoteCryptoServicesToken = null
        ) {
            it.handleResult(
                successHandler = { finalizeOffers ->
                    assert(finalizeOffers.failedCredentials.size == credentialsAmount)
                    assert(
                        finalizeOffers.failedCredentials.find { cred ->
                            cred.encodedJwt == CredentialMocks.JwtCredentialEducationDegreeRegistrationFromRegularIssuer
                        } != null
                    )
                    assert(
                        finalizeOffers.failedCredentials.find { cred ->
                            cred.encodedJwt == CredentialMocks.JwtCredentialEmploymentPastFromRegularIssuer
                        } != null
                    )
                    assert(finalizeOffers.passedCredentials.isEmpty())
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testPassedCredentials() {
        subject = FinalizeOffersUseCaseImpl(
            FinalizeOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = CredentialMocks.JwtCredentialsFromRegularIssuer)
            ),
            JwtServiceRepositoryImpl(
                VCLJwtSignServiceLocalImpl(keyService),
                VCLJwtVerifyServiceLocalImpl()
            ),
            CredentialIssuerVerifierImpl(
                CredentialTypesModelMock(
                    issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
                ),
                NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
            ),
            CredentialDidVerifierImpl(),
            CredentialsByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        subject.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorPassed,
            didJwk = didJwk,
            sessionToken = VCLToken(value = ""),
            remoteCryptoServicesToken = null
        ) {
            it.handleResult(
                successHandler = { finalizeOffers ->
                    assert(finalizeOffers.passedCredentials.size == credentialsAmount)
                    assert(
                        finalizeOffers.passedCredentials.find { cred ->
                            cred.encodedJwt == CredentialMocks.JwtCredentialEducationDegreeRegistrationFromRegularIssuer
                        } != null
                    )
                    assert(
                        finalizeOffers.passedCredentials.find { cred ->
                            cred.encodedJwt == CredentialMocks.JwtCredentialEmploymentPastFromRegularIssuer
                        } != null
                    )
                    assert(finalizeOffers.failedCredentials.isEmpty())
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testEmptyCredentials() {
        // Arrange
        subject = FinalizeOffersUseCaseImpl(
            FinalizeOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = CredentialMocks.JwtEmptyCredentials)
            ),
            JwtServiceRepositoryImpl(
                VCLJwtSignServiceLocalImpl(keyService),
                VCLJwtVerifyServiceLocalImpl()
            ),
            CredentialIssuerVerifierImpl(
                CredentialTypesModelMock(
                    issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
                ),
                NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
            ),
            CredentialDidVerifierImpl(),
            CredentialsByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        subject.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorPassed,
            didJwk = didJwk,
            sessionToken = VCLToken(value = ""),
            remoteCryptoServicesToken = null
        ) {
            it.handleResult(
                successHandler = { finalizeOffers ->
                    assert(finalizeOffers.failedCredentials.isEmpty())
                    assert(finalizeOffers.passedCredentials.isEmpty())
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testFailure() {
        subject = FinalizeOffersUseCaseImpl(
            FinalizeOffersRepositoryImpl(
                NetworkServiceSuccess("wrong payload")
            ),
            JwtServiceRepositoryImpl(
                VCLJwtSignServiceLocalImpl(keyService),
                VCLJwtVerifyServiceLocalImpl()
            ),
            CredentialIssuerVerifierImpl(
                CredentialTypesModelMock(
                    issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
                ),
                NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
            ),
            CredentialDidVerifierImpl(),
            CredentialsByDeepLinkVerifierImpl(),
            EmptyExecutor()
        )

        subject.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorPassed,
            didJwk = didJwk,
            sessionToken = VCLToken(value = ""),
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
}