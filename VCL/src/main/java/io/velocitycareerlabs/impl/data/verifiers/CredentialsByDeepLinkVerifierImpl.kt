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
import java.util.concurrent.CompletableFuture

class CredentialsByDeepLinkVerifierImpl: CredentialsByDeepLinkVerifier {
    override fun verifyCredentials(
        jwtCredentials: List<VCLJwt>,
        deepLink: VCLDeepLink,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        val errorCredential = jwtCredentials.find { it.iss != deepLink.did }
        errorCredential?.let {
            completionBlock(VCLResult.Failure(VCLError(errorCode = VCLErrorCode.MismatchedCredentialIssuerDid.value)))
        } ?: run {
            completionBlock(VCLResult.Success(true))
        }
    }
}