/**
 * Created by Michael Avoyan on 8/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.*
import io.velocitycareerlabs.impl.extensions.encode
import io.velocitycareerlabs.impl.extensions.getUrlQueryItems
import io.velocitycareerlabs.impl.extensions.getUrlQueryParams
import java.net.URI

data class VCLDeepLink(val value: String) {
    val requestUri: String? = generateUri(uriKey = KeyRequestUri)
    val vendorOriginContext: String? = retrieveQueryParam(KeyVendorOriginContext)
    val did: String? = (retrieveQueryParam(KeyIssuerDid) ?: retrieveQueryParam(KeyInspectorDid))
        ?: requestUri?.getUrlSubPath(KeyDidPrefix) // fallback for old agents

    // The link's query is read before anything is decoded, so the request URI's own query stays apart: a repeated
    // name such as credential_types keeps every value, and a value keeps any &, = or ? it encodes. The link's other
    // parameters are added to the request URI and replace any it carries under the same name. A malformed escape in
    // the request URI's own query is kept as it is, as before.
    private fun generateUri(uriKey: String): String? {
        val linkItems = value.getUrlQueryItems()
        val uri = linkItems.lastOrNull { (name, _) -> name == uriKey }?.second ?: return null
        val linkParams = linkItems.filter { (name, _) -> name != uriKey }
        val linkNames = linkParams.map { (name, _) -> name }.toSet()
        val params = (uri.getUrlQueryItems(lenient = true).filter { (name, _) -> name !in linkNames } + linkParams)
            .filter { (_, item) -> item.isNotEmpty() }
        if (params.isEmpty()) {
            return uri
        }
        return params.joinToString("&", prefix = "${uri.substringBefore('?')}?") { (name, item) ->
            "${name.encode()}=${item.encode()}"
        }
    }

    // The link's own parameter, else one inside a URL the link carries, such as request_uri or an OpenID issuer
    private fun retrieveQueryParam(key: String): String? {
        val linkItems = value.getUrlQueryItems()
        return linkItems.lastOrNull { (name, _) -> name == key }?.second
            ?: linkItems.asReversed().firstNotNullOfOrNull { (_, item) -> item.getUrlQueryParams()?.get(key) }
    }

    companion object CodingKeys {
        const val KeyDidPrefix = "did:"
        const val KeyRequestUri = "request_uri"
        const val KeyVendorOriginContext = "vendorOriginContext"
        const val KeyIssuerDid = "issuerDid"
        const val KeyInspectorDid = "inspectorDid"
    }
}