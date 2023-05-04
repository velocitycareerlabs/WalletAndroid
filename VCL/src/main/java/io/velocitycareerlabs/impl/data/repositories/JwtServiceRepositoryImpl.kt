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
            jwtService.parse(encodedJwt)?.let {
                completionBlock(VCLResult.Success(VCLJwt(it)))
            } ?: throw Exception("Failed to parse $encodedJwt")
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
            completionBlock(VCLResult.Success(jwtService.verify(jwt, jwkPublic.valueStr)))
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }

    override fun generateSignedJwt(
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        try {
            jwtService.sign(jwtDescriptor)?.let { completionBlock(VCLResult.Success(VCLJwt(it))) }
                    ?: throw Exception("Failed to sign ${jwtDescriptor.payload}")
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }

    override fun generateDidJwk(
        didJwkDescriptor: VCLDidJwkDescriptor?,
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    ) {
        try {
            completionBlock(VCLResult.Success(jwtService.generateDidJwk(didJwkDescriptor)))
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }
}