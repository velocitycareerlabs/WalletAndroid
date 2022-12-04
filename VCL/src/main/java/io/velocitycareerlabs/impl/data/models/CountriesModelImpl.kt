/**
 * Created by Michael Avoyan on 09/12/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.models

import io.velocitycareerlabs.api.entities.VCLCountries
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.data
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.domain.models.CountriesModel
import io.velocitycareerlabs.impl.domain.usecases.CountriesUseCase

internal class CountriesModelImpl(
    private val countriesUseCase: CountriesUseCase
): CountriesModel {
    override var data: VCLCountries? = null

    override fun initialize(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLCountries>) -> Unit
    ) {
        countriesUseCase.getCountries(cacheSequence) { result ->
            result.handleResult({ data = result.data }, { })
            completionBlock(result)
        }
    }
}