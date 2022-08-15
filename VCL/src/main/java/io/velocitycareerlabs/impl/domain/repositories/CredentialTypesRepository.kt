package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLCredentialTypes
import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 3/13/21.
 */
internal interface CredentialTypesRepository {
    fun getCredentialTypes(completionBlock: (VCLResult<VCLCredentialTypes>) -> Unit)
}