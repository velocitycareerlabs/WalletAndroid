/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
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
        jwkPublic: VCLJwkPublic,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread {
            jwtServiceRepository.verifyJwt(jwt, jwkPublic) {
                executor.runOn(callingLooper) { completionBlock(it) }
            }
        }
    }

    override fun generateSignedJwt(
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread {
            jwtServiceRepository.generateSignedJwt(jwtDescriptor) {
                executor.runOn(callingLooper) { completionBlock(it) }
            }
        }
    }

    override fun generateDidJwk(
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    ) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread {
            jwtServiceRepository.generateDidJwk {
                executor.runOn(callingLooper) { completionBlock(it) }
            }
        }
    }
}