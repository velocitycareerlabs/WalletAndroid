package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLCountries
import io.velocitycareerlabs.api.entities.VCLCredentialTypesUIFormSchema
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLCredentialTypesUIFormSchemaDescriptor

/**
 * Created by Michael Avoyan on 13/06/2021.
 */
internal interface CredentialTypesUIFormSchemaUseCase {
    fun getCredentialTypesUIFormSchema(
        credentialTypesUIFormSchemaDescriptor: VCLCredentialTypesUIFormSchemaDescriptor,
        countries: VCLCountries,
        completionBlock: (VCLResult<VCLCredentialTypesUIFormSchema>) -> Unit
    )
}