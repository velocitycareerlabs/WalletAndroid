/**
 * Created by Michael Avoyan on 04/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.verifiers

import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLResult

internal interface CredentialIssuerVerifier {
    fun verifyCredentials(
        jwtCredentials: List<VCLJwt>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<Boolean>) -> Unit
    )
}


