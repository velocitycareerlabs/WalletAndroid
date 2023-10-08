/**
 * Created by Michael Avoyan on 4/6/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.jwt.VCLJwtSignService
import io.velocitycareerlabs.api.jwt.VCLJwtVerifyService
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import java.lang.Exception

internal class JwtServiceRepositoryImpl(
        private val jwtSignService: VCLJwtSignService,
        private val jwtVerifyService: VCLJwtVerifyService
): JwtServiceRepository {

    override fun decode(
        encodedJwt: String,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        try {
            completionBlock(
                VCLResult.Success(VCLJwt(encodedJwt))
            )
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }

    override fun verifyJwt(
        jwt: VCLJwt,
        publicJwk: VCLPublicJwk,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        jwtVerifyService.verify(jwt, publicJwk) {
            completionBlock(it)
        }
    }

    override fun generateSignedJwt(
        kid: String?, // did:jwk in case of person binding
        nonce: String?, // nonce == challenge
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        jwtSignService.sign(
            kid = kid,
            nonce = nonce,
            jwtDescriptor = jwtDescriptor
        ) {
            completionBlock(it)
        }
    }
}