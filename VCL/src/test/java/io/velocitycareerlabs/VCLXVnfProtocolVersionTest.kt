/**
 * Created by Michael Avoyan on 26/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs

import io.velocitycareerlabs.api.VCLXVnfProtocolVersion
import org.junit.Test

class VCLXVnfProtocolVersionTest {
    @Test
    fun fromStringTest() {
        assert(VCLXVnfProtocolVersion.fromString(value = "1.0") == VCLXVnfProtocolVersion.XVnfProtocolVersion1)
        assert(VCLXVnfProtocolVersion.fromString(value = "2.0") == VCLXVnfProtocolVersion.XVnfProtocolVersion2)
        assert(VCLXVnfProtocolVersion.fromString(value = "123") == VCLXVnfProtocolVersion.XVnfProtocolVersion1) //default
    }
}