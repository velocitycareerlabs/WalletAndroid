/**
 * Created by Michael Avoyan on 14/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLServiceType
import org.junit.Test

class VCLServiceTypeTest {
    @Test
    fun testFromExactString() {
        assert(VCLServiceType.fromString(value = "Issuer") == VCLServiceType.Issuer)
        assert(VCLServiceType.fromString(value = "Inspector") == VCLServiceType.Inspector)
        assert(VCLServiceType.fromString(value = "CareerIssuer") == VCLServiceType.CareerIssuer)
        assert(VCLServiceType.fromString(value = "NotaryIssuer") == VCLServiceType.NotaryIssuer)
        assert(VCLServiceType.fromString(value = "IdentityIssuer") == VCLServiceType.IdentityIssuer)
        assert(VCLServiceType.fromString(value = "OtherService") == VCLServiceType.Undefined)
        assert(VCLServiceType.fromString(value = "Undefined") == VCLServiceType.Undefined)
    }

    @Test
    fun testFromNonExactString() {
        assert(VCLServiceType.fromString(value = "11_Issuer6_2") == VCLServiceType.Issuer)
        assert(VCLServiceType.fromString(value = "hyre_8Inspector09_nf") == VCLServiceType.Inspector)
        assert(VCLServiceType.fromString(value = "9jfCareerIssuer@#$%") == VCLServiceType.CareerIssuer)
        assert(VCLServiceType.fromString(value = ")64fhsNotaryIssuer") == VCLServiceType.NotaryIssuer)
        assert(VCLServiceType.fromString(value = "IdentityIssuer05%#Rg&*") == VCLServiceType.IdentityIssuer)
        assert(VCLServiceType.fromString(value = "ksdjhkD#OtherService959)%") == VCLServiceType.Undefined)
        assert(VCLServiceType.fromString(value = "#Wfg85\$Undefined)%dgsc") == VCLServiceType.Undefined)
        assert(VCLServiceType.fromString(value = "") == VCLServiceType.Undefined)
    }
}