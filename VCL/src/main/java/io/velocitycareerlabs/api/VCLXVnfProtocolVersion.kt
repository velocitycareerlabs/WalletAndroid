/**
 * Created by Michael Avoyan on 20/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api

enum class VCLXVnfProtocolVersion(val value: String) {
    XVnfProtocolVersion1("1.0"),
    XVnfProtocolVersion2("2.0");

    companion object {
        fun fromString(value: String) =
            when (value) {
                XVnfProtocolVersion1.value -> XVnfProtocolVersion1
                XVnfProtocolVersion2.value -> XVnfProtocolVersion2
                else -> XVnfProtocolVersion1
            }
    }
}