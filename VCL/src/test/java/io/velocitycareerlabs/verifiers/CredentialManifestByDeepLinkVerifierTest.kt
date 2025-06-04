/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.verifiers

import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.repositories.ResolveDidDocumentRepositoryImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialManifestByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.domain.verifiers.CredentialManifestByDeepLinkVerifier
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidDocumentMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.junit.Test

class CredentialManifestByDeepLinkVerifierTest {
    private lateinit var subject: CredentialManifestByDeepLinkVerifier

    private val credentialManifest = VCLCredentialManifest(
        jwt = VCLJwt(CredentialManifestMocks.JwtCredentialManifest1),
        verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileOfRegularIssuer.toJsonObject()!!),
        didJwk = DidJwkMocks.DidJwk
    )
    private val deepLink = DeepLinkMocks.CredentialManifestDeepLinkDevNet

    @Test
    fun testVerifyCredentialManifestSuccess() {
        subject = CredentialManifestByDeepLinkVerifierImpl(
            ResolveDidDocumentRepositoryImpl(
                NetworkServiceSuccess(DidDocumentMocks.DidDocumentMockStr)
            )
        )

        subject.verifyCredentialManifest(
            credentialManifest,
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
    fun testVerifyCredentialManifestError() {
        subject = CredentialManifestByDeepLinkVerifierImpl(
            ResolveDidDocumentRepositoryImpl(
                NetworkServiceSuccess(DidDocumentMocks.DidDocumentWithWrongDidMockStr)
            )
        )

        subject.verifyCredentialManifest(
            credentialManifest,
            deepLink
        ) {
            it.handleResult({
                assert(false) { "${VCLErrorCode.MismatchedRequestIssuerDid.value} error code is expected" }
            }, { error ->
                assert(error.errorCode == VCLErrorCode.MismatchedRequestIssuerDid.value)
            })
        }
    }
}