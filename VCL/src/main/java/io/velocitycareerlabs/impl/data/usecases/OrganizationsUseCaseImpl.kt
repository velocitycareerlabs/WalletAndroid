/**
 * Created by Michael Avoyan on 4/20/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
import io.velocitycareerlabs.api.entities.VCLOrganizations
import io.velocitycareerlabs.api.entities.VCLOrganizationsSearchDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.OrganizationsRepository
import io.velocitycareerlabs.impl.domain.usecases.OrganizationsUseCase

internal class OrganizationsUseCaseImpl(
    private val organizationsRepository: OrganizationsRepository,
    private val executor: Executor
): OrganizationsUseCase {

    override fun searchForOrganizations(organizationsSearchDescriptor: VCLOrganizationsSearchDescriptor,
                                        completionBlock: (VCLResult<VCLOrganizations>) -> Unit) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread(){
            organizationsRepository.searchForOrganizations(organizationsSearchDescriptor) {
                executor.runOn(callingLooper) { completionBlock(it) }
            }
        }
    }

}