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
import io.velocitycareerlabs.impl.domain.repositories.SubmissionRepository
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject
import java.lang.Exception

internal class SubmissionRepositoryImpl(
    private val networkService: NetworkService
): SubmissionRepository {
    val TAG = SubmissionRepositoryImpl::class.simpleName

    override fun submit(
        submission: VCLSubmission,
        jwt: VCLJwt,
        authToken: VCLAuthToken?,
        completionBlock: (VCLResult<VCLSubmissionResult>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = submission.submitUri,
            body = submission.generateRequestBody(jwt).toString(),
            method = Request.HttpMethod.POST,
            headers = generateHeader(authToken),
            contentType = Request.ContentTypeApplicationJson,
            completionBlock = { result ->
                result.handleResult(
                    { submissionResponse ->
                        try {
                            val jsonObj = submissionResponse.payload.toJsonObject()
                            val submissionResult =
                                parse(jsonObj, submission.jti, submission.submissionId)
                            completionBlock(VCLResult.Success(submissionResult))
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

    internal fun generateHeader(authToken: VCLAuthToken? = null): List<Pair<String, String>> {
        val header =
            mutableListOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion))
        authToken?.let {
            header.add(
                Pair(
                    HeaderKeys.Authorization,
                    "${HeaderValues.PrefixBearer} ${authToken.accessToken.value}"
                )
            )
        }
        return header
    }

    private fun parse(
        jsonObj: JSONObject?,
        jti: String,
        submissionId: String
    ): VCLSubmissionResult {
        val exchangeJsonObj = jsonObj?.optJSONObject(VCLSubmissionResult.KeyExchange)
        return VCLSubmissionResult(
            sessionToken = VCLToken(jsonObj?.optString(VCLSubmissionResult.KeyToken) ?: ""),
            exchange = parseExchange(exchangeJsonObj),
            jti = jti,
            submissionId = submissionId
        )
    }

    private fun parseExchange(exchangeJsonObj: JSONObject?) =
        VCLExchange(
            id = exchangeJsonObj?.optString(VCLExchange.KeyId) ?: "",
            type = exchangeJsonObj?.optString(VCLExchange.KeyType) ?: "",
            disclosureComplete = exchangeJsonObj?.optBoolean(VCLExchange.KeyDisclosureComplete)
                ?: false,
            exchangeComplete = exchangeJsonObj?.optBoolean(VCLExchange.KeyExchangeComplete) ?: false
        )
}