/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 *  Copyright 2022 Velocity Career Labs inc.
 *  SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.jwt

import io.velocitycareerlabs.api.entities.VCLJwkPublic
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.initialization.VCLJwtServiceUrls
import io.velocitycareerlabs.api.jwt.VCLJwtService
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService

internal class VCLJwtServiceRemoteImpl(
    private val networkService: NetworkService,
    private val jwtServiceUrls: VCLJwtServiceUrls
) : VCLJwtService {
    override fun verify(
        jwt: VCLJwt,
        jwkPublic: VCLJwkPublic,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun sign(
        kid: String?,
        nonce: String?,
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}