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
        val endpoint = credentialManifestDescriptor.endpoint.orEmpty()
        networkService.sendRequest(
            endpoint = endpoint,
            method = Request.HttpMethod.GET,
            headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)),
            completionBlock = { result ->
                result.handleResult(
                    { credentialManifestResponse ->
                        try {
                            val payload = JSONObject(credentialManifestResponse.payload)
                            if (!payload.has(VCLCredentialManifest.KeyIssuingRequest)) {
                                completionBlock(
                                    VCLResult.Failure(
                                        ErrorTaxonomy.toClientRequestFetchError(
                                            VCLError(message = "Missing issuing_request"),
                                            requestUri = endpoint,
                                            requestKind = ErrorTaxonomy.RequestKindIssuing,
                                        )
                                    )
                                )
                            } else {
                                completionBlock(
                                    VCLResult.Success(
                                        payload.optString(VCLCredentialManifest.KeyIssuingRequest)
                                    )
                                )
                            }
                        } catch (ex: Exception) {
                            completionBlock(
                                VCLResult.Failure(
                                    ErrorTaxonomy.toClientRequestFetchError(
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
                                ErrorTaxonomy.toClientRequestFetchError(
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
    }
}
