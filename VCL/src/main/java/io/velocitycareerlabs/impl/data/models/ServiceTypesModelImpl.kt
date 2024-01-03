/**
 * Created by Michael Avoyan on 25/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.models

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLServiceTypesDynamic
import io.velocitycareerlabs.api.entities.data
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.domain.models.ServiceTypesModel
import io.velocitycareerlabs.impl.domain.usecases.ServiceTypesUseCase

internal class ServiceTypesModelImpl(
    private val serviceTypesUseCase: ServiceTypesUseCase
): ServiceTypesModel {

    override var data: VCLServiceTypesDynamic? = null

    override fun initialize(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLServiceTypesDynamic>) -> Unit
    ) {
        serviceTypesUseCase.getServiceTypes(cacheSequence) { result ->
            result.handleResult({ data = result.data }, { })
            completionBlock(result)
        }
    }
}