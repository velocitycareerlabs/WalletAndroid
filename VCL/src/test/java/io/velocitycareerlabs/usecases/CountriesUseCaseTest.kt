/**
 * Created by Michael Avoyan on 12/9/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.VCLCountries
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.data.repositories.CountriesRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.CountriesUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.CountriesUseCase
import io.velocitycareerlabs.infrastructure.resources.EmptyCacheService
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.CountriesMocks
import org.junit.After
import org.junit.Before
import org.junit.Test

//@RunWith(RobolectricTestRunner::class)
//@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CountriesUseCaseTest {

    internal lateinit var subject: CountriesUseCase

    @Before
    fun setUp() {
    }

    @Test
    fun testGetCountriesSuccess() {
        subject = CountriesUseCaseImpl(
            CountriesRepositoryImpl(
                NetworkServiceSuccess(
                    CountriesMocks.CountriesJson
                ),
                EmptyCacheService()
            ),
            ExecutorImpl()
        )

        subject.getCountries(0) {
            it.handleResult(
                successHandler = { countries ->
                    val afghanistanCountry = countries.countryByCode(VCLCountries.AF)!!
                    val afghanistanRegions = afghanistanCountry.regions!!

                    assert(afghanistanCountry.code == CountriesMocks.AfghanistanCode)
                    assert(afghanistanCountry.name == CountriesMocks.AfghanistanName)

                    assert(afghanistanRegions.all[0].name == CountriesMocks.AfghanistanRegion1Name)
                    assert(afghanistanRegions.all[0].code == CountriesMocks.AfghanistanRegion1Code)
                    assert(afghanistanRegions.all[1].name == CountriesMocks.AfghanistanRegion2Name)
                    assert(afghanistanRegions.all[1].code == CountriesMocks.AfghanistanRegion2Code)
                    assert(afghanistanRegions.all[2].name == CountriesMocks.AfghanistanRegion3Name)
                    assert(afghanistanRegions.all[2].code == CountriesMocks.AfghanistanRegion3Code)
                },
                errorHandler = {
                    assert(false) { "$it" }
                }
            )
        }
    }

    @After
    fun tearDown() {
    }
}