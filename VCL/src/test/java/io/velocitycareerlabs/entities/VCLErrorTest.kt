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
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class VCLErrorTest {
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
        assertNull(error.cause)
    }

    @Test
    fun testErrorFromProperties() {
        val cause = IllegalStateException("manual cause")
        val error = VCLError(
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            requestId = ErrorMocks.RequestId,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode,
        )
        error.initCause(cause)

        assertEquals(ErrorMocks.Error, error.error)
        assertEquals(ErrorMocks.ErrorCode, error.errorCode)
        assertEquals(ErrorMocks.RequestId, error.requestId)
        assertEquals(ErrorMocks.Message, error.message)
        assertEquals(ErrorMocks.StatusCode, error.statusCode)
        assertSame(cause, error.cause)
    }

    @Test
    fun testErrorFromException() {
        val exception = IllegalStateException("boom", IllegalArgumentException("cause"))
        val error = VCLError(exception = exception, statusCode = ErrorMocks.StatusCode)

        assertEquals(VCLErrorCode.SdkError.value, error.errorCode)
        assertEquals(exception.toString(), error.message)
        assertEquals(ErrorMocks.StatusCode, error.statusCode)
        assertSame(exception, error.cause)
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
    fun testErrorToJsonFromProperties() {
        val error = VCLError(
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            requestId = ErrorMocks.RequestId,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode,
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
}
