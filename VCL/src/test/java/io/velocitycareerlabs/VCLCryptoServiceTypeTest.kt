/**
 * Created by Michael Avoyan on 26/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs

import io.velocitycareerlabs.api.VCLCryptoServiceType
import org.junit.Test

class VCLCryptoServiceTypeTest {
    @Test
    fun fromStringTest() {
        assert(VCLCryptoServiceType.fromString(value = "local") == VCLCryptoServiceType.Local)
        assert(VCLCryptoServiceType.fromString(value = "remote") == VCLCryptoServiceType.Remote)
        assert(VCLCryptoServiceType.fromString(value = "injected") == VCLCryptoServiceType.Injected)
        assert(VCLCryptoServiceType.fromString(value = "123") == VCLCryptoServiceType.Local) //default
    }
}