/**
 * Created by Michael Avoyan on 04/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.utils

import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwtVerifiableCredentials
import io.velocitycareerlabs.api.entities.VCLResult

internal interface CredentialDidVerifier {
    fun verifyCredentials(
        jwtEncodedCredentials: List<String>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    )
}