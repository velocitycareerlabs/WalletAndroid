/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.keys

import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.initialization.VCLKeyServiceUrls
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService

internal class VCLKeyServiceRemoteImpl(
    private val networkService: NetworkService,
    private val keyServiceUrls: VCLKeyServiceUrls
) : VCLKeyService {
    override fun generateDidJwk(completionBlock: (VCLResult<VCLDidJwk>) -> Unit) {
        TODO("Not yet implemented")
    }
}