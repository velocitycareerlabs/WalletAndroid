/**
 * Created by Michael Avoyan on 02/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.jwt

import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken

interface VCLJwtSignService {
    fun sign(
        jwtDescriptor: VCLJwtDescriptor,
        nonce: String? = null,
        didJwk: VCLDidJwk,
        remoteCryptoServicesToken: VCLToken? = null,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    )
}