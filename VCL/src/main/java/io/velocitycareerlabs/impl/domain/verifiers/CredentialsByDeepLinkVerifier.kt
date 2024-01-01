/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.domain.verifiers

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLResult

interface CredentialsByDeepLinkVerifier {
    fun verifyCredentials(
        jwtCredentials: List<VCLJwt>,
        deepLink: VCLDeepLink,
        completionBlock: (VCLResult<Boolean>) -> Unit
    )
}