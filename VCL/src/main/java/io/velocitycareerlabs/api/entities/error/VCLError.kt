/**
 * Created by Michael Avoyan on 07/03/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities.error

import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject

class VCLError(
): Error() {
    var payload: String? = null
    var error: String? = null
    var errorCode: String = VCLErrorCode.SdkError.value
    var requestId: String? = null
    override var message: String? = null
    var statusCode: Int? = null

    constructor(
        error: String? = null,
        errorCode: String = VCLErrorCode.SdkError.value,
        requestId: String? = null,
        message: String? = null,
        statusCode: Int? = null,
    ) : this() {
        this.error = error
        this.errorCode = errorCode
        this.requestId = requestId
        this.message = message
        this.statusCode = statusCode
    }

    constructor(
        payload: String?,
        errorCode: String? = null
    ): this() {
        val payloadJson = payload?.toJsonObject()
        this.payload = payload
        this.error = payloadJson?.optString(KeyError)
        this.errorCode = errorCode ?: payloadJson?.optString(KeyErrorCode) ?: VCLErrorCode.SdkError.value
        this.requestId = payloadJson?.optString(KeyRequestId)
        this.message = payloadJson?.optString(KeyMessage)
        this.statusCode = payloadJson?.optInt(KeyStatusCode)
    }

    constructor(
        exception: Exception,
        errorCode: String = VCLErrorCode.SdkError.value,
        statusCode: Int? = null
    ): this() {
        this.errorCode = errorCode
        this.message = exception.toString()
        this.statusCode = statusCode
    }

    fun toJsonObject() = JSONObject()
        .putOpt(KeyPayload, payload)
        .putOpt(KeyError, error)
        .putOpt(KeyErrorCode, errorCode)
        .putOpt(KeyRequestId, requestId)
        .putOpt(KeyMessage, message)
        .putOpt(KeyStatusCode, statusCode)

    companion object CodingKeys {
        const val KeyPayload = "payload"
        const val KeyError = "error"
        const val KeyErrorCode = "errorCode"
        const val KeyRequestId = "requestId"
        const val KeyMessage = "message"
        const val KeyStatusCode = "statusCode"
    }
}