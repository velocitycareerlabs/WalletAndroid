/**
 * Created by Michael Avoyan on 16/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.verifiers.directissuerverification

import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.verifiers.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.utils.VCLLog

internal class CredentialIssuerVerifierEmptyImpl: CredentialIssuerVerifier {
    val TAG = CredentialIssuerVerifierEmptyImpl::class.java.simpleName

    override fun verifyCredentials(
        jwtCredentials: List<VCLJwt>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        VCLLog.d(TAG, "Empty implementation - credential issuer is always approved...")
        completionBlock(VCLResult.Success(true))
    }
}