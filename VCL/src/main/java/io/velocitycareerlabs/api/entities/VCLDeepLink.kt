/**
 * Created by Michael Avoyan on 8/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.appendQueryParams
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.impl.extensions.encode
import io.velocitycareerlabs.impl.extensions.getUrlQueryParams

data class VCLDeepLink(val value: String) {
    val requestUri: String = generateRequestUri()
    val vendorOriginContext: String? = retrieveVendorOriginContext()

    private fun generateRequestUri(): String {
        var resRequestUri = ""
        this.value.getUrlQueryParams()?.let { queryParams ->
            resRequestUri = queryParams[KeyRequestUri]?.decode() ?: ""
            val queryItems = queryParams
                .mapValues { it.value }
                .filter { it.key != KeyRequestUri && it.value.isNotEmpty() }
                .map { (key, value) -> "$key=${value}" }
                .sortedBy { it } // Sort is needed for unit tests
                .joinToString("&")
            if (queryItems.isNotEmpty()) {
                resRequestUri = resRequestUri.appendQueryParams(queryItems)
            }
        }
        return resRequestUri
    }

    private fun retrieveVendorOriginContext(): String? =
        this.value.decode().getUrlQueryParams()?.get(KeyVendorOriginContext)

    companion object CodingKeys {
        const val KeyRequestUri = "request_uri"
        const val KeyVendorOriginContext = "vendorOriginContext"
    }
}