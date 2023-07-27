/**
 * Created by Michael Avoyan on 12/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.utils

import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLErrorCode
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.utils.CredentialIssuerVerifierImpl
import io.velocitycareerlabs.impl.domain.utils.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toListOfStrings
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.CredentialTypesModelMock
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialMocks
import io.velocitycareerlabs.infrastructure.resources.valid.JsonLdMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

internal class CredentialIssuerVerifierTest {

    lateinit var subject: CredentialIssuerVerifier

    private val OffersMock = VCLOffers(JSONObject(), JSONArray(), 1, VCLToken(""), "")

    lateinit var finalizeOffersDescriptorWithoutPermittedServices: VCLFinalizeOffersDescriptor
    lateinit var credentialManifestWithoutPermittedServices: VCLCredentialManifest

    lateinit var finalizeOffersDescriptorOfNotaryIssuer: VCLFinalizeOffersDescriptor
    lateinit var credentialManifestFromNotaryIssuer: VCLCredentialManifest

    lateinit var finalizeOffersDescriptorOfRegularIssuer: VCLFinalizeOffersDescriptor
    lateinit var credentialManifestFromRegularIssuer: VCLCredentialManifest

    lateinit var finalizeOffersDescriptorOfIdentityIssuer: VCLFinalizeOffersDescriptor
    lateinit var credentialManifestFromIdentityIssuer: VCLCredentialManifest

    @Before
    fun setUp() {
        credentialManifestWithoutPermittedServices = VCLCredentialManifest(
            jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifestFromNotaryIssuer),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileWithoutServices.toJsonObject()!!)
        )
        finalizeOffersDescriptorWithoutPermittedServices = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifestWithoutPermittedServices,
            offers = OffersMock,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )

        credentialManifestFromNotaryIssuer = VCLCredentialManifest(
            jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifestFromNotaryIssuer),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileOfNotaryIssuer.toJsonObject()!!)
        )
        finalizeOffersDescriptorOfNotaryIssuer = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifestFromNotaryIssuer,
            offers = OffersMock,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )

        credentialManifestFromRegularIssuer = VCLCredentialManifest(
            jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifestFromRegularIssuer),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileOfRegularIssuer.toJsonObject()!!)
        )
        finalizeOffersDescriptorOfRegularIssuer = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifestFromRegularIssuer,
            offers = OffersMock,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )

        credentialManifestFromIdentityIssuer = VCLCredentialManifest(
            jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifestFromRegularIssuer),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileOfIdentityIssuer.toJsonObject()!!)
        )
        finalizeOffersDescriptorOfIdentityIssuer = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifestFromIdentityIssuer,
            offers = OffersMock,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )
    }

    @Test
    fun testVerifyNotaryIssuerSuccess() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.IssuerCategoryNotaryIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtCredentialsFromNotaryIssuer.toJsonArray()
            !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorOfNotaryIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(it)
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testVerifyNotaryIssuerMissingSubjectSuccess() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.IssuerCategoryNotaryIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtCredentialsWithoutSubject.toJsonArray()
            !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorOfNotaryIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(it)
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testVerifyRegularIssuerSuccess() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtCredentialsFromRegularIssuer.toJsonArray()
                !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorOfRegularIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(it)
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testVerifyRegularIssuerWrongDidFailed() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtCredentialsFromNotaryIssuer.toJsonArray()
            !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorOfRegularIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(false) { "${VCLErrorCode.IssuerRequiresNotaryPermission.value} error code is expected" }
                },
                errorHandler = { error ->
                    assert(error.errorCode == VCLErrorCode.IssuerRequiresNotaryPermission.value)
                }
            )
        }
    }

    @Test
    fun testVerifyIssuerWithoutServicesFailed() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.IssuerCategoryNotaryIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtCredentialsFromNotaryIssuer.toJsonArray()
            !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorWithoutPermittedServices,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(false) { "${VCLErrorCode.CredentialTypeNotRegistered.value} error code is expected" }
                },
                errorHandler = { error ->
                    assert(error.errorCode == VCLErrorCode.CredentialTypeNotRegistered.value)
                }
            )
        }
    }

    @Test
    fun testVerifyRegularIssuerMissingSubjectFailed() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtCredentialsWithoutSubject.toJsonArray()
            !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorOfRegularIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(false) { "${VCLErrorCode.InvalidCredentialSubjectContext.value} error code is expected" }
                },
                errorHandler = { error ->
                    assert(error.errorCode == VCLErrorCode.InvalidCredentialSubjectContext.value)
                }
            )
        }
    }

    @Test
    fun testVerifyIdentityIssuerSuccess() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryIdentityIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtCredentialsFromRegularIssuer.toJsonArray()
            !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorOfIdentityIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(it)
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testVerifyEmptyCredentialsSuccess() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryIdentityIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtEmptyCredentials.toJsonArray()
            !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorOfIdentityIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(it)
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testVerifyIdentityIssuerFailedWithoutIdentityService() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtCredentialsFromIdentityIssuer.toJsonArray()
            !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorOfIdentityIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(false) { "${VCLErrorCode.IssuerRequiresIdentityPermission.value} error code is expected" }
                },
                errorHandler = { error ->
                    assert(error.errorCode == VCLErrorCode.IssuerRequiresIdentityPermission.value)
                }
            )
        }
    }

    @Test
    fun testVerifyIssuerWithoutServicesFailedCompleteContextNotFound() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = ""),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtCredentialsFromRegularIssuer.toJsonArray()
            !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorOfRegularIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(false) { "${VCLErrorCode.InvalidCredentialSubjectContext.value} error code is expected" }
                },
                errorHandler = { error ->
                    assert(error.errorCode == VCLErrorCode.InvalidCredentialSubjectContext.value)
                }
            )
        }
    }

    @Test
    fun testVerifyIssuerWithoutServicesFailedPrimaryOrganizationNotFound() {
        subject = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10JsonldWithoutPrimaryOrganization),
        )

        subject.verifyCredentials(
            jwtEncodedCredentials = CredentialMocks.JwtCredentialsFromRegularIssuer.toJsonArray()
            !!.toListOfStrings(),
            finalizeOffersDescriptor = finalizeOffersDescriptorOfRegularIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = {
                    assert(false) { "${VCLErrorCode.InvalidCredentialSubjectType.value} error code is expected" }
                },
                errorHandler = { error ->
                    assert(error.errorCode == VCLErrorCode.InvalidCredentialSubjectType.value)
                }
            )
        }
    }
}