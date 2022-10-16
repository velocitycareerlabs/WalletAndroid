/**
 * Created by Michael Avoyan on 10/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.infrastructure.network.Response
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.GenerateOffersRepository
import org.json.JSONArray
import java.lang.Exception

internal class GenerateOffersRepositoryImpl(
    private val networkService: NetworkService
): GenerateOffersRepository {
    private val TAG = GenerateOffersRepositoryImpl::class.simpleName

    override fun generateOffers(token: VCLToken,
                                generateOffersDescriptor: VCLGenerateOffersDescriptor,
                                completionBlock: (VCLResult<VCLOffers>) -> Unit) {
        networkService.sendRequest(
            endpoint = generateOffersDescriptor.checkOffersUri,
            headers = listOf(Pair(
                HeaderKeys.HeaderKeyAuthorization,
                "${HeaderKeys.HeaderValuePrefixBearer} ${token.value}"
            )),
            body = generateOffersDescriptor.payload.toString(),
            method = Request.HttpMethod.POST,
            contentType = Request.ContentTypeApplicationJson,
            completionBlock = { result ->
                result.handleResult(
                    { offersResponse ->
                        try {
                            completionBlock(VCLResult.Success(
                                parse(offersResponse, token)
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

    private fun parse(offersResponse: Response, token: VCLToken): VCLOffers {
        return try {
            VCLOffers(
                all = JSONArray(offersResponse.payload),
                responseCode = offersResponse.code,
                token = token
            )
        } catch (ex: Exception) {
            VCLOffers(
                all = JSONArray("[]"),
                responseCode = offersResponse.code,
                token = token
            )
        }
    }
}