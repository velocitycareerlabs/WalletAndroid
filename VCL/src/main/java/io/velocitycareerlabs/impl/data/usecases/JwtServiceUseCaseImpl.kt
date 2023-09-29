/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.usecases.JwtServiceUseCase

internal class JwtServiceUseCaseImpl(
    private val jwtServiceRepository: JwtServiceRepository,
    private val executor: Executor
): JwtServiceUseCase {
    override fun verifyJwt(
        jwt: VCLJwt,
        publicJwk: VCLPublicJwk,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        executor.runOnBackground {
            jwtServiceRepository.verifyJwt(jwt, publicJwk) {
                executor.runOnMain {
                    completionBlock(it)
                }
            }
        }
    }

    override fun generateSignedJwt(
        kid: String?,
        nonce: String?,
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        executor.runOnBackground {
            jwtServiceRepository.generateSignedJwt(
                kid = kid,
                nonce = nonce,
                jwtDescriptor = jwtDescriptor
            ) { jwtResult ->
                executor.runOnMain {
                    completionBlock(jwtResult)
                }
            }
        }
    }
}