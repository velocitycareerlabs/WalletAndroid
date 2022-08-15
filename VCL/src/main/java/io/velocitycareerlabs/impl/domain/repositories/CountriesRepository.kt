package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLCountries
import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 12/9/21.
 */
internal interface CountriesRepository {
    fun getCountries(completionBlock: (VCLResult<VCLCountries>) -> Unit)
}