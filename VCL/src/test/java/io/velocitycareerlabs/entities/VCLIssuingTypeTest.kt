/**
 * Created by Michael Avoyan on 15/02/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLIssuingType
import org.junit.Test

class VCLIssuingTypeTest {
    @Test
    fun testFromExactString() {
        assert(VCLIssuingType.fromString(value = "Career") == VCLIssuingType.Career)
        assert(VCLIssuingType.fromString(value = "Identity") == VCLIssuingType.Identity)
        assert(VCLIssuingType.fromString(value = "Refresh") == VCLIssuingType.Refresh)
        assert(VCLIssuingType.fromString(value = "Undefined") == VCLIssuingType.Undefined)
    }

    @Test
    fun testFromNonExactString() {
        assert(VCLIssuingType.fromString(value = "11_Career6_2") == VCLIssuingType.Career)
        assert(VCLIssuingType.fromString(value = "hyre_8Identity09_nf") == VCLIssuingType.Identity)
        assert(VCLIssuingType.fromString(value = "hyrek_yRefresho89#l") == VCLIssuingType.Refresh)
        assert(VCLIssuingType.fromString(value = "") == VCLIssuingType.Undefined)
    }
}