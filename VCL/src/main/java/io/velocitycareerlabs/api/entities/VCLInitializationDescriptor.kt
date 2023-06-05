/**
 * Created by Michael Avoyan on 23/10/22.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.api.VCLEnvironment

data class VCLInitializationDescriptor(
    val environment: VCLEnvironment = VCLEnvironment.PROD,
    val cacheSequence: Int = 0
)
