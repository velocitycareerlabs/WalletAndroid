package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLCredentialTypes
import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 3/11/21.
 */
internal interface CredentialTypesUseCase {
    fun getCredentialTypes(completionBlock:(VCLResult<VCLCredentialTypes>) -> Unit)
}