/**
 * Created by Michael Avoyan on 30/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.ExchangeProgressRepository
import io.velocitycareerlabs.impl.extensions.encode
import org.json.JSONObject
import java.lang.Exception

internal class ExchangeProgressRepositoryImpl(
    private val networkService: NetworkService
): ExchangeProgressRepository {

    override fun getExchangeProgress(
        exchangeDescriptor: VCLExchangeDescriptor,
        completionBlock: (VCLResult<VCLExchange>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = exchangeDescriptor.processUri +
                    "?${VCLExchangeDescriptor.KeyExchangeId}=${exchangeDescriptor.exchangeId.encode()}",
            headers = listOf(
                Pair(HeaderKeys.Authorization, "${HeaderValues.PrefixBearer} ${exchangeDescriptor.sessionToken.value}"),
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)
                ),
            method = Request.HttpMethod.GET,
            contentType = Request.ContentTypeApplicationJson,
            completionBlock = { submissionResult ->
                submissionResult.handleResult(
                    { exchangeProgressResponse ->
                        try {
                            completionBlock(VCLResult.Success(
                                parseExchange(JSONObject(exchangeProgressResponse.payload))
                            ))
                        } catch (ex: Exception) {
                            completionBlock(VCLResult.Failure(VCLError(ex)))
                        }
                    },
                    {
                        completionBlock(VCLResult.Failure(it))
                    }
                )
            }
        )
    }

    private fun parseExchange(exchangeJsonObj: JSONObject) =
        VCLExchange(
            id = exchangeJsonObj.optString(VCLExchange.KeyId),
            type = exchangeJsonObj.optString(VCLExchange.KeyType),
            disclosureComplete = exchangeJsonObj.optBoolean(VCLExchange.KeyDisclosureComplete),
            exchangeComplete = exchangeJsonObj.optBoolean(VCLExchange.KeyExchangeComplete)
        )
}