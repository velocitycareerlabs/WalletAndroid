/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.verifiers

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.repositories.ResolveDidDocumentRepositoryImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialsByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.domain.verifiers.CredentialsByDeepLinkVerifier
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidDocumentMocks
import org.junit.Test

class CredentialsByDeepLinkVerifierTest {
    private lateinit var subject: CredentialsByDeepLinkVerifier

    private val deepLink = DeepLinkMocks.CredentialManifestDeepLinkDevNet

    @Test
    fun testVerifyCredentialsSuccess() {
        subject = CredentialsByDeepLinkVerifierImpl(
            ResolveDidDocumentRepositoryImpl(
                NetworkServiceSuccess(DidDocumentMocks.DidDocumentMockStr)
            )
        )

        subject.verifyCredentials(
            listOf(
                VCLJwt(CredentialMocks.JwtCredentialEmploymentPastFromRegularIssuer),
                VCLJwt(CredentialMocks.JwtCredentialEducationDegreeRegistrationFromRegularIssuer)
            ),
            deepLink
        ) {
            it.handleResult({ isVerified ->
                assert(isVerified)
            }, { error ->
                assert(false) { "${error.toJsonObject()}" }
            })
        }
    }

    @Test
    fun testVerifyCredentialsError() {
        subject = CredentialsByDeepLinkVerifierImpl(
            ResolveDidDocumentRepositoryImpl(
                NetworkServiceSuccess(DidDocumentMocks.DidDocumentWithWrongDidMockStr)
            )
        )

        subject.verifyCredentials(
            listOf(
                VCLJwt(CredentialMocks.JwtCredentialEmploymentPastFromRegularIssuer),
                VCLJwt(CredentialMocks.JwtCredentialEducationDegreeRegistrationFromRegularIssuer)
            ),
            deepLink
        ) {
            it.handleResult({
                assert(false) { "${VCLErrorCode.MismatchedCredentialIssuerDid.value} error code is expected" }
            }, { error ->
                assert(error.errorCode == VCLErrorCode.MismatchedCredentialIssuerDid.value)
            })
        }
    }
}