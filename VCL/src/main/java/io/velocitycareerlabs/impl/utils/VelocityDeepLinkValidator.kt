/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.utils

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.error.VCLError
import java.net.URI

internal class VelocityDeepLinkValidator {
    fun validateDeepLink(
        deepLink: VCLDeepLink,
        expectedPath: String,
        requestKind: String,
    ): VCLError? {
        val uri = parseableVelocityPayload(deepLink)
        if (uri == null) {
            return ErrorTaxonomy.invalidLink(
                message = "Payload is not a parseable URL",
                sourceErrorCode = SourceUnparseablePayload,
                requestUri = deepLink.requestUri,
                requestKind = requestKind,
            )
        }
        if (isUnsupportedVelocityLink(uri, expectedPath)) {
            return ErrorTaxonomy.invalidLink(
                message = "Unsupported Velocity link: ${deepLink.value}",
                sourceErrorCode = SourceUnsupportedVelocityLink,
                requestUri = deepLink.requestUri,
                requestKind = requestKind,
            )
        }
        val descriptorDid = deepLink.did
        if (!isSyntacticallyValidDid(descriptorDid)) {
            return ErrorTaxonomy.invalidLink(
                message = "Invalid or missing DID in Velocity link",
                sourceErrorCode = SourceInvalidOrMissingDid,
                requestUri = deepLink.requestUri,
                requestKind = requestKind,
            )
        }
        if (!isAllowedRequestUri(deepLink.requestUri)) {
            return ErrorTaxonomy.invalidLink(
                message = "Invalid or missing request_uri in Velocity link",
                sourceErrorCode = SourceInvalidOrMissingRequestUri,
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
                sourceErrorCode = SourceInvalidOrMissingRequestEndpoint,
                requestUri = requestUri,
                requestKind = requestKind,
            )
        }
        return null
    }

    private fun isAllowedRequestUri(requestUri: String?): Boolean {
        val uri = runCatching { URI(requestUri ?: return false) }.getOrNull() ?: return false
        return uri.scheme == "http" || uri.scheme == "https"
    }

    private fun parseableVelocityPayload(deepLink: VCLDeepLink): URI? =
        runCatching { URI(deepLink.value) }
            .getOrNull()
            ?.takeIf { !it.isOpaque && it.host != null }

    private fun isUnsupportedVelocityLink(uri: URI, expectedPath: String): Boolean =
        uri.scheme !in AllowedVelocitySchemes || uri.host != expectedPath

    private fun isSyntacticallyValidDid(did: String?): Boolean =
        did?.startsWith(VCLDeepLink.KeyDidPrefix) == true && hasValidDidParts(did)

    private fun hasValidDidParts(did: String): Boolean {
        val didParts = did.removePrefix(VCLDeepLink.KeyDidPrefix)
        val methodEndIndex = didParts.indexOf(':')
        if (methodEndIndex <= 0 || methodEndIndex == didParts.length - 1) {
            return false
        }
        val method = didParts.substring(0, methodEndIndex)
        return method.all { it in 'a'..'z' || it in '0'..'9' }
    }

    companion object {
        val AllowedVelocitySchemes = setOf(
            "velocity-network",
            "velocity-network-devnet",
            "velocity-network-testnet",
        )
        const val SourceUnparseablePayload = "invalid_link_unparseable_payload"
        const val SourceUnsupportedVelocityLink = "invalid_link_unsupported_velocity_link"
        const val SourceInvalidOrMissingDid = "invalid_link_invalid_or_missing_did"
        const val SourceInvalidOrMissingRequestUri = "invalid_link_invalid_or_missing_request_uri"
        const val SourceInvalidOrMissingRequestEndpoint = "invalid_link_invalid_or_missing_request_endpoint"
    }
}
