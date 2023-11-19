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
    val requestUri: String? = generateUri(uriKey = KeyRequestUri)
    val vendorOriginContext: String? = retrieveQueryParam(KeyVendorOriginContext)
//    val did: String? = requestUri?.getUrlSubPath(KeyDidPrefix) ?: issuer?.getUrlSubPath(KeyDidPrefix)
    val did: String? = retrieveQueryParam(KeyIssuerDid) ?: retrieveQueryParam(KeyInspectorDid)

    private fun generateUri(uriKey: String, asSubParams: Boolean = false): String? {
        this.value.decode().getUrlQueryParams()?.let { queryParams ->
            queryParams[uriKey]?.let { uri ->
                val queryItems = queryParams
                    .filter { it.key != uriKey && it.value.isNotEmpty() }
                    .map { (key, value) -> "$key=${value.encode()}" }
                    .joinToString("&")
                if (queryItems.isNotEmpty()) {
                    return if(asSubParams) "$uri&$queryItems" else uri.appendQueryParams(queryItems)
                }
                return uri
            }
        }
        return null
    }

    private fun retrieveQueryParam(key: String): String? =
        this.value.decode().getUrlQueryParams()?.get(key)

    companion object CodingKeys {
        const val KeyRequestUri = "request_uri"
        const val KeyVendorOriginContext = "vendorOriginContext"
        const val KeyIssuerDid = "issuerDid"
        const val KeyInspectorDid = "inspectorDid"
    }
}