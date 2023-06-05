/**
 * Created by Michael Avoyan on 4/6/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.jwt.JwtService
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import java.lang.Exception

internal class JwtServiceRepositoryImpl(
        private val jwtService: JwtService
): JwtServiceRepository {

    override fun decode(
        encodedJwt: String,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        try {
            completionBlock(
                VCLResult.Success(jwtService.decode(encodedJwt))
            )
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }

    override fun verifyJwt(
        jwt: VCLJwt,
        jwkPublic: VCLJwkPublic,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        try {
            completionBlock(VCLResult.Success(jwtService.verify(jwt, jwkPublic)))
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }

    override fun generateSignedJwt(
        kid: String?, // did:jwk in case of person binding
        nonce: String?, // nonce == challenge
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        try {
            completionBlock(
                VCLResult.Success(
                    jwtService.sign(
                        kid = kid,
                        nonce = nonce,
                        jwtDescriptor = jwtDescriptor
                    )
                )
            )
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }
}