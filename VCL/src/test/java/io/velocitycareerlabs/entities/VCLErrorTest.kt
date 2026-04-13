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
        val error = VCLError.fromPayloadJson(JSONObject(ErrorMocks.Payload))

        assert(JSONObject(error.payload ?: "").toString() == JSONObject(ErrorMocks.Payload).toString())
        assert(error.error == ErrorMocks.Error)
        assert(error.errorCode == ErrorMocks.ErrorCode)
        assert(error.requestId == ErrorMocks.RequestId)
        assert(error.message == ErrorMocks.Message)
        assert(error.statusCode == ErrorMocks.StatusCode)
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

        assert(error.payload == null)
        assert(error.error == ErrorMocks.Error)
        assert(error.errorCode == ErrorMocks.ErrorCode)
        assert(error.requestId == ErrorMocks.RequestId)
        assert(error.message == ErrorMocks.Message)
        assert(error.statusCode == ErrorMocks.StatusCode)
    }

    @Test
    fun testErrorToJsonFromPayload() {
        val error = VCLError.fromPayloadJson(JSONObject(ErrorMocks.Payload))
        val errorJsonObject = error.toJsonObject()

        assert(JSONObject(errorJsonObject.optString(VCLError.KeyPayload)).toString() == JSONObject(ErrorMocks.Payload).toString())
        assert(errorJsonObject.optString(VCLError.KeyError) == ErrorMocks.Error)
        assert(errorJsonObject.optString(VCLError.KeyErrorCode) == ErrorMocks.ErrorCode)
        assert(errorJsonObject.optString(VCLError.KeyRequestId) == ErrorMocks.RequestId)
        assert(errorJsonObject.optString(VCLError.KeyMessage) == ErrorMocks.Message)
        assert(errorJsonObject.optInt(VCLError.KeyStatusCode) == ErrorMocks.StatusCode)
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

        assert(errorJsonObject.optString(VCLError.KeyPayload) == "")
        assert(errorJsonObject.optString(VCLError.KeyError) == ErrorMocks.Error)
        assert(errorJsonObject.optString(VCLError.KeyErrorCode) == ErrorMocks.ErrorCode)
        assert(errorJsonObject.optString(VCLError.KeyRequestId) == ErrorMocks.RequestId)
        assert(errorJsonObject.optString(VCLError.KeyMessage) == ErrorMocks.Message)
        assert(errorJsonObject.optInt(VCLError.KeyStatusCode) == ErrorMocks.StatusCode)
    }
}
