/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities.initialization

import io.velocitycareerlabs.api.jwt.VCLJwtService
import io.velocitycareerlabs.api.keys.VCLKeyService

data class VCLInjectedServicesDescriptor(
    val keyService: VCLKeyService,
    val jwtService: VCLJwtService
)