/**
 * Created by Michael Avoyan on 09/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.appendQueryParams
import io.velocitycareerlabs.impl.extensions.encode
import io.velocitycareerlabs.impl.extensions.getUrlSubPath

interface VCLCredentialManifestDescriptor {
    val uri: String?
    val issuingType: VCLIssuingType
    val credentialTypes: List<String>?
    val pushDelegate: VCLPushDelegate?
    val vendorOriginContext: String?
    val deepLink: VCLDeepLink?
    val didJwk: VCLDidJwk
    val remoteCryptoServicesToken: VCLToken?

    val endpoint: String?

    val did get(): String? {
        return deepLink?.did ?: retrieveDid()
    }

    fun retrieveDid(): String? {
        return uri?.getUrlSubPath(CredentialManifestDescriptorCodingKeys.KeyDidPrefix)
    }

    fun retrieveEndpoint(): String? {
        return generateQueryParams()?.let { queryParams ->
            uri?.appendQueryParams(queryParams)
        } ?: uri
    }

    fun generateQueryParams(): String? {
        val pCredentialTypes: String? = credentialTypes?.joinToString("&") { type ->
            "${CredentialManifestDescriptorCodingKeys.KeyCredentialTypes}=${type.encode()}"
        }
        val pPushDelegate: String? = pushDelegate?.pushUrl?.let {
            "${CredentialManifestDescriptorCodingKeys.KeyPushDelegatePushUrl}=${it.encode()}"
        }
        val pPushToken: String? = pushDelegate?.pushToken?.let {
            "${CredentialManifestDescriptorCodingKeys.KeyPushDelegatePushToken}=${it.encode()}"
        }

        val qParams =
            listOfNotNull(pCredentialTypes, pPushDelegate, pPushToken).filter { it.isNotEmpty() }
        return if (qParams.isEmpty()) null else qParams.joinToString("&")
    }

//private fun generateQueryParams(): String? {
//    val pCredentialTypes = credentialTypes?.let { credTypes ->
//        credTypes.map { it.encode() }.joinToString(separator = "&") { "$KeyCredentialTypes=$it" } }
//    val pPushDelegate = pushDelegate?.let {
//        "$KeyPushDelegatePushUrl=${it.pushUrl.encode()}&" + "$KeyPushDelegatePushToken=${it.pushToken}"
//    }
//    val qParams = listOfNotNull(pCredentialTypes, pPushDelegate).filter { it.isNotBlank() }
//    return if(qParams.isNotEmpty()) qParams.joinToString("&") else null
//}

    fun toPropsString(): String {
        return buildString {
            appendLine("uri: ${uri.orEmpty()}")
            appendLine("did: ${did.orEmpty()}")
            appendLine("issuingType: $issuingType")
            appendLine("credentialTypes: ${credentialTypes?.joinToString()}")
            appendLine("pushDelegate: ${pushDelegate?.toPropsString().orEmpty()}")
            appendLine("vendorOriginContext: ${vendorOriginContext.orEmpty()}")
        }
    }
}

object CredentialManifestDescriptorCodingKeys {
    const val KeyDidPrefix = "did:"
    const val KeyCredentialTypes = "credential_types"
    const val KeyPushDelegatePushUrl = "push_delegate.push_url"
    const val KeyPushDelegatePushToken = "push_delegate.push_token"

    const val KeyCredentialId = "credentialId"
    const val KeyRefresh = "refresh"
}



