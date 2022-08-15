package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLCountries
import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 09/12/2021.
 */
internal interface CountriesUseCase {
    fun getCountries(completionBlock: (VCLResult<VCLCountries>) -> Unit)
}