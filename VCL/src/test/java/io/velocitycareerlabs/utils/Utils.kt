/**
 * Created by Michael Avoyan on 26/05/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.utils

import io.velocitycareerlabs.api.entities.VCLExchange
import io.velocitycareerlabs.api.entities.VCLSubmissionResult
import io.velocitycareerlabs.api.entities.VCLToken
import org.json.JSONObject

internal fun expectedPresentationSubmissionResult(
    jsonObj: JSONObject,
    jti: String,
    submissionId: String
): VCLSubmissionResult {
    val exchangeJsonObj = jsonObj.optJSONObject(VCLSubmissionResult.CodingKeys.KeyExchange)!!
    return VCLSubmissionResult(
        sessionToken = VCLToken(value = (jsonObj[VCLSubmissionResult.CodingKeys.KeyToken] as String)),
        exchange = expectedExchange(exchangeJsonObj),
        jti = jti,
        submissionId = submissionId
    )
}

internal fun expectedExchange(exchangeJsonObj: JSONObject): VCLExchange {
    return VCLExchange(
        id = (exchangeJsonObj.optString(VCLExchange.CodingKeys.KeyId)),
        type = (exchangeJsonObj.optString(VCLExchange.CodingKeys.KeyType)),
        disclosureComplete = (exchangeJsonObj[VCLExchange.CodingKeys.KeyDisclosureComplete] as Boolean),
        exchangeComplete = (exchangeJsonObj[VCLExchange.CodingKeys.KeyExchangeComplete] as Boolean)
    )
}