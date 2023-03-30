/**
 * Created by Michael Avoyan on 12/9/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.CountriesRepository
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

internal class CountriesRepositoryImpl(
    private val networkService: NetworkService,
    private val cacheService: CacheService
): CountriesRepository {

    override fun getCountries(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLCountries>) -> Unit
    ) {
        val endpoint = Urls.Countries
        if (cacheService.isResetCacheCountries(cacheSequence)) {
            fetchCountries(endpoint, cacheSequence, completionBlock)
        } else {
            cacheService.getCountries(endpoint)?.let { countries ->
                completionBlock(
                    VCLResult.Success(
                        jsonArrToCountries(JSONArray(countries))
                    )
                )
            } ?: run {
                fetchCountries(endpoint, cacheSequence, completionBlock)
            }
        }
    }

    private fun fetchCountries(
        endpoint: String,
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLCountries>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = endpoint,
            method = Request.HttpMethod.GET,
            headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)),
            useCaches = true,
            completionBlock = { result ->
                result.handleResult(
                    { countriesResponse ->
                        try {
                            cacheService.setCountries(endpoint, countriesResponse.payload, cacheSequence)
                            completionBlock(VCLResult.Success(
                                jsonArrToCountries(JSONArray(countriesResponse.payload))
                            ))
                        } catch (ex: Exception) {
                            completionBlock(VCLResult.Failure(VCLError(ex)))
                        }
                    },
                    { error ->
                        completionBlock(VCLResult.Failure(error))
                    }
                )
            }
        )
    }

    private fun jsonArrToCountries(countriesJsonArr: JSONArray): VCLCountries {
        val countries = mutableListOf<VCLCountry>()
        for (i in 0 until countriesJsonArr.length()) {
            countries.add(parseCountry(countriesJsonArr[i] as JSONObject))
        }
        return VCLCountries(countries)
    }

    private fun parseCountry(countryJsonObj: JSONObject): VCLCountry {
        val jsonArrRegions = countryJsonObj.optJSONArray(VCLCountry.KeyRegions)
        var regions: VCLRegions? = null

        jsonArrRegions?.let {
            val regionsList = mutableListOf<VCLRegion>()
            for (i in 0 until it.length()) {
                regionsList.add(VCLRegion(
                    payload = it.optJSONObject(i),
                    code = it.optJSONObject(i).optString(VCLRegion.KeyCode),
                    name = it.optJSONObject(i).optString(VCLRegion.KeyName),
                ))
            }
            regions = VCLRegions(regionsList)
        }

        return VCLCountry(
            payload = countryJsonObj,
            code = countryJsonObj.optString(VCLCountry.KeyCode),
            name = countryJsonObj.optString(VCLCountry.KeyName),
            regions = regions
        )
    }
}