package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLCredentialTypeSchemas
import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 3/31/21.
 */
internal interface CredentialTypeSchemasUseCase {
    fun getCredentialTypeSchemas(completionBlock:(VCLResult<VCLCredentialTypeSchemas>) -> Unit)
}