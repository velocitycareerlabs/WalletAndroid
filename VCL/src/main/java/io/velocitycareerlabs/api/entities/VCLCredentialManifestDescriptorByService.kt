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

class VCLCredentialManifestDescriptorByService(
    val service: VCLService, // for log
    issuingType: VCLIssuingType = VCLIssuingType.Career,
    credentialTypes: List<String>? = null,
    pushDelegate: VCLPushDelegate? = null,
    didJwk: VCLDidJwk? = null,
    remoteCryptoServicesToken: VCLToken? = null
): VCLCredentialManifestDescriptor(
    uri = service.serviceEndpoint,
    issuingType = issuingType,
    credentialTypes = credentialTypes,
    pushDelegate = pushDelegate,
    didJwk = didJwk,
    remoteCryptoServicesToken = remoteCryptoServicesToken
) {
    override fun toPropsString() =
        StringBuilder(super.toPropsString())
            .append("\nservice: ${service.toPropsString()}")
            .toString()
}