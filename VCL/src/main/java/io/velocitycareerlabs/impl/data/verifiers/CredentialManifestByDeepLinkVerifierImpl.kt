/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.verifiers

import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.domain.verifiers.CredentialManifestByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.VCLLog

class CredentialManifestByDeepLinkVerifierImpl: CredentialManifestByDeepLinkVerifier {
    private val TAG = CredentialManifestByDeepLinkVerifierImpl::class.simpleName

    override fun verifyCredentialManifest(
        credentialManifest: VCLCredentialManifest,
        deepLink: VCLDeepLink,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        if (credentialManifest.issuerId == deepLink.did) {
            completionBlock(VCLResult.Success(true))
        } else {
            VCLLog.e(TAG, "credential manifest: ${credentialManifest.jwt.encodedJwt} \ndeepLink: ${deepLink.value}")
            completionBlock((VCLResult.Failure(VCLError(errorCode = VCLErrorCode.MismatchedRequestIssuerDid.value))))
        }
    }
}