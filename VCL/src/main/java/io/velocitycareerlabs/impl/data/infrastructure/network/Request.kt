/**
 * Created by Michael Avoyan on 3/12/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.infrastructure.network

internal class Request(
        val endpoint: String,
        val body: String?,
        val method: HttpMethod,
        val headers: List<Pair<String, String>>?,
        val doOutput: Boolean,
        val doInput: Boolean,
        val useCaches: Boolean,
        val connectTimeOut: Int,
        val readTimeOut: Int,
        val encoding: String,
        val contentType: String
) {
    companion object {

        const val DefaultReadTimeoutMillis = 60 * 1000 // 60s
        const val DefaultConnectTimeoutMillis = 20 * 1000 // 20s

        const val DefaultEncoding = "UTF-8"
        const val ContentTypeApplicationJson = "application/json"
    }

    enum class HttpMethod(val value: String) {
        POST("POST"),
        GET("GET");
    }

    private constructor(builder: Builder) : this(
            endpoint = builder.endpoint,
            method = builder.method,
            body = builder.body,
            headers = ArrayList(builder.headers),
            doOutput = builder.doOutput,
            doInput = builder.doInput,
            useCaches = builder.useCaches,
            connectTimeOut = builder.connectTimeOut,
            readTimeOut = builder.readTimeOut,
            encoding = builder.encoding,
            contentType = builder.contentType
    )

    class Builder {
        lateinit var endpoint: String
        var body: String? = null
        lateinit var method: HttpMethod
        var headers: MutableList<Pair<String, String>> = mutableListOf()
        var doOutput: Boolean = true
        var doInput: Boolean = true
        var useCaches: Boolean = true
        var connectTimeOut: Int = DefaultConnectTimeoutMillis
        var readTimeOut: Int = DefaultReadTimeoutMillis
        var encoding: String = DefaultEncoding
        var contentType: String = ContentTypeApplicationJson

        fun setBody(body: String?): Builder {
            this.body = body
            return this
        }

        fun setEncoding(encoding: String): Builder {
            this.encoding = encoding
            return this
        }

        fun setReadTimeOut(readTimeOut: Int): Builder {
            this.readTimeOut = readTimeOut
            return this
        }

        fun setConnectTimeOut(connectTimeOut: Int): Builder {
            this.connectTimeOut = connectTimeOut
            return this
        }

        fun setUseCaches(useCaches: Boolean): Builder {
            this.useCaches = useCaches
            return this
        }

        fun setEndpoint(endpoint: String): Builder {
            this.endpoint = endpoint
            return this
        }

        fun addHeader(header: Pair<String, String>): Builder {
            headers.add(header)
            return this
        }

        fun addHeaders(headers: List<Pair<String, String>>?): Builder {
            headers?.let {  this.headers.addAll(it) }
            return this
        }

        fun setContentType(contentType: String): Builder {
            this.contentType = contentType
            return this
        }

        fun setMethod(method: HttpMethod): Builder {
            this.method = method
            return this
        }

        fun setDoOutput(doOutput: Boolean): Builder {
            this.doOutput = doOutput
            return this
        }

        fun build(): Request {
            return Request(this)
        }
    }
}