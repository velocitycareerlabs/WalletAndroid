/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.*

internal interface JwtServiceUseCase {
    fun verifyJwt(
        jwt: VCLJwt,
        publicJwk: VCLPublicJwk,
        completionBlock: (VCLResult<Boolean>) -> Unit
    )
    fun generateSignedJwt(
        kid: String? = null,
        nonce: String? = null,
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    )
}