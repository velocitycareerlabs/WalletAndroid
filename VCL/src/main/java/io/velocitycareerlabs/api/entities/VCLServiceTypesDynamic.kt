/**
 * Created by Michael Avoyan on 25/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

data class VCLServiceTypesDynamic(val all: List<VCLServiceTypeDynamic>) {
    companion object CodingKeys {
        const val KeyServiceTypes = "serviceTypes"
    }
}
