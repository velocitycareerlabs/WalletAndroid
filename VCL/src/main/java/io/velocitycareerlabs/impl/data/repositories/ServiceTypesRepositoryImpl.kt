/**
 * Created by Michael Avoyan on 25/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLServiceType
import io.velocitycareerlabs.api.entities.VCLServiceTypeDynamic
import io.velocitycareerlabs.api.entities.VCLServiceTypesDynamic
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.ServiceTypesRepository
import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

internal class ServiceTypesRepositoryImpl(
    private val networkService: NetworkService,
    private val cacheService: CacheService
): ServiceTypesRepository {
    override fun getServiceTypes(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLServiceTypesDynamic>) -> Unit
    ) {
        val endpoint = Urls.ServiceTypes
        if (cacheService.isResetCacheServiceTypes(cacheSequence)) {
            fetchServiceTypes(endpoint, cacheSequence, completionBlock)
        } else {
            cacheService.getServiceTypes(endpoint)?.let { serviceTypes ->
                parse(serviceTypes.toJsonObject())?.let {
                    completionBlock(VCLResult.Success(it))
                } ?: run {
                    completionBlock(VCLResult.Failure(VCLError("Failed to parse $serviceTypes")))
                }
            } ?: run {
                fetchServiceTypes(endpoint, cacheSequence, completionBlock)
            }
        }
    }

    private fun fetchServiceTypes(
        endpoint: String,
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLServiceTypesDynamic>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = endpoint,
            contentType = Request.ContentTypeApplicationJson,
            method = Request.HttpMethod.GET,
            headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)),
            useCaches = true,
            completionBlock = { result ->
                result.handleResult(
                    { serviceTypesResponse->
                        try {
                            cacheService.setServiceTypes(
                                endpoint,
                                serviceTypesResponse.payload,
                                cacheSequence
                            )
                            parse(serviceTypesResponse.payload.toJsonObject())?.let {
                                completionBlock(VCLResult.Success(it))
                            } ?: run {
                                completionBlock(VCLResult.Failure(VCLError("Failed to parse ${serviceTypesResponse.payload}")))
                            }
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

    private fun parse(serviceTypesJsonObj: JSONObject?): VCLServiceTypesDynamic? {
        serviceTypesJsonObj?.optJSONArray(VCLServiceTypesDynamic.KeyServiceTypes)?.let {
            serviceTypesJsonArr ->
            val serviceTypesArr = mutableListOf<VCLServiceTypeDynamic>()
            for (i in 0 until (serviceTypesJsonArr.length())) {
                serviceTypesJsonArr.optJSONObject(i)?.let { payload ->
                    serviceTypesArr.add(
                        VCLServiceTypeDynamic(payload)
                    )
                }
            }
            return VCLServiceTypesDynamic(serviceTypesArr)
        } ?: return null
    }

    companion object {

    }
}