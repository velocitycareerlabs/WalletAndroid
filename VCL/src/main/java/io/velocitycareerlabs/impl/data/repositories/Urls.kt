/**
 * Created by Michael Avoyan on 3/13/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.VCLEnvironment
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.data.repositories.Params.Companion.CredentialType
import io.velocitycareerlabs.impl.data.repositories.Params.Companion.Did

internal class Urls {
    companion object {
        private val EnvironmentPrefix get(): String =
            when(GlobalConfig.CurrentEnvironment) {
                VCLEnvironment.Dev -> VCLEnvironment.Dev.value
                VCLEnvironment.Qa -> VCLEnvironment.Qa.value
                VCLEnvironment.Staging -> VCLEnvironment.Staging.value
                else -> "" // prod is a default, doesn't has a prefix
            }
        private val BaseUrlRegistrar get() = "https://${EnvironmentPrefix}registrar.velocitynetwork.foundation"
//        private val BaseUrlWalletApi get() = "https://${EnvironmentPrefix}walletapi.velocitycareerlabs.io"
        val CredentialTypes get() = "$BaseUrlRegistrar/api/v0.6/credential-types"
        val CredentialTypeSchemas get() = "$BaseUrlRegistrar/schemas/"
        val Countries get() = "$BaseUrlRegistrar/reference/countries"
        val Organizations get() = "$BaseUrlRegistrar/api/v0.6/organizations/search-profiles"
        val ResolveKid get() = "$BaseUrlRegistrar/api/v0.6/resolve-kid/"
        val CredentialTypesFormSchema get() = "$BaseUrlRegistrar/api/v0.6/form-schemas?credentialType=$CredentialType"
        val VerifiedProfile get() = "$BaseUrlRegistrar/api/v0.6/organizations/$Did/verified-profile"
    }
}

class Params {
    companion object {
        const val Did = "{did}"
        const val CredentialType = "{credentialType}"
    }
}

object HeaderKeys {
    const val Authorization = "Authorization"
    const val Bearer = "Bearer"
    const val XVnfProtocolVersion = "x-vnf-protocol-version"
}

object HeaderValues {
    val XVnfProtocolVersion get() = GlobalConfig.XVnfProtocolVersion.value
}