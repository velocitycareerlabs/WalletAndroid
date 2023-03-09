/**
 * Created by Michael Avoyan on 3/31/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.VCLCredentialTypeSchema
import io.velocitycareerlabs.api.entities.VCLError
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.repositories.CredentialTypeSchemaRepository
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import org.json.JSONObject
import java.lang.Exception

internal class CredentialTypeSchemaRepositoryImpl(
        private val networkService: NetworkService,
        private val cacheService: CacheService
): CredentialTypeSchemaRepository {

    override fun getCredentialTypeSchema(
        schemaName: String,
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLCredentialTypeSchema>) -> Unit
    ) {
        val endpoint = Urls.CredentialTypeSchemas + schemaName
        if(cacheService.isResetCacheCredentialTypeSchema(cacheSequence)) {
            fetchCredentialTypeSchema(endpoint, cacheSequence, completionBlock)
        } else {
            cacheService.getCredentialTypeSchema(endpoint)?.let { credentialTypeSchema ->
                completionBlock(
                    VCLResult.Success(
                        VCLCredentialTypeSchema(
                            JSONObject(credentialTypeSchema)
                        )
                    )
                )
            } ?: run {
                fetchCredentialTypeSchema(endpoint, cacheSequence, completionBlock)
            }
        }
    }

    private fun fetchCredentialTypeSchema(
        endpoint: String,
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLCredentialTypeSchema>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = endpoint,
            method = Request.HttpMethod.GET,
            headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)),
            useCaches = true,
            completionBlock = { result ->
                result.handleResult(
                    { credentialTypeSchemaResponse ->
                        try {
                            cacheService.setCredentialTypeSchema(
                                endpoint,
                                credentialTypeSchemaResponse.payload,
                                cacheSequence
                            )
                            completionBlock(VCLResult.Success(
                                VCLCredentialTypeSchema(
                                    JSONObject(credentialTypeSchemaResponse.payload)
                                )
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
}