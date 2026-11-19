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
import io.velocitycareerlabs.api.entities.CredentialManifestDescriptorCodingKeys.KeyCredentialTypes
import io.velocitycareerlabs.impl.extensions.getUrlQueryParamValues
import io.velocitycareerlabs.impl.extensions.getUrlQueryParams
import java.net.URI

data class VCLDeepLink(val value: String) {
    val requestUri: String? = generateUri(uriKey = KeyRequestUri)
    val vendorOriginContext: String? = retrieveQueryParam(KeyVendorOriginContext)
    val did: String? = (retrieveQueryParam(KeyIssuerDid) ?: retrieveQueryParam(KeyInspectorDid))
        ?: requestUri?.getUrlSubPath(KeyDidPrefix) // fallback for old agents

    // The map keeps one value per key, so credential_types, which a link repeats once per type, comes with every
    // value from the request URI's own query instead. The request URI is taken from the link before the rest is
    // decoded, so text encoded in another value can't add a type. A request URI without credential_types of its
    // own is handled as before.
    private fun generateUri(uriKey: String, asSubParams: Boolean = false): String? {
        this.value.decode().getUrlQueryParams()?.let { queryParams ->
            queryParams[uriKey]?.let { uri ->
                val credentialTypes = rawQueryParam(uriKey)?.decode()
                    ?.getUrlQueryParamValues(KeyCredentialTypes)
                    .orEmpty()
                    .filter { it.isNotEmpty() }
                    .distinct()
                val queryItems = (queryParams
                    .filter { it.key != uriKey && it.value.isNotEmpty() }
                    .filter { credentialTypes.isEmpty() || it.key != KeyCredentialTypes }
                    .map { (key, value) -> "$key=${value.encode()}" } +
                        credentialTypes.map { "$KeyCredentialTypes=${it.encode()}" })
                    .joinToString("&")
                if (queryItems.isNotEmpty()) {
                    return if (asSubParams) "$uri&$queryItems" else uri.appendQueryParams(queryItems)
                }
                return uri
            }
        }
        return null
    }

    // A parameter of the link as it's written, before anything is decoded
    private fun rawQueryParam(key: String): String? =
        value.substringAfter('?', "").split('&')
            .lastOrNull { it.substringBefore('=') == key }
            ?.substringAfter('=', "")

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