package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLCredentialTypeSchema
import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 3/30/21.
 */
internal interface CredentialTypeSchemaRepository {
    fun getCredentialTypeSchema(schemaName: String, completionBlock: (VCLResult<VCLCredentialTypeSchema>) -> Unit)
}