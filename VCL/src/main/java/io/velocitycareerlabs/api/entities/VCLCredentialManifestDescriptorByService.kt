/**
 * Created by Michael Avoyan on 8/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.appendQueryParams
import io.velocitycareerlabs.impl.extensions.encode
import java.lang.StringBuilder

data class VCLCredentialManifestDescriptorByService(
    private val service: VCLService,
    override var issuingType: VCLIssuingType = VCLIssuingType.Career,
    override var credentialTypes: List<String>? = null,
    override var pushDelegate: VCLPushDelegate? = null,
    override var didJwk: VCLDidJwk,
    override var remoteCryptoServicesToken: VCLToken? = null
) : VCLCredentialManifestDescriptor {

    override var uri: String? = service.serviceEndpoint
    override var vendorOriginContext: String? = null
    override var deepLink: VCLDeepLink? = null

    override val endpoint: String?
        get() = retrieveEndpoint()

    override fun toPropsString(): String {
        return buildString {
            appendLine(super.toPropsString())
            appendLine("service: ${service.toPropsString()}")
        }
    }
}
