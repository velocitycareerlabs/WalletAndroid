/**
 * Created by Michael Avoyan on 09/12/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.VCLCountries
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.CountriesRepository
import io.velocitycareerlabs.impl.domain.usecases.CountriesUseCase

internal class CountriesUseCaseImpl(
    private val countriesRepository: CountriesRepository,
    private val executor: Executor
): CountriesUseCase {
    override fun getCountries(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLCountries>) -> Unit
    ) {
        executor.runOnBackground {
            countriesRepository.getCountries(cacheSequence) {
                executor.runOnMain {
                    completionBlock(it)
                }
            }
        }
    }
}