package io.velocitycareerlabs.impl.data.models

import io.velocitycareerlabs.impl.domain.usecases.CredentialTypesUseCase
import io.velocitycareerlabs.impl.domain.models.CredentialTypesModel
import io.velocitycareerlabs.api.entities.VCLCredentialTypes
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.data
import io.velocitycareerlabs.api.entities.handleResult

/**
 * Created by Michael Avoyan on 3/11/21.
 */
internal class CredentialTypesModelImpl(
        private val credentialTypesUseCase: CredentialTypesUseCase
): CredentialTypesModel {

    override var data: VCLCredentialTypes? = null

    override fun initialize(completionBlock: (VCLResult<VCLCredentialTypes>) -> Unit) {
        credentialTypesUseCase.getCredentialTypes { result ->
            result.handleResult({ data = result.data }, { })
            completionBlock(result)
        }
    }
}