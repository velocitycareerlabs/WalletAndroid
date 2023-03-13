/**
 * Created by Michael Avoyan on 3/31/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.CredentialTypeSchemaRepository
import io.velocitycareerlabs.impl.domain.usecases.CredentialTypeSchemasUseCase
import io.velocitycareerlabs.impl.utils.VCLLog

internal class CredentialTypeSchemasUseCaseImpl (
    private val credentialTypeSchemaRepository: CredentialTypeSchemaRepository,
    private val credentialTypes: VCLCredentialTypes,
    private val executor: Executor
): CredentialTypeSchemasUseCase {

    private val TAG = CredentialTypeSchemasUseCaseImpl::class.simpleName

    override fun getCredentialTypeSchemas(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLCredentialTypeSchemas>) -> Unit
    ) {
        val credentialTypeSchemasMap = HashMap<String, VCLCredentialTypeSchema>()
        var credentialTypeSchemasMapIsEmpty = true

        val schemaNamesArr =
            this.credentialTypes.all?.filter { it.schemaName != null }?.map { it.schemaName }
                ?: listOf()

        schemaNamesArr.forEach { schemaName ->
            schemaName?.let {
                executor.runOnBackgroundThread {
                    credentialTypeSchemaRepository.getCredentialTypeSchema(
                        schemaName,
                        cacheSequence
                    ) { result ->
                        result.data?.let { credentialTypeSchemasMap[schemaName] = it }
                        credentialTypeSchemasMapIsEmpty =
                            credentialTypeSchemasMap.isEmpty() // like in Swift
                    }
                }
            }
        }
        executor.waitForTermination()

        if (credentialTypeSchemasMapIsEmpty) {
            VCLLog.e(TAG, "Credential type schemas were not fount.")
        }
        completionBlock(VCLResult.Success(VCLCredentialTypeSchemas(credentialTypeSchemasMap)))
    }
}