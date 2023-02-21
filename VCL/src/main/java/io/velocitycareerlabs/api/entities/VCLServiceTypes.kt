/**
 * Created by Michael Avoyan on 14/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

class VCLServiceTypes {

    val all: List<VCLServiceType>

    constructor(all: List<VCLServiceType>) {
        this.all = all
    }

    constructor(serviceType: VCLServiceType): this(listOf(serviceType))

    constructor(issuingType: VCLIssuingType) : this(
        when(issuingType) {
            VCLIssuingType.Career -> listOf(
                VCLServiceType.Issuer,
                VCLServiceType.CareerIssuer,
                VCLServiceType.NotaryIssuer
            )
            VCLIssuingType.Identity -> listOf(VCLServiceType.IdentityIssuer)
            VCLIssuingType.Refresh -> listOf(
                VCLServiceType.Issuer,
                VCLServiceType.CareerIssuer,
                VCLServiceType.NotaryIssuer,
                VCLServiceType.IdentityIssuer
            )
            VCLIssuingType.Undefined -> listOf(VCLServiceType.Undefined)
        }
    )

    fun containsAtLeastOneOf(serviceTypes: VCLServiceTypes): Boolean =
        all.any { serviceTypes.all.contains(it) && it != VCLServiceType.Undefined }

    fun contains(serviceType: VCLServiceType): Boolean =
        all.contains(serviceType) && serviceType != VCLServiceType.Undefined
}