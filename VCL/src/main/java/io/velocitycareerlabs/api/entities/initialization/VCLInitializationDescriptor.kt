/**
 * Created by Michael Avoyan on 23/10/22.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities.initialization

import io.velocitycareerlabs.api.VCLEnvironment
import io.velocitycareerlabs.api.VCLXVnfProtocolVersion

data class VCLInitializationDescriptor(
    val environment: VCLEnvironment = VCLEnvironment.Prod,
    val xVnfProtocolVersion: VCLXVnfProtocolVersion = VCLXVnfProtocolVersion.XVnfProtocolVersion1,
    val cacheSequence: Int = 0,
    val isDebugOn: Boolean = false,
    val keyServicesDescriptor: VCLCryptoServicesDescriptor = VCLCryptoServicesDescriptor()
)
