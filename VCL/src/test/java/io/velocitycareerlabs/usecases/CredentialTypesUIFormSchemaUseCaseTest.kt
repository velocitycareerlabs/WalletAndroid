/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.data.repositories.CredentialTypesUIFormSchemaRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.CredentialTypesUIFormSchemaUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.CredentialTypesUIFormSchemaUseCase
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialTypesUIFormSchemaMocks
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class CredentialTypesUIFormSchemaUseCaseTest {

    private lateinit var subject1: CredentialTypesUIFormSchemaUseCase
    private lateinit var subject2: CredentialTypesUIFormSchemaUseCase
    private lateinit var subject3: CredentialTypesUIFormSchemaUseCase
    private lateinit var subject4: CredentialTypesUIFormSchemaUseCase
    private lateinit var subject5: CredentialTypesUIFormSchemaUseCase

    lateinit var mockedCountries: VCLCountries

    @Before
    fun setUp() {
        mockedCountries = jsonArrToCountries(JSONArray(CredentialTypesUIFormSchemaMocks.CountriesJson))
    }

    @Test
    fun testCredentialTypesFormSchemaFull() {
        subject1 = CredentialTypesUIFormSchemaUseCaseImpl(
            CredentialTypesUIFormSchemaRepositoryImpl(
                NetworkServiceSuccess(CredentialTypesUIFormSchemaMocks.UISchemaFormJsonFull)
            ),
            EmptyExecutor()
        )

        subject1.getCredentialTypesUIFormSchema(
            VCLCredentialTypesUIFormSchemaDescriptor("some type", VCLCountries.CA),
            mockedCountries
        ) {
            it.handleResult(
                { credentialTypeUIFormSchema ->
                    val addressJsonObj = credentialTypeUIFormSchema.payload.getJSONObject("place")
                    val addressCountryJsonObj =
                        addressJsonObj.getJSONObject(VCLCredentialTypesUIFormSchema.KeyAddressCountry)
                    val addressRegionJsonObj =
                        addressJsonObj.getJSONObject(VCLCredentialTypesUIFormSchema.KeyAddressRegion)

                    val expectedAddressCountryCodes =
                        addressCountryJsonObj.getJSONArray(VCLCredentialTypesUIFormSchema.KeyUiEnum).toString()
                    val expectedAddressCountryNames =
                        addressCountryJsonObj.getJSONArray(VCLCredentialTypesUIFormSchema.KeyUiNames).toString()

                    val expectedAddressRegionCodes =
                        addressRegionJsonObj.getJSONArray(VCLCredentialTypesUIFormSchema.KeyUiEnum).toString()
                    val expectedAddressRegionNames =
                        addressRegionJsonObj.getJSONArray(VCLCredentialTypesUIFormSchema.KeyUiNames).toString()

                    assert(expectedAddressCountryCodes == CredentialTypesUIFormSchemaMocks.CountryCodes)
                    assert(expectedAddressCountryNames == CredentialTypesUIFormSchemaMocks.CountryNames)
                    assert(expectedAddressRegionCodes == CredentialTypesUIFormSchemaMocks.CanadaRegionCodes)
                    assert(expectedAddressRegionNames == CredentialTypesUIFormSchemaMocks.CanadaRegionNames)
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testCredentialTypesFormSchemaOnlyCountries() {
        subject2 = CredentialTypesUIFormSchemaUseCaseImpl(
            CredentialTypesUIFormSchemaRepositoryImpl(
                NetworkServiceSuccess(CredentialTypesUIFormSchemaMocks.UISchemaFormJsonOnlyCountries)
            ),
            EmptyExecutor()
        )

        subject2.getCredentialTypesUIFormSchema(
            VCLCredentialTypesUIFormSchemaDescriptor("some type", VCLCountries.CA),
            mockedCountries
        ) {
            it.handleResult(
                { credentialTypeUIFormSchema ->
                    val addressJsonObj = credentialTypeUIFormSchema.payload.getJSONObject("place")
                    val addressCountryJsonObj =
                        addressJsonObj.getJSONObject(VCLCredentialTypesUIFormSchema.KeyAddressCountry)
                    val addressRegionJsonObj =
                        addressJsonObj.getJSONObject(VCLCredentialTypesUIFormSchema.KeyAddressRegion)

                    val expectedAddressCountryCodes =
                        addressCountryJsonObj.getJSONArray(VCLCredentialTypesUIFormSchema.KeyUiEnum).toString()
                    val expectedAddressCountryNames =
                        addressCountryJsonObj.getJSONArray(VCLCredentialTypesUIFormSchema.KeyUiNames).toString()

                    val expectedAddressRegionCodes =
                        addressRegionJsonObj.optJSONArray(VCLCredentialTypesUIFormSchema.KeyUiEnum)
                    val expectedAddressRegionNames =
                        addressRegionJsonObj.optJSONArray(VCLCredentialTypesUIFormSchema.KeyUiNames)

                    assert(expectedAddressCountryCodes == CredentialTypesUIFormSchemaMocks.CountryCodes)
                    assert(expectedAddressCountryNames == CredentialTypesUIFormSchemaMocks.CountryNames)
                    assert(expectedAddressRegionCodes == null)
                    assert(expectedAddressRegionNames == null)
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testCredentialTypesFormSchemaOnlyRegions() {
        subject3 = CredentialTypesUIFormSchemaUseCaseImpl(
            CredentialTypesUIFormSchemaRepositoryImpl(
                NetworkServiceSuccess(CredentialTypesUIFormSchemaMocks.UISchemaFormJsonOnlyRegions)
            ),
            EmptyExecutor()
        )

        subject3.getCredentialTypesUIFormSchema(
            VCLCredentialTypesUIFormSchemaDescriptor("some type", VCLCountries.CA),
            mockedCountries
        ) {
            it.handleResult(
                { credentialTypeUIFormSchema ->
                    val addressJsonObj = credentialTypeUIFormSchema.payload.getJSONObject("place")
                    val addressCountryJsonObj =
                        addressJsonObj.getJSONObject(VCLCredentialTypesUIFormSchema.KeyAddressCountry)
                    val addressRegionJsonObj =
                        addressJsonObj.getJSONObject(VCLCredentialTypesUIFormSchema.KeyAddressRegion)

                    val expectedAddressCountryCodes =
                        addressCountryJsonObj.optJSONArray(VCLCredentialTypesUIFormSchema.KeyUiEnum)
                    val expectedAddressCountryNames =
                        addressCountryJsonObj.optJSONArray(VCLCredentialTypesUIFormSchema.KeyUiNames)

                    val expectedAddressRegionCodes =
                        addressRegionJsonObj.getJSONArray(VCLCredentialTypesUIFormSchema.KeyUiEnum).toString()
                    val expectedAddressRegionNames =
                        addressRegionJsonObj.getJSONArray(VCLCredentialTypesUIFormSchema.KeyUiNames).toString()

                    assert(expectedAddressCountryCodes == null)
                    assert(expectedAddressCountryNames == null)
                    assert(expectedAddressRegionCodes == CredentialTypesUIFormSchemaMocks.CanadaRegionCodes)
                    assert(expectedAddressRegionNames == CredentialTypesUIFormSchemaMocks.CanadaRegionNames)
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testCredentialTypesFormSchemaOnlyEnums() {
        subject4 = CredentialTypesUIFormSchemaUseCaseImpl(
            CredentialTypesUIFormSchemaRepositoryImpl(
                NetworkServiceSuccess(CredentialTypesUIFormSchemaMocks.UISchemaFormJsonOnlyEnums)
            ),
            EmptyExecutor()
        )

        subject4.getCredentialTypesUIFormSchema(
            VCLCredentialTypesUIFormSchemaDescriptor("some type", VCLCountries.CA),
            mockedCountries
        ) {
            it.handleResult(
                { credentialTypeUIFormSchema ->
                    val addressJsonObj = credentialTypeUIFormSchema.payload.getJSONObject("place")
                    val addressCountryJsonObj =
                        addressJsonObj.getJSONObject(VCLCredentialTypesUIFormSchema.KeyAddressCountry)
                    val addressRegionJsonObj =
                        addressJsonObj.getJSONObject(VCLCredentialTypesUIFormSchema.KeyAddressRegion)

                    val expectedAddressCountryCodes =
                        addressCountryJsonObj.getJSONArray(VCLCredentialTypesUIFormSchema.KeyUiEnum).toString()
                    val expectedAddressCountryNames =
                        addressCountryJsonObj.optJSONArray(VCLCredentialTypesUIFormSchema.KeyUiNames)

                    val expectedAddressRegionCodes =
                        addressRegionJsonObj.getJSONArray(VCLCredentialTypesUIFormSchema.KeyUiEnum).toString()
                    val expectedAddressRegionNames =
                        addressRegionJsonObj.optJSONArray(VCLCredentialTypesUIFormSchema.KeyUiNames)

//        Assert
                    assert(expectedAddressCountryCodes == CredentialTypesUIFormSchemaMocks.CountryCodes)
                    assert(expectedAddressCountryNames == null)
                    assert(expectedAddressRegionCodes == CredentialTypesUIFormSchemaMocks.CanadaRegionCodes)
                    assert(expectedAddressRegionNames == null)
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testCredentialTypesFormSchemaFailure() {
        subject5 = CredentialTypesUIFormSchemaUseCaseImpl(
            CredentialTypesUIFormSchemaRepositoryImpl(
                NetworkServiceSuccess("wrong payload")
            ),
            EmptyExecutor()
        )

        subject5.getCredentialTypesUIFormSchema(
            VCLCredentialTypesUIFormSchemaDescriptor("some type", VCLCountries.CA),
            mockedCountries
        ) {
            it.handleResult(
                successHandler = {
                    assert(false) { "${VCLErrorCode.SdkError.value} error code is expected" }
                },
                errorHandler = { error ->
                    assert(error.errorCode == VCLErrorCode.SdkError.value)
                }
            )
        }
    }

    private fun jsonArrToCountries(countriesJsonArr: JSONArray): VCLCountries {
        val countries = mutableListOf<VCLCountry>()
        for (i in 0 until countriesJsonArr.length()) {
            countries.add(parseCountry(countriesJsonArr[i] as JSONObject))
        }
        return VCLCountries(countries)
    }

    private fun parseCountry(countryJsonObj: JSONObject): VCLCountry {
        var regions: VCLRegions? = null

        countryJsonObj.optJSONArray(VCLCountry.KeyRegions)?.let { jsonArrRegions ->
            val regionsList = mutableListOf<VCLRegion>()
            for (i in 0 until jsonArrRegions.length()) {
                regionsList.add(VCLRegion(
                    payload = jsonArrRegions.optJSONObject(i),
                    code = jsonArrRegions.optJSONObject(i).optString(VCLRegion.KeyCode),
                    name = jsonArrRegions.optJSONObject(i).optString(VCLRegion.KeyName),
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