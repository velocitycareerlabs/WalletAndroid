/**
 * Created by Michael Avoyan on 4/6/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.*

internal interface JwtServiceRepository {
    fun decode(
        encodedJwt: String,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    )
    fun verifyJwt(
        jwt: VCLJwt,
        publicJwk: VCLPublicJwk,
        remoteCryptoServicesToken: VCLToken? = null,
        completionBlock: (VCLResult<Boolean>) -> Unit
    )
    fun generateSignedJwt(
        kid: String? = null,
        nonce: String? = null,
        jwtDescriptor: VCLJwtDescriptor,
        remoteCryptoServicesToken: VCLToken? = null,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    )
}