/**
 * Created by Michael Avoyan on 17/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.utils

import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.utils.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.utils.VCLLog

internal class CredentialIssuerVerifierEmptyImpl: CredentialIssuerVerifier {
    val TAG = CredentialIssuerVerifierEmptyImpl::class.simpleName
    override fun verifyCredentials(
        jwtEncodedCredentials: List<String>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        VCLLog.d(TAG, "Empty implementation - credential issuer is always approved...")
        completionBlock(VCLResult.Success(true))
    }
}