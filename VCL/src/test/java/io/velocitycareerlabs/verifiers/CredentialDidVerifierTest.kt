/**
 * Created by Michael Avoyan on 12/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.verifiers

import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.verifiers.CredentialDidVerifierImpl
import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toJwtList
import io.velocitycareerlabs.impl.extensions.toList
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

internal class CredentialDidVerifierTest {

    private val subject = CredentialDidVerifierImpl()
    private val credentialsFromNotaryIssuerAmount =
        CredentialMocks.JwtCredentialsFromNotaryIssuer.toJsonArray()?.length()
    private val credentialsFromRegularIssuerAmount =
        CredentialMocks.JwtCredentialsFromRegularIssuer.toJsonArray()?.length()
    private val OffersMock = VCLOffers(JSONObject(), listOf(), 1, VCLToken(".."), "")

    lateinit var finalizeOffersDescriptorOfNotaryIssuer: VCLFinalizeOffersDescriptor
    lateinit var credentialManifestFromNotaryIssuer: VCLCredentialManifest

    lateinit var finalizeOffersDescriptorOfRegularIssuer: VCLFinalizeOffersDescriptor
    lateinit var credentialManifestFromRegularIssuer: VCLCredentialManifest

    @Before
    fun setUp() {
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
    }

    @Test
    fun testVerifyCredentialsSuccess() {
        subject.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsFromNotaryIssuer.toJwtList()!!,
            finalizeOffersDescriptor = finalizeOffersDescriptorOfNotaryIssuer
        ) { verifiableCredentialsResult ->
            verifiableCredentialsResult.handleResult(
                successHandler = { verifiableCredentials ->
                    assert(verifiableCredentials.passedCredentials.size == credentialsFromNotaryIssuerAmount)
                    assert(
                        verifiableCredentials.passedCredentials.find {
                            it.encodedJwt == CredentialMocks.JwtCredentialEmploymentPastFromNotaryIssuer
                        } != null
                    )
                    assert(
                        verifiableCredentials.passedCredentials.find {
                            it.encodedJwt == CredentialMocks.JwtCredentialEducationDegreeRegistrationFromNotaryIssuer
                        } != null
                    )
                    assert(verifiableCredentials.failedCredentials.isEmpty())
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testVerifyCredentialsFailed() {
        subject.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtCredentialsFromRegularIssuer.toJwtList()!!,
            finalizeOffersDescriptor = finalizeOffersDescriptorOfNotaryIssuer
        ) { verifiableCredentialsResult ->
            verifiableCredentialsResult.handleResult(
                successHandler = { verifiableCredentials ->
                    assert(verifiableCredentials.failedCredentials.size == credentialsFromRegularIssuerAmount)
                    assert(
                        verifiableCredentials.failedCredentials.find {
                            it.encodedJwt == CredentialMocks.JwtCredentialEmploymentPastFromRegularIssuer
                        } != null
                    )
                    assert(
                        verifiableCredentials.failedCredentials.find {
                            it.encodedJwt == CredentialMocks.JwtCredentialEducationDegreeRegistrationFromRegularIssuer
                        } != null
                    )
                    assert(verifiableCredentials.passedCredentials.isEmpty())
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testVerifyCredentials1Passed1Failed() {
        subject.verifyCredentials(
            jwtCredentials = "[\"${CredentialMocks.JwtCredentialEmploymentPastFromNotaryIssuer}\", \"${CredentialMocks.JwtCredentialEmailFromIdentityIssuer}\"]"
                .toJwtList()!!,
            finalizeOffersDescriptor = finalizeOffersDescriptorOfNotaryIssuer
        ) { verifiableCredentialsResult ->
            verifiableCredentialsResult.handleResult(
                successHandler = { verifiableCredentials ->
                    assert(verifiableCredentials.passedCredentials.size == 1)
                    assert(
                        verifiableCredentials.passedCredentials.find {
                            it.encodedJwt == CredentialMocks.JwtCredentialEmploymentPastFromNotaryIssuer
                        } != null
                    )

                    assert(verifiableCredentials.failedCredentials.size == 1)
                    assert(
                        verifiableCredentials.failedCredentials.find {
                            it.encodedJwt == CredentialMocks.JwtCredentialEmailFromIdentityIssuer
                        } != null
                    )
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testVerifyCredentialsEmpty() {
        subject.verifyCredentials(
            jwtCredentials = CredentialMocks.JwtEmptyCredentials.toJwtList()!!,
            finalizeOffersDescriptor = finalizeOffersDescriptorOfNotaryIssuer
        ) { verifiableCredentialsResult ->
            verifiableCredentialsResult.handleResult(
                successHandler = { verifiableCredentials ->
                    assert(verifiableCredentials.passedCredentials.isEmpty())
                    assert(verifiableCredentials.failedCredentials.isEmpty())
                },
                errorHandler = { error ->
                    assert(false) { "${error.toJsonObject()}" }
                }
            )
        }
    }
}