/**
 * Created by Michael Avoyan on 28/09/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.verifiers.directissuerverification.repositories

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.repositories.HeaderKeys
import io.velocitycareerlabs.impl.data.repositories.HeaderValues
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toMap

internal class CredentialSubjectContextRepositoryImpl(
    private val networkService: NetworkService
): CredentialSubjectContextRepository {
    private val TAG = CredentialSubjectContextRepositoryImpl::class.java.simpleName

    override fun getCredentialSubjectContext(
        credentialSubjectContextEndpoint: String,
        completionBlock: (VCLResult<Map<*, *>>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = credentialSubjectContextEndpoint,
            method = Request.HttpMethod.GET,
            headers = listOf(
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)
            ),
            completionBlock = { result ->
                result.handleResult({ ldContextResponse ->
                    ldContextResponse.payload.toJsonObject()?.toMap()?.let {
                        completionBlock(VCLResult.Success(it))
                    } ?: run {
                        val error = VCLError("Unexpected LD-Context payload for $credentialSubjectContextEndpoint")
                        completionBlock(VCLResult.Failure(error))
                    }
                }, { error ->
                    completionBlock(VCLResult.Failure(error))
                })
            })
    }
}