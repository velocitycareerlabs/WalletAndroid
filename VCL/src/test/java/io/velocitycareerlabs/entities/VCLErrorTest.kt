/**
 * Created by Michael Avoyan on 08/03/2023.
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLError
import io.velocitycareerlabs.infrastructure.resources.valid.ErrorMocks
import org.junit.Test

class VCLErrorTest {

    @Test
    fun testErrorFromPayload() {
        val error = VCLError(ErrorMocks.Payload)

        assert(error.payload == ErrorMocks.Payload)
        assert(error.error == ErrorMocks.Error)
        assert(error.errorCode == ErrorMocks.ErrorCode)
        assert(error.message == ErrorMocks.Message)
        assert(error.statusCode == ErrorMocks.StatusCode)
    }

    @Test
    fun testErrorFromProperties() {
        val error = VCLError(
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode
        )

        assert(error.payload == null)
        assert(error.error == ErrorMocks.Error)
        assert(error.errorCode == ErrorMocks.ErrorCode)
        assert(error.message == ErrorMocks.Message)
        assert(error.statusCode == ErrorMocks.StatusCode)
    }

    @Test
    fun testErrorToJsonFromPayload() {
        val error = VCLError(ErrorMocks.Payload)
        val errorJsonObject = error.toJsonObject()

        assert(errorJsonObject.optString(VCLError.KeyPayload) == ErrorMocks.Payload)
        assert(errorJsonObject.optString(VCLError.KeyError) == ErrorMocks.Error)
        assert(errorJsonObject.optString(VCLError.KeyErrorCode) == ErrorMocks.ErrorCode)
        assert(errorJsonObject.optString(VCLError.KeyMessage) == ErrorMocks.Message)
        assert(errorJsonObject.optInt(VCLError.KeyStatusCode) == ErrorMocks.StatusCode)
    }

    @Test
    fun testErrorToJsonFromProperties() {
        val error = VCLError(
            error = ErrorMocks.Error,
            errorCode = ErrorMocks.ErrorCode,
            message = ErrorMocks.Message,
            statusCode = ErrorMocks.StatusCode
        )
        val errorJsonObject = error.toJsonObject()

        assert(errorJsonObject.optString(VCLError.KeyPayload) == "")
        assert(errorJsonObject.optString(VCLError.KeyError) == ErrorMocks.Error)
        assert(errorJsonObject.optString(VCLError.KeyErrorCode) == ErrorMocks.ErrorCode)
        assert(errorJsonObject.optString(VCLError.KeyMessage) == ErrorMocks.Message)
        assert(errorJsonObject.optInt(VCLError.KeyStatusCode) == ErrorMocks.StatusCode)
    }
}