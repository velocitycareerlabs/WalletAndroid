/**
 * Created by Michael Avoyan on 05/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLJwkPublic
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.jwt.VCLJwtService

class VCLJwtServiceMock: VCLJwtService {
    override fun sign(
        kid: String?,
        nonce: String?,
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
    }

    override fun verify(
        jwt: VCLJwt,
        jwkPublic: VCLJwkPublic,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
    }
}