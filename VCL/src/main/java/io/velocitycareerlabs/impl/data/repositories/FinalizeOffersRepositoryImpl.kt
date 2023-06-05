/**
 * Created by Michael Avoyan on 11/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.FinalizeOffersRepository
import io.velocitycareerlabs.impl.extensions.toList
import org.json.JSONArray
import java.lang.Exception

internal class FinalizeOffersRepositoryImpl(
    private val networkService: NetworkService
): FinalizeOffersRepository {
    private val TAG = FinalizeOffersRepositoryImpl::class.simpleName

    override fun finalizeOffers(
        token: VCLToken,
        proof: VCLJwt,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<List<String>>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = finalizeOffersDescriptor.finalizeOffersUri,
            headers = listOf(
                Pair(
                    HeaderKeys.HeaderKeyAuthorization,
                    "${HeaderKeys.HeaderValuePrefixBearer} ${token.value}"
                ),
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)
            ),
            body = finalizeOffersDescriptor.generateRequestBody(jwt = proof).toString(),
            method = Request.HttpMethod.POST,
            contentType = Request.ContentTypeApplicationJson,
            completionBlock = { result ->
                result.handleResult(
                    { finalizedOffersResponse ->
                        try {
                            val encodedJwts =
                                JSONArray(finalizedOffersResponse.payload).toList() as? List<String>
                            encodedJwts?.let {
                                completionBlock(VCLResult.Success(it))
                            } ?: run {
                                completionBlock(
                                    VCLResult.Failure(
                                        VCLError("Failed to parse: $finalizedOffersResponse.payload")
                                    )
                                )
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
}