/**
 * Created by Michael Avoyan on 10/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.GenerateOffersRepository
import io.velocitycareerlabs.impl.domain.usecases.GenerateOffersUseCase

internal class GenerateOffersUseCaseImpl(
    private val generateOffersRepository: GenerateOffersRepository,
    private val executor: Executor
): GenerateOffersUseCase {
    override fun generateOffers(
        token: VCLToken,
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        completionBlock: (VCLResult<VCLOffers>) -> Unit
    ) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread {
            generateOffersRepository.generateOffers(token, generateOffersDescriptor) {
                executor.runOn(callingLooper) { completionBlock(it) }
            }
        }
    }
}