/**
 * Created by Michael Avoyan on 13/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.VCLCountries
import io.velocitycareerlabs.api.entities.VCLCredentialTypesUIFormSchema
import io.velocitycareerlabs.api.entities.VCLCredentialTypesUIFormSchemaDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.CredentialTypesUIFormSchemaRepository
import io.velocitycareerlabs.impl.domain.usecases.CredentialTypesUIFormSchemaUseCase

internal class CredentialTypesUIFormSchemaUseCaseImpl(
    private val credentialTypesUIFormSchemaRepository: CredentialTypesUIFormSchemaRepository,
    private val executor: Executor
): CredentialTypesUIFormSchemaUseCase {
    override fun getCredentialTypesUIFormSchema(
        credentialTypesUIFormSchemaDescriptor: VCLCredentialTypesUIFormSchemaDescriptor,
        countries: VCLCountries,
        completionBlock: (VCLResult<VCLCredentialTypesUIFormSchema>) -> Unit
    ) {
        executor.runOnBackground {
            credentialTypesUIFormSchemaRepository.getCredentialTypesUIFormSchema(
                credentialTypesUIFormSchemaDescriptor,
                countries
            ) {
                executor.runOnMain {
                    completionBlock(it)
                }
            }
        }
    }
}