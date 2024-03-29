/**
 * Created by Michael Avoyan on 8/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.encode
import java.net.URI

class VCLCredentialManifestDescriptorRefresh(
    service: VCLService,
    val credentialIds: List<String>,
    didJwk: VCLDidJwk,
    remoteCryptoServicesToken: VCLToken? = null
): VCLCredentialManifestDescriptor(
    uri = service.serviceEndpoint,
    issuingType = VCLIssuingType.Refresh,
    didJwk = didJwk,
    remoteCryptoServicesToken = remoteCryptoServicesToken
) {
    override val endpoint =  generateQueryParams()?.let { queryParams ->
        val originUri = URI(uri)
        val allQueryParams =
            (originUri.query?.let { "&" } ?: "?") + "${KeyRefresh}=${true}&$queryParams"
        uri + allQueryParams
    } ?: "$uri?${KeyRefresh}=${true}"

    private fun generateQueryParams(): String? {
        val pCredentialIds = credentialIds.map {
            it.encode()
        }.joinToString(separator = "&") { "$KeyCredentialId=$it" }
        val qParams = listOfNotNull(pCredentialIds).filter { it.isNotBlank() }
        return if(qParams.isNotEmpty()) qParams.joinToString("&") else null
    }
}
