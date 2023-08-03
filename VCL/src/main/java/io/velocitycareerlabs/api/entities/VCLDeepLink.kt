/**
 * Created by Michael Avoyan on 8/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.*
import io.velocitycareerlabs.impl.extensions.appendQueryParams
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.impl.extensions.encode
import io.velocitycareerlabs.impl.extensions.getUrlQueryParams
import java.net.URI

data class VCLDeepLink(val value: String) {
    val issuer: String? = generateUri(uriKey = KeyIssuer)
    val requestUri: String? = generateUri(uriKey = KeyRequestUri)
    val vendorOriginContext: String? = retrieveVendorOriginContext()
    val did: String? = requestUri?.getUrlSubPath(KeyDidPrefix) ?: issuer?.getUrlSubPath(KeyDidPrefix)

    private fun generateUri(uriKey: String, asSubParams: Boolean = false): String? {
        this.value.decode().getUrlQueryParams()?.let { queryParams ->
            queryParams[uriKey]?.let { uri ->
                val queryItems = queryParams
                    .filter { it.key != uriKey && it.value.isNotEmpty() }
                    .map { (key, value) -> "$key=${value.encode()}" }
                    .sortedBy { it } // Sort is needed for unit tests
                    .joinToString("&")
                if (queryItems.isNotEmpty()) {
                    return if(asSubParams) "$uri&$queryItems" else uri.appendQueryParams(queryItems)
                }
                return uri
            }
        }
        return null
    }

    private fun retrieveVendorOriginContext(): String? =
        this.value.decode().getUrlQueryParams()?.get(KeyVendorOriginContext)

    companion object CodingKeys {
        const val KeyDidPrefix = "did:"
        const val KeyIssuer = "issuer"
        const val KeyRequestUri = "request_uri"
        const val KeyVendorOriginContext = "vendorOriginContext"
    }
}