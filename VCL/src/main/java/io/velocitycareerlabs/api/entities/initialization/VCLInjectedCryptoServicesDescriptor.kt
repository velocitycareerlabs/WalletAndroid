/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities.initialization

import io.velocitycareerlabs.api.jwt.VCLJwtSignService
import io.velocitycareerlabs.api.jwt.VCLJwtVerifyService
import io.velocitycareerlabs.api.keys.VCLKeyService

data class VCLInjectedCryptoServicesDescriptor(
    val keyService: VCLKeyService,
    val jwtSignService: VCLJwtSignService,
    val jwtVerifyService: VCLJwtVerifyService? = null
)