/**
 * Created by Michael Avoyan on 11/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.FinalizeOffersRepository
import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.impl.extensions.toJwtList
import io.velocitycareerlabs.impl.extensions.toList
import org.json.JSONArray
import java.lang.Exception
import java.util.concurrent.CompletableFuture

internal class FinalizeOffersRepositoryImpl(
    private val networkService: NetworkService
): FinalizeOffersRepository {
    private val TAG = FinalizeOffersRepositoryImpl::class.simpleName

    override fun finalizeOffers(
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        sessionToken: VCLToken,
        proof: VCLJwt?,
        completionBlock: (VCLResult<List<VCLJwt>>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = finalizeOffersDescriptor.finalizeOffersUri,
            headers = listOf(
                Pair(HeaderKeys.Authorization, "${HeaderKeys.Bearer} ${sessionToken.value}"),
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)
            ),
            body = finalizeOffersDescriptor.generateRequestBody(proof = proof).toString(),
            method = Request.HttpMethod.POST,
            contentType = Request.ContentTypeApplicationJson,
            completionBlock = { result ->
                result.handleResult(
                    { finalizedOffersResponse ->
                        try {
                            finalizedOffersResponse.payload.toJwtList()?.let {
                                completionBlock(VCLResult.Success(it))
                            } ?: run {
                                completionBlock(
                                    VCLResult.Failure(
                                        VCLError("Failed to parse: ${finalizedOffersResponse.payload}")
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