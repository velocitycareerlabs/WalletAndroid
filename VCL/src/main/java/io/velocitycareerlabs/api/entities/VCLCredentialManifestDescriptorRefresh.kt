package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.encode
import java.net.URI

data class VCLCredentialManifestDescriptorRefresh(
    val service: VCLService,
    val credentialIds: List<String>
): VCLCredentialManifestDescriptor(
    uri = service.serviceEndpoint
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
