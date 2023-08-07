/**
 * Created by Michael Avoyan on 09/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.getUrlSubPath

open class VCLCredentialManifestDescriptor(
    val uri: String? = null,
    val issuingType: VCLIssuingType = VCLIssuingType.Career,
    val credentialTypes: List<String>? = null,
    val pushDelegate: VCLPushDelegate? = null,
    val vendorOriginContext: String? = null
) {
    val did = uri?.getUrlSubPath(KeyDidPrefix)

    open fun toPropsString() =
        StringBuilder()
            .append("\nuri: , $uri")
            .append("\ndid: , $did")
            .append("\nissuingType: , $issuingType")
            .append("\ncredentialTypes: , $credentialTypes")
            .append("\npushDelegate: , ${pushDelegate?.toPropsString()}")
            .append("\nvendorOriginContext: , $vendorOriginContext")
            .toString()

    open val endpoint: String? get() =  uri

    companion object CodingKeys {
        const val KeyId = "id"
        const val KeyDidPrefix = "did:"
        const val KeyCredentialTypes = "credential_types"
        const val KeyPushDelegatePushUrl = "push_delegate.push_url"
        const val KeyPushDelegatePushToken = "push_delegate.push_token"

        const val KeyCredentialId = "credentialId"
        const val KeyRefresh = "refresh"
    }
}