/**
 * Created by Michael Avoyan on 25/10/2023.
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLServiceTypesDynamic
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.ServiceTypesRepository
import io.velocitycareerlabs.impl.domain.usecases.ServiceTypesUseCase

internal class ServiceTypesUseCaseImpl(
    private val serviceTypesRepository: ServiceTypesRepository,
    private val executor: Executor
): ServiceTypesUseCase {
    override fun getServiceTypes(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLServiceTypesDynamic>) -> Unit
    ) {
        executor.runOnBackground {
            serviceTypesRepository.getServiceTypes(cacheSequence) {
                executor.runOnMain {
                    completionBlock(it)
                }
            }
        }
    }
}