/**
 * Created by Michael Avoyan on 14/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLServiceType
import io.velocitycareerlabs.api.entities.VCLServiceTypes
import org.junit.Test

class VCLServiceTypesTest {

    @Test
    fun testContainsFull() {
        val serviceTypes = VCLServiceTypes(listOf(
            VCLServiceType.Issuer,
            VCLServiceType.Inspector,
            VCLServiceType.TrustRoot,
            VCLServiceType.CareerIssuer
        ))

        assert(serviceTypes.contains(VCLServiceType.Issuer))
        assert(serviceTypes.contains(VCLServiceType.Inspector))
        assert(serviceTypes.contains(VCLServiceType.TrustRoot))
        assert(serviceTypes.contains(VCLServiceType.CareerIssuer))

        assert(!serviceTypes.contains(VCLServiceType.NodeOperator))
        assert(!serviceTypes.contains(VCLServiceType.NotaryIssuer))
        assert(!serviceTypes.contains(VCLServiceType.IdentityIssuer))
        assert(!serviceTypes.contains(VCLServiceType.HolderAppProvider))
        assert(!serviceTypes.contains(VCLServiceType.CredentialAgentOperator))
        assert(!serviceTypes.contains(VCLServiceType.Undefined))
    }

    @Test
    fun testContainsEmpty() {
        val serviceTypes = VCLServiceTypes(listOf())

        assert(!serviceTypes.contains(VCLServiceType.Issuer))
        assert(!serviceTypes.contains(VCLServiceType.Inspector))
        assert(!serviceTypes.contains(VCLServiceType.TrustRoot))
        assert(!serviceTypes.contains(VCLServiceType.CareerIssuer))
        assert(!serviceTypes.contains(VCLServiceType.NodeOperator))
        assert(!serviceTypes.contains(VCLServiceType.NotaryIssuer))
        assert(!serviceTypes.contains(VCLServiceType.IdentityIssuer))
        assert(!serviceTypes.contains(VCLServiceType.HolderAppProvider))
        assert(!serviceTypes.contains(VCLServiceType.CredentialAgentOperator))
        assert(!serviceTypes.contains(VCLServiceType.Undefined))
    }
}