/**
 * Created by Michael Avoyan on 09/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.CredentialManifestRepository
import io.velocitycareerlabs.impl.utils.ErrorTaxonomy
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
                headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)),
                completionBlock = { result ->
                    result.handleResult(
                        { credentialManifestResponse ->
                            try {
                                val jwtStr = JSONObject(credentialManifestResponse.payload)
                                    .optString(VCLCredentialManifest.KeyIssuingRequest)
                                if (jwtStr.isBlank()) {
                                    completionBlock(
                                        VCLResult.Failure(
                                            ErrorTaxonomy.classifyClientRequestFetch(
                                                VCLError(message = "Credential manifest response is missing issuing_request"),
                                                requestUri = endpoint,
                                                requestKind = ErrorTaxonomy.RequestKindIssuing,
                                            )
                                        )
                                    )
                                } else {
                                    completionBlock(VCLResult.Success(jwtStr))
                                }
                            } catch (ex: Exception) {
                                completionBlock(
                                    VCLResult.Failure(
                                        ErrorTaxonomy.classifyClientRequestFetch(
                                            VCLError(ex),
                                            requestUri = endpoint,
                                            requestKind = ErrorTaxonomy.RequestKindIssuing,
                                        )
                                    )
                                )
                            }
                        },
                        { error ->
                            completionBlock(
                                VCLResult.Failure(
                                    ErrorTaxonomy.classifyClientRequestFetch(
                                        error,
                                        requestUri = endpoint,
                                        requestKind = ErrorTaxonomy.RequestKindIssuing,
                                    )
                                )
                            )
                        }
                    )
                }
            )
        } ?: run {
            completionBlock(
                VCLResult.Failure(
                    VCLError(message = "credentialManifestDescriptor.endpoint = null")
                )
            )
        }
    }
}
