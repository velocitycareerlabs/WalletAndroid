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
                VCLEnvironment.DEV -> VCLEnvironment.DEV.value
                VCLEnvironment.STAGING -> VCLEnvironment.STAGING.value
                else -> "" // prod is a default, doesn't has a prefix
            }

        private val BaseUrlServices get() = "https://${EnvironmentPrefix}registrar.velocitynetwork.foundation"
        val CredentialTypes get() = "$BaseUrlServices/api/v0.6/credential-types"
        val CredentialTypeSchemas get() = "$BaseUrlServices/schemas/"
        val Countries get() = "$BaseUrlServices/reference/countries"
        val Organizations get() = "$BaseUrlServices/api/v0.6/organizations/search-profiles"
        val ResolveKid get() = "$BaseUrlServices/api/v0.6/resolve-kid/"
        val CredentialTypesFormSchema get() = "$BaseUrlServices/api/v0.6/form-schemas?credentialType=$CredentialType"
        val VerifiedProfile get() = "$BaseUrlServices/api/v0.6/organizations/$Did/verified-profile"

//        private const val BaseUrlAgent get() = "https://${EnvironmentPrefix}agent.velocitycareerlabs.io"
//        val PresentationSubmission get() = "$BaseUrlAgent/api/holder/v0.6/org/$Did/inspect/submit-presentation"
//        val ExchangeProgress get() = "$BaseUrlAgent/api/holder/v0.6/org/$Did/get-exchange-progress"
//        val CredentialManifest get() = "$BaseUrlAgent/api/holder/v0.6/org/$Did/issue/get-credential-manifest"
//        val IdentificationSubmission get() = "$BaseUrlAgent/api/holder/v0.6/org/$Did/issue/submit-identification"
//        val GenerateOffers get() = "$BaseUrlAgent/api/holder/v0.6/org/$Did/issue/credential-offers"
//        val FinalizeOffers get() = "$BaseUrlAgent/api/holder/v0.6/org/$Did/issue/finalize-offers"
    }
}

class Params {
    companion object {
        const val Did = "{did}"
        const val CredentialType = "{credentialType}"
    }
}

object HeaderKeys {
    const val HeaderKeyAuthorization = "Authorization"
    const val HeaderValuePrefixBearer = "Bearer"
    const val XVnfProtocolVersion = "x-vnf-protocol-version"
}

object HeaderValues {
    const val XVnfProtocolVersion = "1.0"
}