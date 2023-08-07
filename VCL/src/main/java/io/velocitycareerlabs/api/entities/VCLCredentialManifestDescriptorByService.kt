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
    pushDelegate: VCLPushDelegate? = null
): VCLCredentialManifestDescriptor(
    uri = service.serviceEndpoint,
    issuingType = issuingType,
    credentialTypes = credentialTypes,
    pushDelegate = pushDelegate
) {
    override fun toPropsString() =
        StringBuilder(super.toPropsString())
            .append("\nservice: ${service.toPropsString()}")
            .toString()

    override val endpoint =  generateQueryParams()?.let { queryParams ->
        uri?.appendQueryParams(queryParams)
    } ?: uri

    private fun generateQueryParams(): String? {
        val pCredentialTypes = credentialTypes?.let { credTypes ->
            credTypes.map { it.encode() }.joinToString(separator = "&") { "$KeyCredentialTypes=$it" } }
        val pPushDelegate = pushDelegate?.let {
            "$KeyPushDelegatePushUrl=${it.pushUrl.encode()}&" + "$KeyPushDelegatePushToken=${it.pushToken}"
        }
        val qParams = listOfNotNull(pCredentialTypes, pPushDelegate).filter { it.isNotBlank() }
        return if(qParams.isNotEmpty()) qParams.joinToString("&") else null
    }
}