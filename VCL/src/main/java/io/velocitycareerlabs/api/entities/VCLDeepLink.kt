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
import io.velocitycareerlabs.impl.extensions.getUrlQueryParamList
import io.velocitycareerlabs.impl.extensions.getUrlQueryParams
import java.net.URI

data class VCLDeepLink(val value: String) {
    val requestUri: String? = generateUri(uriKey = KeyRequestUri)
    val vendorOriginContext: String? = retrieveQueryParam(KeyVendorOriginContext)
    val did: String? = (retrieveQueryParam(KeyIssuerDid) ?: retrieveQueryParam(KeyInspectorDid))
        ?: requestUri?.getUrlSubPath(KeyDidPrefix) // fallback for old agents

    // Decoding the link merges the request URI's own query into the link's, so the query is read as a list:
    // a map would keep one of a request URI's repeated credential_types. distinct() drops a parameter that
    // both carry, such as issuerDid.
    private fun generateUri(uriKey: String, asSubParams: Boolean = false): String? {
        val queryParams = this.value.decode().getUrlQueryParamList() ?: return null
        val uri = queryParams.lastOrNull { (key, _) -> key == uriKey }?.second ?: return null
        val queryItems = queryParams
            .filter { (key, value) -> key != uriKey && value.isNotEmpty() }
            .distinct()
            .joinToString("&") { (key, value) -> "$key=${value.encode()}" }
        if (queryItems.isNotEmpty()) {
            return if (asSubParams) "$uri&$queryItems" else uri.appendQueryParams(queryItems)
        }
        return uri
    }

    private fun retrieveQueryParam(key: String): String? =
        this.value.decode().getUrlQueryParams()?.get(key)

    companion object CodingKeys {
        const val KeyDidPrefix = "did:"
        const val KeyRequestUri = "request_uri"
        const val KeyVendorOriginContext = "vendorOriginContext"
        const val KeyIssuerDid = "issuerDid"
        const val KeyInspectorDid = "inspectorDid"
    }
}