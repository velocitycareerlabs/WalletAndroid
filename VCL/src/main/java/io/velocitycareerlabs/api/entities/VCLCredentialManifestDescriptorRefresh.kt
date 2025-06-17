/**
 * Created by Michael Avoyan on 8/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.api.entities.CredentialManifestDescriptorCodingKeys.KeyCredentialId
import io.velocitycareerlabs.api.entities.CredentialManifestDescriptorCodingKeys.KeyRefresh
import io.velocitycareerlabs.impl.extensions.encode
import java.net.URI

data class VCLCredentialManifestDescriptorRefresh(
    val credentialIds: List<String>,
    private val service: VCLService, // for log
    override val issuingType: VCLIssuingType = VCLIssuingType.Refresh,
    override val didJwk: VCLDidJwk,
    override val remoteCryptoServicesToken: VCLToken? = null,
    override val uri: String = service.serviceEndpoint,
    override val credentialTypes: List<String>? = null,
    override val pushDelegate: VCLPushDelegate? = null,
    override val vendorOriginContext: String? = null,
    override val deepLink: VCLDeepLink? = null
) : VCLCredentialManifestDescriptor {

    override val endpoint = retrieveEndpoint()

    override fun retrieveEndpoint() =
        generateQueryParams()?.let { queryParams ->
            val originUri = URI(uri)
            val allQueryParams =
                (originUri.query?.let { "&" } ?: "?") + "${KeyRefresh}=${true}&$queryParams"
            uri + allQueryParams
        } ?: "$uri?${KeyRefresh}=${true}"

    override fun generateQueryParams(): String? {
        val pCredentialIds = credentialIds.map {
            it.encode()
        }.joinToString(separator = "&") { "$KeyCredentialId=$it" }
        val qParams = listOfNotNull(pCredentialIds).filter { it.isNotBlank() }
        return if(qParams.isNotEmpty()) qParams.joinToString("&") else null
    }
}
