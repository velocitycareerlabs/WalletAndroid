/**
 * Created by Michael Avoyan on 3/13/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.CredentialTypesRepository
import org.json.JSONArray
import java.lang.Exception
import java.util.*

internal class CredentialTypesRepositoryImpl(
        private val networkService: NetworkService,
        private val cacheService: CacheService
): CredentialTypesRepository {

    override fun getCredentialTypes(
        resetCache: Boolean,
        completionBlock: (VCLResult<VCLCredentialTypes>) -> Unit
    ) {
        val endpoint = Urls.CredentialTypes
        if (resetCache) {
            fetchCredentialTypes(endpoint, completionBlock)
        } else {
            cacheService.getCredentialTypes(endpoint)?.let { credentialTypes ->
                completionBlock(
                    VCLResult.Success(
                        parse(JSONArray(credentialTypes))
                    )
                )
            } ?: run {
                fetchCredentialTypes(endpoint, completionBlock)
            }
        }
    }

    private fun fetchCredentialTypes(endpoint: String, completionBlock: (VCLResult<VCLCredentialTypes>) -> Unit) {
        networkService.sendRequest(
            endpoint = endpoint,
            contentType = Request.ContentTypeApplicationJson,
            method = Request.HttpMethod.GET,
            useCaches = true,
            completionBlock = { result ->
                result.handleResult(
                    { credentialTypesResponse->
                        try {
                            cacheService.setCredentialTypes(endpoint, credentialTypesResponse.payload)
                            completionBlock(VCLResult.Success(
                                parse(JSONArray(credentialTypesResponse.payload))
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

    private fun parse(jsonArray: JSONArray): VCLCredentialTypes {
        val credentialTypesArr = mutableListOf<VCLCredentialType>()
        for (i in 0 until jsonArray.length()) {
            jsonArray.optJSONObject(i)?.let { payload ->
                val id = payload.optString(VCLCredentialType.KeyId)
                val schema = payload.optString(VCLCredentialType.KeySchema)
                val createdAt = payload.optString(VCLCredentialType.KeyCreatedAt)
                val schemaName = payload.optString(VCLCredentialType.KeySchemaName)
                val credentialType = payload.optString(VCLCredentialType.KeyCredentialType)
                val recommended = payload.optBoolean(VCLCredentialType.KeyRecommended)

                credentialTypesArr.add(
                    VCLCredentialType(
                        payload = payload,
                        id = id,
                        schema = schema,
                        createdAt = createdAt,
                        schemaName = schemaName,
                        credentialType = credentialType,
                        recommended = recommended
                    )
                )
            }
        }
        return VCLCredentialTypes(credentialTypesArr)
    }
}