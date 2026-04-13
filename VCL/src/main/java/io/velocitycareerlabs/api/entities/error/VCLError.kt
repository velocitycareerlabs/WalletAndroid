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
        replaceWith = ReplaceWith("VCLError.fromPayload(payload, errorCode)")
    )
    constructor(
        payload: String?,
        errorCode: String? = null,
    ) : this(
        payload = payload,
        error = payload?.toJsonObject()?.optString(KeyError),
        errorCode =
            errorCode ?: payload?.toJsonObject()?.optString(KeyErrorCode)
                ?: VCLErrorCode.SdkError.value,
        requestId = payload?.toJsonObject()?.optString(KeyRequestId),
        message = payload?.toJsonObject()?.optString(KeyMessage),
        statusCode = payload?.toJsonObject()?.optInt(KeyStatusCode),
    )

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
        ): VCLError {
            val payloadJson = payload?.toJsonObject()
            return VCLError(
                payload = payload,
                error = payloadJson?.optString(KeyError),
                errorCode = errorCode ?: payloadJson?.optString(KeyErrorCode)
                    ?: VCLErrorCode.SdkError.value,
                requestId = payloadJson?.optString(KeyRequestId),
                message = payloadJson?.optString(KeyMessage),
                statusCode = payloadJson?.optInt(KeyStatusCode),
            )
        }

        const val KeyPayload = "payload"
        const val KeyError = "error"
        const val KeyErrorCode = "errorCode"
        const val KeyRequestId = "requestId"
        const val KeyMessage = "message"
        const val KeyStatusCode = "statusCode"
    }
}
