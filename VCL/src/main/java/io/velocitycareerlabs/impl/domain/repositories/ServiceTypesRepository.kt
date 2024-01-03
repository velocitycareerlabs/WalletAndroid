/**
 * Created by Michael Avoyan on 25/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLServiceTypesDynamic

internal interface ServiceTypesRepository {
    fun getServiceTypes(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLServiceTypesDynamic>) -> Unit
    )
}