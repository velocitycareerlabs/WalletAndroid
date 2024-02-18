/**
 * Created by Michael Avoyan on 25/05/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLDidJwkDescriptor
import io.velocitycareerlabs.api.entities.VCLResult

internal interface KeyServiceUseCase {
    fun generateDidJwk(
        didJwkDescriptor: VCLDidJwkDescriptor,
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    )
}