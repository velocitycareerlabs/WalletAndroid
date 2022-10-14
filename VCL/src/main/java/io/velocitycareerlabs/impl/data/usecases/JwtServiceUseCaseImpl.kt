/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
import io.velocitycareerlabs.api.entities.VCLJWT
import io.velocitycareerlabs.api.entities.VCLPublicKey
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.usecases.JwtServiceUseCase
import org.json.JSONObject

internal class JwtServiceUseCaseImpl(
    private val jwtServiceRepository: JwtServiceRepository,
    private val executor: Executor
): JwtServiceUseCase {
    override fun verifyJwt(jwt: VCLJWT, publicKey: VCLPublicKey, completionBlock: (VCLResult<Boolean>) -> Unit) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread() {
            jwtServiceRepository.verifyJwt(jwt, publicKey) {
                executor.runOn(callingLooper) { completionBlock(it) }
            }
        }
    }

    override fun generateSignedJwt(payload: JSONObject, iss: String, completionBlock: (VCLResult<VCLJWT>) -> Unit) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread() {
            jwtServiceRepository.generateSignedJwt(payload, iss) {
                executor.runOn(callingLooper) { completionBlock(it) }
            }
        }
    }
}