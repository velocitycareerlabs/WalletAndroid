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
import io.velocitycareerlabs.impl.data.repositories.ResolveDidDocumentRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.FinalizeOffersUseCaseImpl
import io.velocitycareerlabs.impl.data.usecases.GenerateOffersUseCaseImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialDidVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.directissuerverification.CredentialIssuerVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialsByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.OffersByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.directissuerverification.repositories.CredentialSubjectContextRepositoryImpl
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
import io.velocitycareerlabs.infrastructure.resources.valid.DidDocumentMocks
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

    private lateinit var subject1: FinalizeOffersUseCase
    private lateinit var subject2: FinalizeOffersUseCase
    private lateinit var subject3: FinalizeOffersUseCase
    private lateinit var subject4: FinalizeOffersUseCase

    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)
    private lateinit var didJwk: VCLDidJwk
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
        keyService.generateDidJwk { jwkResult ->
            jwkResult.handleResult({
                didJwk = it
            } ,{
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }

        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = VCLCredentialManifest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr2.toJsonObject()!!),
                didJwk = didJwk
            )
        )
        GenerateOffersUseCaseImpl(
            GenerateOffersRepositoryImpl(
                NetworkServiceSuccess(validResponse = GenerateOffersMocks.GeneratedOffers)
            ),
            OffersByDeepLinkVerifierImpl(
                ResolveDidDocumentRepositoryImpl(
                    NetworkServiceSuccess(DidDocumentMocks.DidDocumentMockStr)
                )
            ),
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
                        verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr2.toJsonObject()!!),
                        didJwk = didJwk
                    )
                    credentialManifestPassed = VCLCredentialManifest(
                        jwt = vclJwtPassed,
                        verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr2.toJsonObject()!!),
                        didJwk = didJwk
                    )

                    finalizeOffersDescriptorFailed = VCLFinalizeOffersDescriptor(
                        credentialManifest = credentialManifestFailed,
                        challenge = offers.challenge,
                        approvedOfferIds = listOf(),
                        rejectedOfferIds = listOf()
                    )
                    finalizeOffersDescriptorPassed = VCLFinalizeOffersDescriptor(
                        credentialManifest = credentialManifestPassed,
                        challenge = offers.challenge,
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
        subject1 = FinalizeOffersUseCaseImpl(
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
                CredentialSubjectContextRepositoryImpl(
                    NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld)
                )
            ),
            CredentialDidVerifierImpl(),
            CredentialsByDeepLinkVerifierImpl(
                ResolveDidDocumentRepositoryImpl(
                    NetworkServiceSuccess(DidDocumentMocks.DidDocumentMockStr)
                )
            ),
            EmptyExecutor()
        )

        subject1.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorFailed,
            sessionToken = VCLToken(value = "")
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
        subject2 = FinalizeOffersUseCaseImpl(
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
                CredentialSubjectContextRepositoryImpl(
                    NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld)
                )
            ),
            CredentialDidVerifierImpl(),
            CredentialsByDeepLinkVerifierImpl(
                ResolveDidDocumentRepositoryImpl(
                    NetworkServiceSuccess(DidDocumentMocks.DidDocumentMockStr)
                )
            ),
            EmptyExecutor()
        )

        subject2.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorPassed,
            sessionToken = VCLToken(value = ""),
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
        subject3 = FinalizeOffersUseCaseImpl(
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
                CredentialSubjectContextRepositoryImpl(
                    NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld)
                )
            ),
            CredentialDidVerifierImpl(),
            CredentialsByDeepLinkVerifierImpl(
                ResolveDidDocumentRepositoryImpl(
                    NetworkServiceSuccess(DidDocumentMocks.DidDocumentMockStr)
                )
            ),
            EmptyExecutor()
        )

        subject3.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorPassed,
            sessionToken = VCLToken(value = "")
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
        subject4 = FinalizeOffersUseCaseImpl(
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
                CredentialSubjectContextRepositoryImpl(
                    NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld)
                )
            ),
            CredentialDidVerifierImpl(),
            CredentialsByDeepLinkVerifierImpl(
                ResolveDidDocumentRepositoryImpl(
                    NetworkServiceSuccess(DidDocumentMocks.DidDocumentMockStr)
                )
            ),
            EmptyExecutor()
        )

        subject4.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptorPassed,
            sessionToken = VCLToken(value = "")
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