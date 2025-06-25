/**
 * Created by Michael Avoyan on 8/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

data class VCLCredentialManifestDescriptorByDeepLink(
    override val deepLink: VCLDeepLink,
    override val issuingType: VCLIssuingType = VCLIssuingType.Career,
    override val pushDelegate: VCLPushDelegate? = null,
    override val didJwk: VCLDidJwk,
    override val remoteCryptoServicesToken: VCLToken? = null
) : VCLCredentialManifestDescriptor {

    override val uri: String? = deepLink.requestUri
    override val credentialTypes: List<String>? = null
    override val vendorOriginContext: String? = deepLink.vendorOriginContext

    override val endpoint: String?
        get() = retrieveEndpoint()
}
