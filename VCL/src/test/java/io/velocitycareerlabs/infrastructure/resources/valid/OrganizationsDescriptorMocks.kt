/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLServiceType
import io.velocitycareerlabs.api.entities.VCLFilter
import io.velocitycareerlabs.api.entities.VCLPage

class OrganizationsDescriptorMocks {
    companion object {
        val Filter = VCLFilter(
            did = "did:velocity:0x2bef092530ccc122f5fe439b78eddf6010685e88",
            serviceTypes = listOf(VCLServiceType.Inspector),
            credentialTypes = listOf("EducationDegree")
        )
        val Page = VCLPage("1", "1")
        val Sort = listOf(listOf("createdAt","DESC"), listOf("pdatedAt", "ASC"))
        val Query = "Bank"
    }
}