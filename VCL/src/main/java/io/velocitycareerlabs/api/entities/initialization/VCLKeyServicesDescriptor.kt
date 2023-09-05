/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities.initialization

import io.velocitycareerlabs.api.VCLKeyServiceType

class VCLKeyServicesDescriptor(
    val keyServiceType: VCLKeyServiceType = VCLKeyServiceType.Local,
    val injectedServicesDescriptor: VCLInjectedServicesDescriptor? = null,
    val remoteServicesUrlsDescriptor: VCLRemoteServicesUrlsDescriptor? = null
)