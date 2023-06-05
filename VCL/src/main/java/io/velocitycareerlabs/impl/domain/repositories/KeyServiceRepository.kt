/**
 * Created by Michael Avoyan on 23/05/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLResult

internal interface KeyServiceRepository {
    fun generateDidJwk(
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    )
}