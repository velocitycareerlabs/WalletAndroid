/**
 * Created by Michael Avoyan on 11/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources

import io.velocitycareerlabs.api.entities.VCLCredentialType
import io.velocitycareerlabs.api.entities.VCLCredentialTypes
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.models.CredentialTypesModel
import io.velocitycareerlabs.impl.extensions.toJsonObject

internal class CredentialTypesModelMock(
    private val issuerCategory: String
): CredentialTypesModel {

    override fun credentialTypeByTypeName(type: String): VCLCredentialType {
        return VCLCredentialType(
                payload = "{}".toJsonObject()!!,
                issuerCategory = issuerCategory,
            credentialType = ""
            )
        }

    override val data: VCLCredentialTypes
        get() = VCLCredentialTypes(
            listOf(VCLCredentialType(payload = "{}".toJsonObject()!!, issuerCategory = issuerCategory))
        )

    override fun initialize(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLCredentialTypes>) -> Unit
    ) {
    }

    companion object {
        val issuerCategoryIdentityIssuer = "IdentityIssuer"
        val IssuerCategoryNotaryIssuer = "NotaryIssuer"
        val issuerCategoryRegularIssuer = "RegularIssuer"
    }
}