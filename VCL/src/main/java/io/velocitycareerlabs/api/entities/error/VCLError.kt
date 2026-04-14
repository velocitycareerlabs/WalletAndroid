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
    val diagnostic: Diagnostic? = null,
) : Error(message) {
    data class Diagnostic(
        val nativePlatform: String = ValueNativePlatformAndroid,
        val nativeErrorType: String? = null,
        val nativeCauseType: String? = null,
        val nativeCauseMessage: String? = null,
    ) {
        fun toJsonObject() =
            JSONObject().apply {
                putOpt(KeyNativePlatform, nativePlatform)
                putOpt(KeyNativeErrorType, nativeErrorType)
                putOpt(KeyNativeCauseType, nativeCauseType)
                putOpt(KeyNativeCauseMessage, nativeCauseMessage)
            }
    }

    private var wrapperStackFramesInternal: List<String>? = null
    private var causeStackFramesInternal: List<String>? = null

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
    ) {
        wrapperStackFramesInternal = captureCurrentStackFrames()
    }

    constructor(
        exception: Exception,
        errorCode: String = VCLErrorCode.SdkError.value,
        statusCode: Int? = null,
    ) : this(
        errorCode = errorCode,
        message = exception.toString(),
        statusCode = statusCode,
        diagnostic = captureDiagnostic(exception),
    ) {
        wrapperStackFramesInternal = exception.stackTrace.toFrameStrings()
        causeStackFramesInternal = exception.cause?.stackTrace?.toFrameStrings()
    }

    fun toJsonObject() =
        JSONObject().apply {
            putOpt(KeyPayload, payload)
            putOpt(KeyError, error)
            putOpt(KeyErrorCode, errorCode)
            putOpt(KeyRequestId, requestId)
            putOpt(KeyMessage, message)
            putOpt(KeyStatusCode, statusCode)
        }

    fun toDiagnosticJsonObject() =
        toJsonObject().apply {
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
        ).also {
            it.wrapperStackFramesInternal = captureCurrentStackFrames()
        }

        private fun captureDiagnostic(exception: Exception) =
            captureDiagnostic(
                nativeErrorType = exception::class.java.name,
                nativeCauseType = exception.cause?.javaClass?.name,
                nativeCauseMessage = exception.cause?.message ?: exception.cause?.toString(),
            )

        private fun captureDiagnostic(
            nativeErrorType: String,
            nativeCauseType: String? = null,
            nativeCauseMessage: String? = null,
        ) = Diagnostic(
            nativeErrorType = nativeErrorType,
            nativeCauseType = nativeCauseType,
            nativeCauseMessage = nativeCauseMessage,
        )

        private fun captureCurrentStackFrames() = Throwable().stackTrace.toFrameStrings()

        private fun Array<StackTraceElement>.toFrameStrings() = map { it.toString() }

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
        const val KeyNativeCauseType = "nativeCauseType"
        const val KeyNativeCauseMessage = "nativeCauseMessage"
        const val ValueNativePlatformAndroid = "android"
        const val ValuePayloadDiagnosticType = "VCLErrorPayload"
    }
}
