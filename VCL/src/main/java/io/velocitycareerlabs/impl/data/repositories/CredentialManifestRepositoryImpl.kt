/**
 * Created by Michael Avoyan on 09/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.CredentialManifestRepository
import org.json.JSONObject
import java.lang.Exception

internal class CredentialManifestRepositoryImpl(
    val networkService: NetworkService
): CredentialManifestRepository {

    override fun getCredentialManifest(
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        completionBlock: (VCLResult<String>) -> Unit
    ) {
        credentialManifestDescriptor.endpoint?.let { endpoint ->
            networkService.sendRequest(
                endpoint = endpoint,
                method = Request.HttpMethod.GET,
                completionBlock = { result ->
                    result.handleResult(
                        { credentialManifestResponse ->
                            try {
                                val jwtStr = JSONObject(credentialManifestResponse.payload)
                                    .getString(VCLCredentialManifest.KeyIssuingRequest)
                                completionBlock(VCLResult.Success(jwtStr))
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
        } ?: run {
            completionBlock(VCLResult.Failure(VCLError("credentialManifestDescriptor.endpoint = null")))
        }
    }
}