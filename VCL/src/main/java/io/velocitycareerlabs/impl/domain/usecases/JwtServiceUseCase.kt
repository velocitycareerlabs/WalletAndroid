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
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<Boolean>) -> Unit
    )
    fun generateSignedJwt(
        jwtDescriptor: VCLJwtDescriptor,
        nonce: String? = null,
        didJwk: VCLDidJwk,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    )
}