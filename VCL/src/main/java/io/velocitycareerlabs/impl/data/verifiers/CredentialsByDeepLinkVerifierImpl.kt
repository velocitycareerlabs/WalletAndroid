/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.verifiers

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.domain.verifiers.CredentialsByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.VCLLog

class CredentialsByDeepLinkVerifierImpl: CredentialsByDeepLinkVerifier {
    private val TAG = CredentialsByDeepLinkVerifierImpl::class.simpleName

    override fun verifyCredentials(
        jwtCredentials: List<VCLJwt>,
        deepLink: VCLDeepLink,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        jwtCredentials.find { it.iss != deepLink.did }?.let { mismatchedCredential ->
            VCLLog.e(TAG, "mismatched credential: ${mismatchedCredential.encodedJwt} \ndeepLink: ${deepLink.value}")
            completionBlock(VCLResult.Failure(VCLError(errorCode = VCLErrorCode.MismatchedCredentialIssuerDid)))
        } ?: run {
            completionBlock(VCLResult.Success(true))
        }
    }
}