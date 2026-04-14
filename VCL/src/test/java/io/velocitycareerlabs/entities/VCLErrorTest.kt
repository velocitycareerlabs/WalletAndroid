/**
 * Created by Michael Avoyan on 08/03/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.infrastructure.resources.valid.ErrorMocks
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VCLErrorTest {
    private val diagnostic = VCLError.Diagnostic(
        nativeErrorType = "NativeErrorType",
        nativeCauseType = "NativeCauseType",
        nativeCauseMessage = "NativeCauseMessage",
    )

    @Test
    fun testErrorFromPayload() {
        val payloadJson = JSONObject(ErrorMocks.Payload)
        val error = VCLError.fromPayloadJson(payloadJson)

        assertEquals(payloadJson.toString(), error.payload)
        assertEquals(ErrorMocks.Error, error.error)
        assertEquals(ErrorMocks.ErrorCode, error.errorCode)
        assertEquals(ErrorMocks.RequestId, error.requestId)
        assertEquals(ErrorMocks.Message, error.message)
        assertEquals(ErrorMocks.StatusCode, error.statusCode)
        assertEquals(VCLError.ValueNativePlatformAndroid, error.diagnostic?.nativePlatform)
        assertEquals(VCLError.ValuePayloadDiagnosticType, error.diagnostic?.nativeErrorType)
        assertEquals(null, error.diagnostic?.nativeCauseType)
        assertEquals(null, error.diagnostic?.nativeCauseMessage)
    }

    @Test
    fun testErrorFromProperties() {
        val error = VCLError(
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            requestId = ErrorMocks.RequestId,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode,
            diagnostic = diagnostic,
        )

        assertEquals(ErrorMocks.Error, error.error)
        assertEquals(ErrorMocks.ErrorCode, error.errorCode)
        assertEquals(ErrorMocks.RequestId, error.requestId)
        assertEquals(ErrorMocks.Message, error.message)
        assertEquals(ErrorMocks.StatusCode, error.statusCode)
        assertEquals(diagnostic, error.diagnostic)
    }

    @Test
    fun testErrorFromException() {
        val exception = IllegalStateException("boom", IllegalArgumentException("cause"))
        val error = VCLError(exception = exception, statusCode = ErrorMocks.StatusCode)

        assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
        assertEquals(exception.toString(), error.message)
        assertEquals(ErrorMocks.StatusCode, error.statusCode)
        assertEquals(VCLError.ValueNativePlatformAndroid, error.diagnostic?.nativePlatform)
        assertEquals(IllegalStateException::class.java.name, error.diagnostic?.nativeErrorType)
        assertEquals(IllegalArgumentException::class.java.name, error.diagnostic?.nativeCauseType)
        assertEquals("cause", error.diagnostic?.nativeCauseMessage)
    }

    @Test
    fun testErrorToJsonFromPayload() {
        val payloadJson = JSONObject(ErrorMocks.Payload)
        val error = VCLError.fromPayloadJson(payloadJson)
        val errorJsonObject = error.toJsonObject()
        val expectedJsonObject = JSONObject()
            .put(VCLError.KeyPayload, payloadJson.toString())
            .put(VCLError.KeyError, ErrorMocks.Error)
            .put(VCLError.KeyErrorCode, ErrorMocks.ErrorCode)
            .put(VCLError.KeyRequestId, ErrorMocks.RequestId)
            .put(VCLError.KeyMessage, ErrorMocks.Message)
            .put(VCLError.KeyStatusCode, ErrorMocks.StatusCode)

        assertTrue(errorJsonObject.similar(expectedJsonObject))
    }

    @Test
    fun testErrorToDiagnosticJsonFromPayload() {
        val payloadJson = JSONObject(ErrorMocks.Payload)
        val error = VCLError.fromPayloadJson(payloadJson)
        val errorJsonObject = error.toDiagnosticJsonObject()
        val expectedJsonObject = JSONObject()
            .put(VCLError.KeyPayload, payloadJson.toString())
            .put(VCLError.KeyError, ErrorMocks.Error)
            .put(VCLError.KeyErrorCode, ErrorMocks.ErrorCode)
            .put(VCLError.KeyRequestId, ErrorMocks.RequestId)
            .put(VCLError.KeyMessage, ErrorMocks.Message)
            .put(VCLError.KeyStatusCode, ErrorMocks.StatusCode)

        assertTrue(errorJsonObject.similar(expectedJsonObject.apply {
            put(
                VCLError.KeyDiagnostic,
                JSONObject()
                    .put(VCLError.KeyNativePlatform, VCLError.ValueNativePlatformAndroid)
                    .put(VCLError.KeyNativeErrorType, VCLError.ValuePayloadDiagnosticType)
            )
        }))
    }

    @Test
    fun testErrorToJsonFromProperties() {
        val error = VCLError(
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            requestId = ErrorMocks.RequestId,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode,
            diagnostic = diagnostic,
        )
        val errorJsonObject = error.toJsonObject()
        val expectedJsonObject = JSONObject()
            .put(VCLError.KeyError, ErrorMocks.Error)
            .put(VCLError.KeyErrorCode, ErrorMocks.ErrorCode)
            .put(VCLError.KeyRequestId, ErrorMocks.RequestId)
            .put(VCLError.KeyMessage, ErrorMocks.Message)
            .put(VCLError.KeyStatusCode, ErrorMocks.StatusCode)

        assertTrue(errorJsonObject.similar(expectedJsonObject))
    }

    @Test
    fun testErrorToDiagnosticJsonFromProperties() {
        val error = VCLError(
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            requestId = ErrorMocks.RequestId,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode,
            diagnostic = diagnostic,
        )
        val errorJsonObject = error.toDiagnosticJsonObject()
        val expectedJsonObject = JSONObject()
            .put(VCLError.KeyError, ErrorMocks.Error)
            .put(VCLError.KeyErrorCode, ErrorMocks.ErrorCode)
            .put(VCLError.KeyRequestId, ErrorMocks.RequestId)
            .put(VCLError.KeyMessage, ErrorMocks.Message)
            .put(VCLError.KeyStatusCode, ErrorMocks.StatusCode)
            .put(
                VCLError.KeyDiagnostic,
                JSONObject()
                    .put(VCLError.KeyNativePlatform, VCLError.ValueNativePlatformAndroid)
                    .put(VCLError.KeyNativeErrorType, diagnostic.nativeErrorType)
                    .put(VCLError.KeyNativeCauseType, diagnostic.nativeCauseType)
                    .put(VCLError.KeyNativeCauseMessage, diagnostic.nativeCauseMessage)
            )

        assertTrue(errorJsonObject.similar(expectedJsonObject))
    }
}
