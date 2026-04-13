/**
 * Created by Michael Avoyan on 08/03/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.infrastructure.resources.valid.ErrorMocks
import org.json.JSONObject
import org.junit.Test

class VCLErrorTest {

    @Test
    fun testErrorFromPayload() {
        val payloadJson = JSONObject(ErrorMocks.Payload)
        val error = VCLError.fromPayloadJson(payloadJson)
        val expectedError = VCLError(
            payload = payloadJson.toString(),
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            requestId = ErrorMocks.RequestId,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode
        )

        assert(error == expectedError)
    }

    @Test
    fun testErrorFromProperties() {
        val error = VCLError(
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            requestId = ErrorMocks.RequestId,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode
        )
        val expectedError = VCLError(
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            requestId = ErrorMocks.RequestId,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode
        )

        assert(error == expectedError)
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

        assert(errorJsonObject.similar(expectedJsonObject))
    }

    @Test
    fun testErrorToJsonFromProperties() {
        val error = VCLError(
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            requestId = ErrorMocks.RequestId,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode
        )
        val errorJsonObject = error.toJsonObject()
        val expectedJsonObject = JSONObject()
            .put(VCLError.KeyError, ErrorMocks.Error)
            .put(VCLError.KeyErrorCode, ErrorMocks.ErrorCode)
            .put(VCLError.KeyRequestId, ErrorMocks.RequestId)
            .put(VCLError.KeyMessage, ErrorMocks.Message)
            .put(VCLError.KeyStatusCode, ErrorMocks.StatusCode)

        assert(errorJsonObject.similar(expectedJsonObject))
    }
}
