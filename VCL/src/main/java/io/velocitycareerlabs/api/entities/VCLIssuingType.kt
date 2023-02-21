/**
 * Created by Michael Avoyan on 06/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

enum class VCLIssuingType(val value: String) {
    Career("Career"),
    Identity("Identity"),
    Refresh("Refresh"),
    Undefined("Undefined");

    companion object {
        fun fromString(value: String): VCLIssuingType {
            if(value.contains(VCLIssuingType.Career.value)) {
                return VCLIssuingType.Career
            }
            if(value.contains(VCLIssuingType.Identity.value)) {
                return VCLIssuingType.Identity
            }
            if(value.contains(VCLIssuingType.Refresh.value)) {
                return VCLIssuingType.Refresh
            }
            return VCLIssuingType.Undefined
        }
    }
}