/**
 * Created by Michael Avoyan on 07/03/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities.error

import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONArray
import org.json.JSONObject

data class VCLError(
    val payload: String? = null,
    val error: String? = null,
    val errorCode: String = VCLErrorCode.SdkError.value,
    val requestId: String? = null,
    override val message: String? = null,
    val statusCode: Int? = null,
    val diagnostic: Diagnostic? = null,
) : Error(message) {
    data class Diagnostic(
        val nativePlatform: String = ValueNativePlatformAndroid,
        val nativeErrorType: String? = null,
        val nativeStackFrames: List<String>? = null,
        val nativeStackTop: String? = null,
        val nativeCause: String? = null,
    ) {
        fun toJsonObject() =
            JSONObject().apply {
                putOpt(KeyNativePlatform, nativePlatform)
                putOpt(KeyNativeErrorType, nativeErrorType)
                putOpt(KeyNativeStackFrames, nativeStackFrames?.let { JSONArray(it) })
                putOpt(KeyNativeStackTop, nativeStackTop)
                putOpt(KeyNativeCause, nativeCause)
            }
    }

    @Deprecated(
        message = "Use named arguments for human-readable text, or VCLError.fromPayloadJson(...) for payload parsing.",
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
        diagnostic = captureDiagnostic(nativeErrorType = ValuePayloadDiagnosticType),
    )

    constructor(
        exception: Exception,
        errorCode: String = VCLErrorCode.SdkError.value,
        statusCode: Int? = null,
    ) : this(
        errorCode = errorCode,
        message = exception.toString(),
        statusCode = statusCode,
        diagnostic = captureDiagnostic(exception),
    )

    fun toJsonObject() =
        JSONObject().apply {
            putOpt(KeyPayload, payload)
            putOpt(KeyError, error)
            putOpt(KeyErrorCode, errorCode)
            putOpt(KeyRequestId, requestId)
            putOpt(KeyMessage, message)
            putOpt(KeyStatusCode, statusCode)
            putOpt(KeyDiagnostic, diagnostic?.toJsonObject())
        }

    companion object CodingKeys {
        fun fromPayloadJson(
            payloadJson: JSONObject,
            errorCode: String? = null,
        ) = VCLError(
            payload = payloadJson.toString(),
            error = payloadJson.optNullableString(KeyError),
            errorCode = errorCode ?: payloadJson.optNullableString(KeyErrorCode)
                ?: VCLErrorCode.SdkError.value,
            requestId = payloadJson.optNullableString(KeyRequestId),
            message = payloadJson.optNullableString(KeyMessage),
            statusCode = payloadJson.optNullableInt(KeyStatusCode),
            diagnostic = captureDiagnostic(nativeErrorType = ValuePayloadDiagnosticType),
        )

        private fun captureDiagnostic(exception: Exception) =
            captureDiagnostic(
                nativeErrorType = exception::class.java.name,
                nativeCause = exception.cause?.toString(),
                nativeStackFrames = exception.stackTrace.map { it.toString() },
            )

        private fun captureDiagnostic(
            nativeErrorType: String,
            nativeCause: String? = null,
            nativeStackFrames: List<String> = Throwable().stackTrace.map { it.toString() },
        ) = Diagnostic(
            nativeErrorType = nativeErrorType,
            nativeStackFrames = nativeStackFrames.ifEmpty { null },
            nativeStackTop = nativeStackFrames.firstOrNull(),
            nativeCause = nativeCause,
        )

        private fun JSONObject?.optNullableString(key: String): String? =
            takeIf { it?.has(key) == true && !it.isNull(key) }?.optString(key)

        private fun JSONObject?.optNullableInt(key: String): Int? =
            takeIf { it?.has(key) == true && !it.isNull(key) }?.optInt(key)

        const val KeyPayload = "payload"
        const val KeyError = "error"
        const val KeyErrorCode = "errorCode"
        const val KeyRequestId = "requestId"
        const val KeyMessage = "message"
        const val KeyStatusCode = "statusCode"
        const val KeyDiagnostic = "diagnostic"
        const val KeyNativePlatform = "nativePlatform"
        const val KeyNativeErrorType = "nativeErrorType"
        const val KeyNativeStackFrames = "nativeStackFrames"
        const val KeyNativeStackTop = "nativeStackTop"
        const val KeyNativeCause = "nativeCause"
        const val ValueNativePlatformAndroid = "android"
        const val ValuePayloadDiagnosticType = "VCLErrorPayload"
    }
}
