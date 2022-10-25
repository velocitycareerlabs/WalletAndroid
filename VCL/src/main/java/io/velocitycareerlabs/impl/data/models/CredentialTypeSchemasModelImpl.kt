/**
 * Created by Michael Avoyan on 3/31/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.models

import io.velocitycareerlabs.api.entities.VCLCredentialTypeSchemas
import io.velocitycareerlabs.impl.domain.models.CredentialTypeSchemasModel
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.data
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.domain.usecases.CredentialTypeSchemasUseCase

internal class CredentialTypeSchemasModelImpl(
        private val credentialTypeSchemasUseCase: CredentialTypeSchemasUseCase
) : CredentialTypeSchemasModel {

    override var data: VCLCredentialTypeSchemas? = null

    override fun initialize(
        resetCache: Boolean,
        completionBlock: (VCLResult<VCLCredentialTypeSchemas>) -> Unit
    ) {
        credentialTypeSchemasUseCase.getCredentialTypeSchemas(resetCache) { result ->
            result.handleResult({ data = result.data }, { })
            completionBlock(result)
        }
    }
}