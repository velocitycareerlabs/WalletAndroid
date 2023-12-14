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
import io.velocitycareerlabs.impl.data.verifiers.CredentialsByDeepLinkVerifierImpl
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import org.junit.Test

class CredentialsByDeepLinkVerifierTest {
    private val subject = CredentialsByDeepLinkVerifierImpl()

    private val correctDeepLink =
        VCLDeepLink("velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiBMsw27IKRYIdwUOfDeBd0LnWVeG2fPxxJi9L1fvjM20g%2Fissue%2Fget-credential-manifest%3Fid%3D611b5836e93d08000af6f1bc%26credential_types%3DPastEmploymentPosition%26issuerDid%3Ddid%3Aion%3AEiBMsw27IKRYIdwUOfDeBd0LnWVeG2fPxxJi9L1fvjM20g")
    private val wrongDeepLink = DeepLinkMocks.CredentialManifestDeepLinkDevNet

    @Test
    fun testVerifyCredentialsSuccess() {
        subject.verifyCredentials(
            listOf(
                VCLJwt(CredentialMocks.JwtCredentialEmploymentPastFromRegularIssuer),
                VCLJwt(CredentialMocks.JwtCredentialEducationDegreeRegistrationFromRegularIssuer)
            ),
            correctDeepLink
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
        subject.verifyCredentials(
            listOf(
                VCLJwt(CredentialMocks.JwtCredentialEmploymentPastFromRegularIssuer),
                VCLJwt(CredentialMocks.JwtCredentialEducationDegreeRegistrationFromRegularIssuer)
            ),
            wrongDeepLink
        ) {
            it.handleResult({
                assert(false) { "${VCLErrorCode.MismatchedCredentialIssuerDid.value} error code is expected" }
            }, { error ->
                assert(error.errorCode == VCLErrorCode.MismatchedCredentialIssuerDid.value)
            })
        }
    }
}