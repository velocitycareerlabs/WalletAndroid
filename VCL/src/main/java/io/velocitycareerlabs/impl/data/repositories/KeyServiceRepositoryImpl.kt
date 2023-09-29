/**
 * Created by Michael Avoyan on 23/05/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.domain.repositories.KeyServiceRepository

internal class KeyServiceRepositoryImpl(
    private val keyService: VCLKeyService
): KeyServiceRepository {
    override fun generateDidJwk(completionBlock: (VCLResult<VCLDidJwk>) -> Unit) {
        keyService.generateDidJwk() {
            completionBlock(it)
        }
    }
}