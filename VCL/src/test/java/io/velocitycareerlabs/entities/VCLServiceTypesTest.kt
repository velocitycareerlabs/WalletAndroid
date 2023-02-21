/**
 * Created by Michael Avoyan on 14/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLIssuingType
import io.velocitycareerlabs.api.entities.VCLServiceType
import io.velocitycareerlabs.api.entities.VCLServiceTypes
import org.junit.Test

class VCLServiceTypesTest {
    @Test
    fun testContainsFull() {
        val serviceTypes = VCLServiceTypes(
            listOf(
                VCLServiceType.Issuer,
                VCLServiceType.Inspector,
                VCLServiceType.CareerIssuer,
                VCLServiceType.NotaryIssuer,
                VCLServiceType.IdentityIssuer
            )
        )

        assert(serviceTypes.contains(VCLServiceType.Issuer))
        assert(serviceTypes.contains(VCLServiceType.Inspector))
        assert(serviceTypes.contains(VCLServiceType.CareerIssuer))
        assert(serviceTypes.contains(VCLServiceType.NotaryIssuer))
        assert(serviceTypes.contains(VCLServiceType.IdentityIssuer))

        assert(serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(VCLServiceType.IdentityIssuer,
                VCLServiceType.Inspector,
                VCLServiceType.NotaryIssuer
            ))))

        assert(!serviceTypes.contains(VCLServiceType.Undefined))

        assert(!serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(
                VCLServiceType.Undefined
            ))))
    }

    @Test
    fun testContainsPartial() {
        val serviceTypes = VCLServiceTypes(
            listOf(
                VCLServiceType.Issuer,
                VCLServiceType.Inspector,
                VCLServiceType.CareerIssuer
            )
        )

        assert(serviceTypes.contains(VCLServiceType.Issuer))
        assert(serviceTypes.contains(VCLServiceType.Inspector))
        assert(serviceTypes.contains(VCLServiceType.CareerIssuer))

        assert(serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(VCLServiceType.Inspector
            ))))

        assert(!serviceTypes.contains(VCLServiceType.NotaryIssuer))
        assert(!serviceTypes.contains(VCLServiceType.IdentityIssuer))
        assert(!serviceTypes.contains(VCLServiceType.Undefined))

        assert(!serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(VCLServiceType.IdentityIssuer
            ))))
    }

    @Test
    fun testContainsEmpty() {
        val serviceTypes = VCLServiceTypes(listOf())

        assert(!serviceTypes.contains(VCLServiceType.Issuer))
        assert(!serviceTypes.contains(VCLServiceType.Inspector))
        assert(!serviceTypes.contains(VCLServiceType.CareerIssuer))
        assert(!serviceTypes.contains(VCLServiceType.NotaryIssuer))
        assert(!serviceTypes.contains(VCLServiceType.IdentityIssuer))
        assert(!serviceTypes.contains(VCLServiceType.Undefined))

        assert(!serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(
                VCLServiceType.Issuer,
                VCLServiceType.Inspector,
                VCLServiceType.CareerIssuer,
                VCLServiceType.NotaryIssuer,
                VCLServiceType.IdentityIssuer
            ))))
    }

    @Test
    fun testFromCareer() {
        val serviceTypes = VCLServiceTypes(VCLIssuingType.Career)

        assert(serviceTypes.contains(VCLServiceType.Issuer))
        assert(serviceTypes.contains(VCLServiceType.CareerIssuer))
        assert(serviceTypes.contains(VCLServiceType.NotaryIssuer))

        assert(serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(
                VCLServiceType.Inspector,
                VCLServiceType.NotaryIssuer
            ))))

        assert(!serviceTypes.contains(VCLServiceType.IdentityIssuer))
        assert(!serviceTypes.contains(VCLServiceType.Undefined))

        assert(!serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(
                VCLServiceType.Inspector,
            ))))
    }

    @Test
    fun testFromIdentity() {
        val serviceTypes = VCLServiceTypes(VCLIssuingType.Identity)

        assert(serviceTypes.contains(VCLServiceType.IdentityIssuer))

        assert(serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(VCLServiceType.IdentityIssuer, VCLServiceType.Inspector)
        )))

        assert(!serviceTypes.contains(VCLServiceType.Issuer))
        assert(!serviceTypes.contains(VCLServiceType.CareerIssuer))
        assert(!serviceTypes.contains(VCLServiceType.NotaryIssuer))
        assert(!serviceTypes.contains(VCLServiceType.Undefined))

        assert(!serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(VCLServiceType.Issuer, VCLServiceType.Inspector))))
    }

    @Test
    fun testFromRefresh() {
        val serviceTypes = VCLServiceTypes(VCLIssuingType.Refresh)

        assert(serviceTypes.contains(VCLServiceType.IdentityIssuer))
        assert(serviceTypes.contains(VCLServiceType.Issuer))
        assert(serviceTypes.contains(VCLServiceType.CareerIssuer))
        assert(serviceTypes.contains(VCLServiceType.NotaryIssuer))

        assert(serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(VCLServiceType.IdentityIssuer, VCLServiceType.Inspector
            ))))

        assert(!serviceTypes.contains(VCLServiceType.Undefined))

        assert(!serviceTypes.containsAtLeastOneOf(VCLServiceTypes(
            listOf(VCLServiceType.Inspector
            ))))
    }

    @Test
    fun testFromUndefined() {
        val serviceTypes = VCLServiceTypes(VCLIssuingType.Undefined)

        assert(!serviceTypes.contains(VCLServiceType.Undefined))
    }
}