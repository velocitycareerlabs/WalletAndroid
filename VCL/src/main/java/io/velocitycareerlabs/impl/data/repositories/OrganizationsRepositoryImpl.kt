/**
 * Created by Michael Avoyan on 4/11/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.repositories.OrganizationsRepository
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.extensions.toJsonObject
import java.lang.Exception

internal class OrganizationsRepositoryImpl(
        private val networkService: NetworkService
): OrganizationsRepository {
    private val TAG = OrganizationsRepositoryImpl::class.simpleName

    override fun searchForOrganizations(
        organizationsSearchDescriptor: VCLOrganizationsSearchDescriptor,
        completionBlock: (VCLResult<VCLOrganizations>) -> Unit
    ) {
        val endpoint = organizationsSearchDescriptor.queryParams
            ?.let { qp -> Urls.Organizations + "?" + qp } ?: Urls.Organizations
        networkService.sendRequest(
            endpoint = endpoint,
            contentType = Request.ContentTypeApplicationJson,
            method = Request.HttpMethod.GET,
            headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)),
            completionBlock = { result ->
                result.handleResult(
                    { organizationsResponse ->
                        try {
                            completionBlock(VCLResult.Success(
                                parse(organizationsResponse.payload)
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

    private fun parse(organizationsStr: String): VCLOrganizations {
        val organizations = mutableListOf<VCLOrganization>()
        organizationsStr.toJsonObject()?.optJSONArray(VCLOrganizations.KeyResult)?.let { organizationsJsonArray ->
            for (i in 0 until organizationsJsonArray.length()) {
                organizationsJsonArray.optJSONObject(i)?.let { obj ->
                    organizations.add(VCLOrganization(obj))
                }
            }
        }
        return VCLOrganizations(organizations)
    }
}