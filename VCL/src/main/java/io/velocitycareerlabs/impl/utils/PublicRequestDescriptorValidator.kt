/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.utils

import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptor
import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLPresentationRequestDescriptor
import io.velocitycareerlabs.api.entities.error.VCLError

internal class PublicRequestDescriptorValidator(
    private val config: Config,
    private val deepLinkValidator: VelocityDeepLinkValidator = VelocityDeepLinkValidator(),
) {
    fun validate(descriptor: VCLPresentationRequestDescriptor): VCLError? =
        validate(
            PublicRequestDescriptor(
                deepLink = descriptor.deepLink,
                endpoint = descriptor.endpoint,
                did = descriptor.did,
                description = descriptor.toString(),
            )
        )

    fun validate(descriptor: VCLCredentialManifestDescriptor): VCLError? =
        validate(
            PublicRequestDescriptor(
                deepLink = descriptor.deepLink,
                endpoint = descriptor.endpoint,
                did = descriptor.did,
                description = descriptor.toPropsString(),
            )
        )

    private fun validate(descriptor: PublicRequestDescriptor): VCLError? {
        val deepLink = descriptor.deepLink
        if (config.requireDeepLink || deepLink != null) {
            if (deepLink == null) {
                return ErrorTaxonomy.invalidLink(
                    message = "Payload is not a parseable URL",
                    sourceErrorCode = VelocityDeepLinkValidator.SourceUnparseablePayload,
                    requestKind = config.requestKind,
                )
            }
            deepLinkValidator.validateDeepLink(
                deepLink = deepLink,
                expectedPath = config.expectedPath,
                requestKind = config.requestKind,
            )?.let { return it }
        }
        deepLinkValidator.validateRequestEndpoint(
            requestUri = descriptor.endpoint,
            requestKind = config.requestKind,
        )?.let { return it }
        if (descriptor.did.isNullOrBlank()) {
            return ErrorTaxonomy.invalidLink(
                message = "did was not found in ${descriptor.description}",
                requestKind = config.requestKind,
            )
        }
        return null
    }

    data class Config(
        val requestKind: String,
        val expectedPath: String,
        val requireDeepLink: Boolean,
    )

    private data class PublicRequestDescriptor(
        val deepLink: VCLDeepLink?,
        val endpoint: String?,
        val did: String?,
        val description: String,
    )
}
