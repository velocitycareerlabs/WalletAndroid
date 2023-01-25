/**
 * Created by Michael Avoyan on 13/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.CredentialTypesUIFormSchemaRepository
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

internal class CredentialTypesUIFormSchemaRepositoryImpl(
    private val networkService: NetworkService
): CredentialTypesUIFormSchemaRepository {
    override fun getCredentialTypesUIFormSchema(
        credentialTypesUIFormSchemaDescriptor: VCLCredentialTypesUIFormSchemaDescriptor,
        countries: VCLCountries,
        completionBlock: (VCLResult<VCLCredentialTypesUIFormSchema>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = Urls.CredentialTypesFormSchema.replace(
                Params.CredentialType,
                credentialTypesUIFormSchemaDescriptor.credentialType
            ),
            method = Request.HttpMethod.GET,
            headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderKValues.XVnfProtocolVersion)),
            completionBlock = { result ->
                result.handleResult(
                    { credentialTypesFormSchemaResponse ->
                        try {
                            val country =
                                countries.countryByCode(credentialTypesUIFormSchemaDescriptor.countryCode)
                            completionBlock(
                                VCLResult.Success(
                                    VCLCredentialTypesUIFormSchema(
                                        payload =
                                        parseCredentialTypesUIFormSchema(
                                            countries,
                                            country?.regions,
                                            JSONObject(credentialTypesFormSchemaResponse.payload)
                                        )
                                    )
                                )
                            )
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

    private fun parseCredentialTypesUIFormSchema(
        countries: VCLCountries,
        regions: VCLRegions?,
        formSchemaDict: JSONObject
    ): JSONObject {
        var formSchemaDictCP = formSchemaDict
        val keysItr: Iterator<String> = formSchemaDictCP.keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            val valueDict: Any = formSchemaDictCP.get(key)
            if (valueDict is JSONObject) {
                if (key == VCLCredentialTypesUIFormSchema.CodingKeys.KeyAddressCountry) {
                    countries.all?.let { allCountries ->
                        formSchemaDictCP = updateAddressEnums(
                            allCountries,
                            key,
                            valueDict,
                            formSchemaDictCP
                        )
                    }
                } else if (key == VCLCredentialTypesUIFormSchema.CodingKeys.KeyAddressRegion) {
                    regions?.all?.let { allRegions ->
                        formSchemaDictCP = updateAddressEnums(
                            allRegions,
                            key,
                            valueDict,
                            formSchemaDictCP
                        )
                    }
                } else {
                    formSchemaDictCP.put(
                        key, parseCredentialTypesUIFormSchema(
                            countries,
                            regions,
                            valueDict
                        )
                    )
                }
            }
        }
        return formSchemaDictCP
    }

    private fun updateAddressEnums(
        places: List<VCLPlace>,
        key: String,
        valueDict: JSONObject,
        formSchemaDict: JSONObject
    ): JSONObject {
        var formSchemaDictCP = formSchemaDict
        val valueDictHasKeyUiEnum = valueDict.has(VCLCredentialTypesUIFormSchema.CodingKeys.KeyUiEnum)
        val valueDictHasKeyUiNames = valueDict.has(VCLCredentialTypesUIFormSchema.CodingKeys.KeyUiNames)
        if (valueDictHasKeyUiEnum || valueDictHasKeyUiNames) {
            val uiEnumArr = JSONArray()
            val uiNamesArr = JSONArray()
            places.forEach { place ->
                if(valueDictHasKeyUiEnum) {
                    uiEnumArr.put(place.code)
                }
                if(valueDictHasKeyUiNames) {
                    uiNamesArr.put(place.name)
                }
            }
            if(valueDictHasKeyUiEnum) {
                valueDict.put(
                    VCLCredentialTypesUIFormSchema.CodingKeys.KeyUiEnum,
                    uiEnumArr
                )
            }
            if(valueDictHasKeyUiNames) {
                valueDict.put(
                    VCLCredentialTypesUIFormSchema.CodingKeys.KeyUiNames,
                    uiNamesArr
                )
            }
            formSchemaDictCP.put(key, valueDict)
        }
        return formSchemaDictCP
    }
}