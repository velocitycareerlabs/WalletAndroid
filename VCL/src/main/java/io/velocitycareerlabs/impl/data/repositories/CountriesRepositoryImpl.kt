package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.CountriesRepository
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.*

/**
 * Created by Michael Avoyan on 12/9/21.
 */
internal class CountriesRepositoryImpl(
    private val networkService: NetworkService,
    private val cacheService: CacheService
): CountriesRepository {

    override fun getCountries(completionBlock: (VCLResult<VCLCountries>) -> Unit) {
        fetchCountries(completionBlock)
    }

    private fun fetchCountries(completionBlock: (VCLResult<VCLCountries>) -> Unit) {
        networkService.sendRequest(
            endpoint = Urls.Countries,
            method = Request.HttpMethod.GET,
            useCaches = true,
            completionBlock = { result ->
                result.handleResult(
                    { countriesResponse ->
                        try {
                            cacheService.countryCodes = countriesResponse.payload
                            completionBlock(VCLResult.Success(
                                jsonArrToCountries(JSONArray(countriesResponse.payload))
                            ))
                        } catch (ex: Exception) {
                            completionBlock(VCLResult.Failure(VCLError(ex.message)))
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