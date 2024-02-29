/**
 * Created by Michael Avoyan on 12/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.verifiers

import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.verifiers.CredentialIssuerVerifierImpl
import io.velocitycareerlabs.impl.domain.verifiers.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toJwtList
import io.velocitycareerlabs.impl.utils.VCLLog
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.CredentialTypesModelMock
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.JsonLdMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

internal class CredentialIssuerVerifierTest {
    private lateinit var subject1: CredentialIssuerVerifier
    private lateinit var subject2: CredentialIssuerVerifier
    private lateinit var subject3: CredentialIssuerVerifier
    private lateinit var subject4: CredentialIssuerVerifier
    private lateinit var subject5: CredentialIssuerVerifier
    private lateinit var subject6: CredentialIssuerVerifier
    private lateinit var subject7: CredentialIssuerVerifier
    private lateinit var subject8: CredentialIssuerVerifier
    private lateinit var subject9: CredentialIssuerVerifier
    private lateinit var subject10: CredentialIssuerVerifier
    private lateinit var subject11: CredentialIssuerVerifier
    private lateinit var subject12: CredentialIssuerVerifier
    private lateinit var subject13: CredentialIssuerVerifier
    private lateinit var subjectQa: CredentialIssuerVerifier

    private val OffersMock = VCLOffers(JSONObject(), listOf(), 1, VCLToken(""), "")

    private lateinit var finalizeOffersDescriptorWithoutPermittedServices: VCLFinalizeOffersDescriptor
    private lateinit var credentialManifestWithoutPermittedServices: VCLCredentialManifest

    private lateinit var finalizeOffersDescriptorOfNotaryIssuer: VCLFinalizeOffersDescriptor
    private lateinit var credentialManifestFromNotaryIssuer: VCLCredentialManifest

    private lateinit var finalizeOffersDescriptorOfRegularIssuer: VCLFinalizeOffersDescriptor
    private lateinit var credentialManifestFromRegularIssuer: VCLCredentialManifest

    private lateinit var finalizeOffersDescriptorOfIdentityIssuer: VCLFinalizeOffersDescriptor
    private lateinit var credentialManifestFromIdentityIssuer: VCLCredentialManifest

    private lateinit var finalizeOffersDescriptorOfMicrosoftQa: VCLFinalizeOffersDescriptor
    private lateinit var CredentialManifestForValidCredentialMicrsoftQa: VCLCredentialManifest
    private lateinit var CredentialManifestForInvalidCredentialMicrsoftQa: VCLCredentialManifest

    @Before
    fun setUp() {
        setUpSubjectProperties()
        setUpSubjects()
    }

    private fun setUpSubjectProperties() {
        credentialManifestWithoutPermittedServices = VCLCredentialManifest(
            jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifestFromNotaryIssuer),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileWithoutServices.toJsonObject()!!),
            didJwk = DidJwkMocks.DidJwk
        )
        finalizeOffersDescriptorWithoutPermittedServices = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifestWithoutPermittedServices,
            offers = OffersMock,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )

        credentialManifestFromNotaryIssuer = VCLCredentialManifest(
            jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifestFromNotaryIssuer),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileOfNotaryIssuer.toJsonObject()!!),
            didJwk = DidJwkMocks.DidJwk
        )
        finalizeOffersDescriptorOfNotaryIssuer = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifestFromNotaryIssuer,
            offers = OffersMock,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )

        credentialManifestFromRegularIssuer = VCLCredentialManifest(
            jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifestFromRegularIssuer),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileOfRegularIssuer.toJsonObject()!!),
            didJwk = DidJwkMocks.DidJwk
        )
        finalizeOffersDescriptorOfRegularIssuer = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifestFromRegularIssuer,
            offers = OffersMock,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )

        credentialManifestFromIdentityIssuer = VCLCredentialManifest(
            jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifestFromRegularIssuer),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileOfIdentityIssuer.toJsonObject()!!),
            didJwk = DidJwkMocks.DidJwk
        )
        finalizeOffersDescriptorOfIdentityIssuer = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifestFromIdentityIssuer,
            offers = OffersMock,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )

        CredentialManifestForValidCredentialMicrsoftQa = VCLCredentialManifest(
            jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifestForValidCredentialMicrsoftQa),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerInspectorMicrosoftQa.toJsonObject()!!),
            didJwk = DidJwkMocks.DidJwk
        )
        CredentialManifestForInvalidCredentialMicrsoftQa = VCLCredentialManifest(
            jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifestForInvalidCredentialMicrsoftQa),
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerInspectorMicrosoftQa.toJsonObject()!!),
            didJwk = DidJwkMocks.DidJwk
        )
        finalizeOffersDescriptorOfMicrosoftQa = VCLFinalizeOffersDescriptor(
            credentialManifest = CredentialManifestForValidCredentialMicrsoftQa,
            offers = OffersMock,
            approvedOfferIds = listOf(),
            rejectedOfferIds = listOf()
        )
    }

    private fun setUpSubjects() {
        subject1 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject2 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject3 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.IssuerCategoryNotaryIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject4 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.IssuerCategoryNotaryIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject5 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject6 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject7 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.IssuerCategoryNotaryIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject8 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject9 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.IssuerCategoryIdDocumentIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject10 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.IssuerCategoryNotaryContactIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject11 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10Jsonld),
        )
        subject12 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = ""),
        )
        subject13 = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10JsonldWithoutPrimaryOrganization),
        )
        subjectQa = CredentialIssuerVerifierImpl(
            CredentialTypesModelMock(
                issuerCategory = CredentialTypesModelMock.issuerCategoryRegularIssuer
            ),
            NetworkServiceSuccess(validResponse = JsonLdMocks.Layer1v10JsonldQa)
        )
    }

    @Test
    fun testVerifyMicrosoftValidCredentialQa() {
        subjectQa.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtValidEmploymentCredentialsFromMicrosoftQa.toJwtList()!!,
            finalizeOffersDescriptor = finalizeOffersDescriptorOfMicrosoftQa
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
    fun testVerifyMicrosoftInvalidCredentialQa() {
        subjectQa.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtInvalidEmploymentCredentialsFromMicrosoftQa.toJwtList()!!,
            finalizeOffersDescriptor = finalizeOffersDescriptorOfMicrosoftQa
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

//    @Test
//    fun testVerifyOpenBadgeCredentialSuccess() {
//        subject1.verifyCredentials(
//            jwtCredentials = CredentialMocks.JwtCredentialsOpenBadgeValid.toJwtList()!!,
//            finalizeOffersDescriptor = finalizeOffersDescriptorOfRegularIssuer,
//        ) { verificationResult ->
//            verificationResult.handleResult(
//                successHandler = {
//                    assert(it)
//                },
//                errorHandler = { error ->
//                    assert(false) { "${error.toJsonObject()}" }
//                }
//            )
//        }
//    }

//    @Test
//    fun testVerifyOpenBadgeCredentialError() {
//        subject2.verifyCredentials(
//            jwtCredentials = CredentialMocks.JwtCredentialsOpenBadgeInvalid.toJwtList()!!,
//            finalizeOffersDescriptor = finalizeOffersDescriptorOfRegularIssuer,
//        ) { verificationResult ->
//            verificationResult.handleResult(
//                successHandler = {
//                    assert(it)
//                },
//                errorHandler = { error ->
//                    assert(false) { "${error.toJsonObject()}" }
//                }
//            )
//        }
//    }

    @Test
    fun testVerifyNotaryIssuerSuccess() {
        subject3.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsFromNotaryIssuer.toJwtList()!!,
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
        subject4.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsWithoutSubject.toJwtList()!!,
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
        subject5.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsFromRegularIssuer.toJwtList()!!,
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
        subject6.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsFromNotaryIssuer.toJwtList()!!,
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
        subject7.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsFromNotaryIssuer.toJwtList()!!,
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
        subject8.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsWithoutSubject.toJwtList()!!,
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
        subject9.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsFromRegularIssuer.toJwtList()!!,
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
        subject10.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtEmptyCredentials.toJwtList()!!,
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
        subject11.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsFromIdentityIssuer.toJwtList()!!,
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
        subject12.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsFromRegularIssuer.toJwtList()!!,
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
    fun testVerifyIssuerPrimaryOrganizationNotFound() {
        subject13.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsFromRegularIssuer.toJwtList()!!,
            finalizeOffersDescriptor = finalizeOffersDescriptorOfRegularIssuer,
        ) { verificationResult ->
            verificationResult.handleResult(
                successHandler = { isVerified ->
                    assert(isVerified) // K not found => verification passed
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }
}