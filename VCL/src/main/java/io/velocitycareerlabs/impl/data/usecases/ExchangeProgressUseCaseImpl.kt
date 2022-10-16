/**
 * Created by Michael Avoyan on 30/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
import io.velocitycareerlabs.api.entities.VCLExchange
import io.velocitycareerlabs.api.entities.VCLExchangeDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.ExchangeProgressRepository
import io.velocitycareerlabs.impl.domain.usecases.ExchangeProgressUseCase

internal class ExchangeProgressUseCaseImpl(
    private val exchangeProgressRepository: ExchangeProgressRepository,
    private val executor: Executor
): ExchangeProgressUseCase {

    override fun getExchangeProgress(exchangeDescriptor: VCLExchangeDescriptor,
                                     completionBlock: (VCLResult<VCLExchange>) -> Unit) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread() {
            exchangeProgressRepository.getExchangeProgress(exchangeDescriptor) {
                executor.runOn(callingLooper) { completionBlock(it) }
            }
        }
    }
}