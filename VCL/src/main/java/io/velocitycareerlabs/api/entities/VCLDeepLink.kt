/**
 * Created by Michael Avoyan on 8/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.*
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.impl.extensions.decodeOrSelf
import io.velocitycareerlabs.impl.extensions.encodeIllegalQueryCharacters
import io.velocitycareerlabs.impl.extensions.getUrlQueryItems
import io.velocitycareerlabs.impl.extensions.getUrlQueryParams
import io.velocitycareerlabs.impl.extensions.getUrlQuerySegments
import java.net.URI

data class VCLDeepLink(val value: String) {
    val requestUri: String? = generateUri(uriKey = KeyRequestUri)
    val vendorOriginContext: String? = retrieveQueryParam(KeyVendorOriginContext)
    val did: String? = (retrieveQueryParam(KeyIssuerDid) ?: retrieveQueryParam(KeyInspectorDid))
        ?: requestUri?.getUrlSubPath(KeyDidPrefix) // fallback for old agents

    // The link's query is split before anything is decoded, so the request URI's own query stays apart. The request
    // URI keeps its query items as the issuer wrote them and gets the link's other parameters as the link wrote them,
    // so a repeated name such as credential_types keeps every value and no value is decoded and encoded again. A link
    // parameter replaces any the request URI carries under the same name. Characters a URL can't hold are escaped.
    private fun generateUri(uriKey: String): String? {
        val linkSegments = value.getUrlQuerySegments()
        val uri = linkSegments.lastOrNull { it.segmentName() == uriKey }?.segmentValue()?.decode() ?: return null
        val linkParams = linkSegments.filter { it.segmentName() != uriKey }
        val linkNames = linkParams.map { it.segmentName() }.toSet()
        val params = (uri.getUrlQuerySegments().filter { it.segmentName() !in linkNames } + linkParams)
            .filter { it.segmentValue().isNotEmpty() }
            .map { it.encodeIllegalQueryCharacters() }
        val base = uri.substringBefore('?')
        return if (params.isEmpty()) base else "$base?${params.joinToString("&")}"
    }

    private fun String.segmentName() = substringBefore('=').decodeOrSelf()

    private fun String.segmentValue() = substringAfter('=', "")

    // The link's own parameter, else one inside a URL the link carries, such as request_uri or an OpenID issuer,
    // decoded once as the server decodes it. Other values, such as vendorOriginContext, aren't read as queries.
    private fun retrieveQueryParam(key: String): String? {
        val linkItems = value.getUrlQueryItems()
        return linkItems.lastOrNull { (name, _) -> name == key }?.second
            ?: linkItems.asReversed()
                .filter { (_, item) -> item.contains("://") }
                .firstNotNullOfOrNull { (_, item) -> item.getUrlQueryParams()?.get(key)?.decodeOrSelf() }
    }

    companion object CodingKeys {
        const val KeyDidPrefix = "did:"
        const val KeyRequestUri = "request_uri"
        const val KeyVendorOriginContext = "vendorOriginContext"
        const val KeyIssuerDid = "issuerDid"
        const val KeyInspectorDid = "inspectorDid"
    }
}