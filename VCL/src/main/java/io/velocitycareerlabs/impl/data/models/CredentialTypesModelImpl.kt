/**
 * Created by Michael Avoyan on 3/11/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.models

import io.velocitycareerlabs.impl.domain.usecases.CredentialTypesUseCase
import io.velocitycareerlabs.impl.domain.models.CredentialTypesModel
import io.velocitycareerlabs.api.entities.VCLCredentialTypes
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.data
import io.velocitycareerlabs.api.entities.handleResult

internal class CredentialTypesModelImpl(
        private val credentialTypesUseCase: CredentialTypesUseCase
): CredentialTypesModel {

    override var data: VCLCredentialTypes? = null

    override fun initialize(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLCredentialTypes>) -> Unit
    ) {
        credentialTypesUseCase.getCredentialTypes(cacheSequence) { result ->
            result.handleResult({ data = result.data }, { })
            completionBlock(result)
        }
    }
}