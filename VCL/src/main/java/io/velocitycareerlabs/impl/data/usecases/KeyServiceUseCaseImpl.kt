/**
 * Created by Michael Avoyan on 25/05/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.KeyServiceRepository
import io.velocitycareerlabs.impl.domain.usecases.KeyServiceUseCase

internal class KeyServiceUseCaseImpl(
    private val keyServiceRepository: KeyServiceRepository,
    private val executor: Executor
): KeyServiceUseCase {

    override fun generateDidJwk(
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    ) {
        executor.runOnBackground {
            keyServiceRepository.generateDidJwk(
                remoteCryptoServicesToken,
            ) {
                executor.runOnMain {
                    completionBlock(it)
                }
            }
        }
    }
}