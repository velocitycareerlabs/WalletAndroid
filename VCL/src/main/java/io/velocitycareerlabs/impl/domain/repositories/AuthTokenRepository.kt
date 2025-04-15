/**
 * Created by Michael Avoyan on 09/04/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLAuthToken
import io.velocitycareerlabs.api.entities.VCLAuthTokenDescriptor
import io.velocitycareerlabs.api.entities.VCLResult

interface AuthTokenRepository {
    fun getAuthToken(
        authTokenDescriptor: VCLAuthTokenDescriptor,
        completionBlock: (VCLResult<VCLAuthToken>) -> Unit
    )
}