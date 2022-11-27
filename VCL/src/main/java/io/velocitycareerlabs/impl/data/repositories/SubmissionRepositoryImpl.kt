/**
 * Created by Michael Avoyan on 4/11/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.repositories.SubmissionRepository
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import org.json.JSONObject
import java.lang.Exception

internal class SubmissionRepositoryImpl(
        private val networkService: NetworkService
): SubmissionRepository {
    val TAG = SubmissionRepositoryImpl::class.simpleName

    override fun submit(submission: VCLSubmission,
                        jwt: VCLJWT,
                        completionBlock: (VCLResult<VCLSubmissionResult>) -> Unit) {
        val body = JSONObject()
            .putOpt(VCLSubmission.KeyDid, submission.iss)
            .putOpt(VCLSubmission.KeyExchangeId, submission.exchangeId)
            .putOpt(VCLSubmission.KeyJwtVp, jwt.signedJwt.serialize())
        networkService.sendRequest(
                endpoint = submission.submitUri,
                body = body.toString(),
                method = Request.HttpMethod.POST,
                contentType = Request.ContentTypeApplicationJson,
                completionBlock = { result ->
                    result.handleResult(
                            { submissionResponse ->
                                try {
                                    val jsonObj = JSONObject(submissionResponse.payload)
                                    val submissionResult = parse(jsonObj, submission.id)
                                    completionBlock(VCLResult.Success(submissionResult))
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

    private fun parse(jsonObj: JSONObject, id: String): VCLPresentationSubmissionResult {
        val exchangeJsonObj = jsonObj.getJSONObject(VCLPresentationSubmissionResult.KeyExchange)
        return VCLPresentationSubmissionResult(
            token = VCLToken(jsonObj.getString(VCLPresentationSubmissionResult.KeyToken)),
            exchange = parseExchange(exchangeJsonObj),
            id = id
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