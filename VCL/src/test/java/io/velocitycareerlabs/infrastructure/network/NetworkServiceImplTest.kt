/**
 * Created by OpenAI Codex on 13/04/2026.
 *
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
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.net.URLStreamHandlerFactory
import java.util.concurrent.atomic.AtomicBoolean

internal class NetworkServiceImplTest {
    private lateinit var subject: NetworkServiceImpl

    @Before
    fun setUp() {
        installMockProtocolFactoryIfNeeded()
        subject = NetworkServiceImpl()
    }

    @Test
    fun testJsonErrorBodyUsesPayloadJsonFactory() {
        val payloadJson = JSONObject(ErrorMocks.Payload)

        val error = sendFailure(
            payload = ErrorMocks.Payload,
            contentType = Request.ContentTypeApplicationJson,
            responseCode = 400
        )

        assertEquals(
            VCLError(
                payload = payloadJson.toString(),
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

        assertEquals(
            VCLError(
                payload = payloadJson.toString(),
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
        nextConnectionFactory = { url ->
            FakeHttpURLConnection(
                url = url,
                responseCodeValue = responseCode,
                contentTypeValue = contentType,
                errorPayload = payload
            )
        }

        var result: VCLResult<io.velocitycareerlabs.impl.data.infrastructure.network.Response>? = null
        subject.sendRequest(
            endpoint = "mockhttp://example.com/error",
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

    private companion object {
        private const val MockProtocol = "mockhttp"
        private val isFactoryInstalled = AtomicBoolean(false)

        @Volatile
        private var nextConnectionFactory: ((URL) -> HttpURLConnection)? = null

        private fun installMockProtocolFactoryIfNeeded() {
            if (isFactoryInstalled.compareAndSet(false, true)) {
                URL.setURLStreamHandlerFactory(
                    URLStreamHandlerFactory { protocol ->
                        if (protocol == MockProtocol) {
                            object : URLStreamHandler() {
                                override fun openConnection(url: URL): URLConnection =
                                    nextConnectionFactory?.invoke(url)
                                        ?: error("No connection factory configured for $url")
                            }
                        } else {
                            null
                        }
                    }
                )
            }
        }
    }
}
