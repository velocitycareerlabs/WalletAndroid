/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.utils

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.impl.extensions.getUrlQueryParams
import java.net.URI

internal class VelocityDeepLinkValidator {
    fun validateDeepLink(
        deepLink: VCLDeepLink,
        expectedPath: String,
        expectedDidParam: String,
        requestKind: String,
    ): VCLError? {
        val uri = runCatching { URI(deepLink.value) }.getOrNull()
            ?: return ErrorTaxonomy.invalidLink(
                message = "Payload is not a parseable URL",
                requestUri = deepLink.requestUri,
                requestKind = requestKind,
            )
        if (uri.scheme !in AllowedVelocitySchemes || uri.host != expectedPath) {
            return ErrorTaxonomy.invalidLink(
                message = "Unsupported Velocity link: ${deepLink.value}",
                requestUri = deepLink.requestUri,
                requestKind = requestKind,
            )
        }
        val descriptorDid = expectedDid(deepLink, expectedDidParam)
        if (!isSyntacticallyValidDid(descriptorDid)) {
            return ErrorTaxonomy.invalidLink(
                message = "Invalid or missing DID in Velocity link",
                requestUri = deepLink.requestUri,
                requestKind = requestKind,
            )
        }
        if (!isAllowedRequestUri(deepLink.requestUri)) {
            return ErrorTaxonomy.invalidLink(
                message = "Invalid or missing request_uri in Velocity link",
                requestUri = deepLink.requestUri,
                requestKind = requestKind,
            )
        }
        return null
    }

    fun validateRequestEndpoint(
        requestUri: String?,
        requestKind: String,
    ): VCLError? {
        if (!isAllowedRequestUri(requestUri)) {
            return ErrorTaxonomy.invalidLink(
                message = "Invalid or missing request endpoint",
                requestUri = requestUri,
                requestKind = requestKind,
            )
        }
        return null
    }

    fun isAllowedRequestUri(requestUri: String?): Boolean {
        val uri = runCatching { URI(requestUri ?: return false) }.getOrNull() ?: return false
        return uri.scheme == "http" || uri.scheme == "https"
    }

    private fun expectedDid(deepLink: VCLDeepLink, expectedDidParam: String): String? =
        deepLink.value.queryParam(expectedDidParam)
            ?: deepLink.requestUri?.queryParam(expectedDidParam)
            ?: deepLink.requestUri?.didInPath()

    private fun String.queryParam(key: String): String? =
        runCatching { decode().getUrlQueryParams()?.get(key) }.getOrNull()

    private fun String.didInPath(): String? =
        runCatching {
            URI(this).path.split("/").find { it.startsWith(VCLDeepLink.KeyDidPrefix) }
        }.getOrNull()

    private fun isSyntacticallyValidDid(did: String?): Boolean =
        did?.matches(DidPattern) == true

    private companion object {
        val AllowedVelocitySchemes = setOf(
            "velocity-network",
            "velocity-network-devnet",
            "velocity-network-testnet",
        )
        val DidPattern = Regex("^did:[a-z0-9]+:[A-Za-z0-9._:%-]+(?:[:/][A-Za-z0-9._:%-]+)*$")
    }
}
