package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.ExchangeProgressRepository
import io.velocitycareerlabs.impl.extensions.encode
import org.json.JSONObject
import java.lang.Exception

/**
 * Created by Michael Avoyan on 30/05/2021.
 */
internal class ExchangeProgressRepositoryImpl(
    private val networkService: NetworkService
): ExchangeProgressRepository {

    override fun getExchangeProgress(exchangeDescriptor: VCLExchangeDescriptor,
                                     completionBlock: (VCLResult<VCLExchange>) -> Unit) {
        networkService.sendRequest(
            endpoint = exchangeDescriptor.processUri +
                    "?${VCLExchangeDescriptor.KeyExchangeId}=${exchangeDescriptor.exchangeId.encode()}",
            headers = listOf(Pair(
                HeaderKeys.HeaderKeyAuthorization,
                "${HeaderKeys.HeaderValuePrefixBearer} ${exchangeDescriptor.token.value}"
            )),
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
                            completionBlock(VCLResult.Failure(VCLError(ex.message)))
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
            id = exchangeJsonObj.getString(VCLExchange.KeyId),
            type = exchangeJsonObj.getString(VCLExchange.KeyType),
            disclosureComplete = exchangeJsonObj.getBoolean(VCLExchange.KeyDisclosureComplete),
            exchangeComplete = exchangeJsonObj.getBoolean(VCLExchange.KeyExchangeComplete)
        )
}