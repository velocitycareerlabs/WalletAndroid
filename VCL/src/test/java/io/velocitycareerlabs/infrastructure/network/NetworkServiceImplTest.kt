/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.network

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.data.infrastructure.network.NetworkServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.infrastructure.resources.valid.ErrorMocks
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

internal class NetworkServiceImplTest {
    @Test
    fun testJsonErrorBodyUsesPayloadJsonFactory() {
        val payloadJson = JSONObject(ErrorMocks.Payload)

        val error = sendFailure(
            payload = ErrorMocks.Payload,
            contentType = Request.ContentTypeApplicationJson,
            responseCode = 400
        )

        assertPayloadJsonEquals(payloadJson, error)
        assertEquals(
            VCLError(
                payload = error.payload,
                error = ErrorMocks.Error,
                errorCode = ErrorMocks.ErrorCode,
                requestId = ErrorMocks.RequestId,
                message = ErrorMocks.Message,
                statusCode = ErrorMocks.StatusCode
            ),
            error
        )
    }

    @Test
    fun testPlusJsonErrorBodyUsesPayloadJsonFactory() {
        val payloadJson = JSONObject(ErrorMocks.Payload)

        val error = sendFailure(
            payload = ErrorMocks.Payload,
            contentType = "application/problem+json; charset=UTF-8",
            responseCode = 400
        )

        assertPayloadJsonEquals(payloadJson, error)
        assertEquals(
            VCLError(
                payload = error.payload,
                error = ErrorMocks.Error,
                errorCode = ErrorMocks.ErrorCode,
                requestId = ErrorMocks.RequestId,
                message = ErrorMocks.Message,
                statusCode = ErrorMocks.StatusCode
            ),
            error
        )
    }

    @Test
    fun testJsonErrorBodyWithoutStatusCodeFallsBackToHttpStatus() {
        val payloadJson = JSONObject(ErrorMocks.Payload).apply {
            remove(VCLError.KeyStatusCode)
        }

        val error = sendFailure(
            payload = payloadJson.toString(),
            contentType = Request.ContentTypeApplicationJson,
            responseCode = 422
        )

        assertPayloadJsonEquals(payloadJson, error)
        assertEquals(
            VCLError(
                payload = error.payload,
                error = ErrorMocks.Error,
                errorCode = ErrorMocks.ErrorCode,
                requestId = ErrorMocks.RequestId,
                message = ErrorMocks.Message,
                statusCode = 422
            ),
            error
        )
    }

    @Test
    fun testMalformedJsonBodyFallsBackToPlainTextError() {
        val malformedPayload = "{not valid json"

        val error = sendFailure(
            payload = malformedPayload,
            contentType = Request.ContentTypeApplicationJson,
            responseCode = 502
        )

        assertEquals(
            VCLError(
                payload = malformedPayload,
                errorCode = VCLErrorCode.SdkError.value,
                message = malformedPayload,
                statusCode = 502
            ),
            error
        )
    }

    @Test
    fun testPlainTextErrorBodyUsesPlainTextError() {
        val textPayload = "server error"

        val error = sendFailure(
            payload = textPayload,
            contentType = "text/plain",
            responseCode = 500
        )

        assertEquals(
            VCLError(
                payload = textPayload,
                errorCode = VCLErrorCode.SdkError.value,
                message = textPayload,
                statusCode = 500
            ),
            error
        )
    }

    private fun sendFailure(
        payload: String,
        contentType: String?,
        responseCode: Int,
    ): VCLError {
        val subject =
            NetworkServiceImpl(connectionFactory = { request ->
            FakeHttpURLConnection(
                url = URL(request.endpoint),
                responseCodeValue = responseCode,
                contentTypeValue = contentType,
                errorPayload = payload
            )
            })

        var result: VCLResult<io.velocitycareerlabs.impl.data.infrastructure.network.Response>? = null
        subject.sendRequest(
            endpoint = "http://example.com/error",
            body = null,
            contentType = Request.ContentTypeApplicationJson,
            method = Request.HttpMethod.GET,
            headers = null,
            useCaches = false
        ) {
            result = it
        }

        return (result as? VCLResult.Failure)?.error
            ?: error("Expected NetworkServiceImpl to return VCLResult.Failure")
    }

    private fun assertPayloadJsonEquals(
        expectedPayloadJson: JSONObject,
        error: VCLError,
    ) {
        assertTrue(JSONObject(error.payload!!).similar(expectedPayloadJson))
    }

    private class FakeHttpURLConnection(
        url: URL,
        private val responseCodeValue: Int,
        private val contentTypeValue: String?,
        private val errorPayload: String?,
    ) : HttpURLConnection(url) {
        override fun connect() = Unit

        override fun disconnect() = Unit

        override fun usingProxy() = false

        override fun getResponseCode(): Int = responseCodeValue

        override fun getContentType(): String? = contentTypeValue

        override fun getErrorStream(): InputStream? =
            errorPayload?.let { ByteArrayInputStream(it.toByteArray()) }

        override fun getInputStream(): InputStream =
            ByteArrayInputStream((errorPayload ?: "").toByteArray())
    }
}
