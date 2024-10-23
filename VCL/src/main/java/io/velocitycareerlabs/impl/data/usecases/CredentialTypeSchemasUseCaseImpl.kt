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
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

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
        executor.runOnBackground {
            val credentialTypeSchemasMap = ConcurrentHashMap<String, VCLCredentialTypeSchema>()

            val schemaNamesArr =
                credentialTypes.all?.filter { it.schemaName != null }?.map { it.schemaName }
                    ?: listOf()

            val completableFutures = schemaNamesArr.mapNotNull { schemaName ->
                schemaName?.let {
                    CompletableFuture.supplyAsync {
                        credentialTypeSchemaRepository.getCredentialTypeSchema(
                            schemaName,
                            cacheSequence
                        ) { result ->
                            result.data?.let { credentialTypeSchemasMap[schemaName] = it }
                        }
                    }
                }
            }

            val allFutures = CompletableFuture.allOf(*completableFutures.toTypedArray())
            allFutures.join()

            if (credentialTypeSchemasMap.isEmpty()) {
                VCLLog.e(TAG, "Credential type schemas were not fount.")
            } else {
                executor.runOnMain {
                    completionBlock(VCLResult.Success(
                        VCLCredentialTypeSchemas(credentialTypeSchemasMap)
                    ))
                }
            }
        }
    }
}
