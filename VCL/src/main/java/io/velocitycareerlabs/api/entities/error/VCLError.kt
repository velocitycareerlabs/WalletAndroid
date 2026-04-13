/**
 * Created by Michael Avoyan on 07/03/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities.error

import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject

data class VCLError(
    val payload: String? = null,
    val error: String? = null,
    val errorCode: String = VCLErrorCode.SdkError.value,
    val requestId: String? = null,
    override val message: String? = null,
    val statusCode: Int? = null,
) : Error(message) {
    @Deprecated(
        message = "Use named arguments for human-readable text, or VCLError.fromPayload(...) for payload parsing.",
    )
    constructor(
        payload: String?,
        errorCode: String? = null,
    ) : this(parsePayload(payload, payload?.toJsonObject(), errorCode))

    constructor(
        exception: Exception,
        errorCode: String = VCLErrorCode.SdkError.value,
        statusCode: Int? = null,
    ) : this(
        errorCode = errorCode,
        message = exception.toString(),
        statusCode = statusCode,
    )

    fun toJsonObject() =
        JSONObject().apply {
            putOpt(KeyPayload, payload)
            putOpt(KeyError, error)
            putOpt(KeyErrorCode, errorCode)
            putOpt(KeyRequestId, requestId)
            putOpt(KeyMessage, message)
            putOpt(KeyStatusCode, statusCode)
        }

    companion object CodingKeys {
        fun fromPayload(
            payload: String?,
            errorCode: String? = null,
        ) = VCLError(parsePayload(payload, payload?.toJsonObject(), errorCode))

        internal fun fromPayload(
            payload: String?,
            payloadJson: JSONObject,
            errorCode: String? = null,
        ) = VCLError(parsePayload(payload, payloadJson, errorCode))

        private fun parsePayload(
            payload: String?,
            payloadJson: JSONObject?,
            errorCode: String?,
        ): ParsedPayload {
            return ParsedPayload(
                payload = payload,
                error = payloadJson.optNullableString(KeyError),
                errorCode = errorCode ?: payloadJson.optNullableString(KeyErrorCode)
                    ?: VCLErrorCode.SdkError.value,
                requestId = payloadJson.optNullableString(KeyRequestId),
                message = payloadJson.optNullableString(KeyMessage),
                statusCode = payloadJson.optNullableInt(KeyStatusCode),
            )
        }

        private fun JSONObject?.optNullableString(key: String): String? =
            takeIf { it?.has(key) == true && !it.isNull(key) }?.optString(key)

        private fun JSONObject?.optNullableInt(key: String): Int? =
            takeIf { it?.has(key) == true && !it.isNull(key) }?.optInt(key)

        private data class ParsedPayload(
            val payload: String?,
            val error: String?,
            val errorCode: String,
            val requestId: String?,
            val message: String?,
            val statusCode: Int?,
        )

        const val KeyPayload = "payload"
        const val KeyError = "error"
        const val KeyErrorCode = "errorCode"
        const val KeyRequestId = "requestId"
        const val KeyMessage = "message"
        const val KeyStatusCode = "statusCode"
    }

    private constructor(parsedPayload: ParsedPayload) : this(
        payload = parsedPayload.payload,
        error = parsedPayload.error,
        errorCode = parsedPayload.errorCode,
        requestId = parsedPayload.requestId,
        message = parsedPayload.message,
        statusCode = parsedPayload.statusCode,
    )
}
