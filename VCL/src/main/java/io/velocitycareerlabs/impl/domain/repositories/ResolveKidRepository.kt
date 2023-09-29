/**
 * Created by Michael Avoyan on 4/20/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.VCLResult

internal interface ResolveKidRepository {
    fun getPublicKey(kid: String, completionBlock: (VCLResult<VCLPublicJwk>) -> Unit)
}