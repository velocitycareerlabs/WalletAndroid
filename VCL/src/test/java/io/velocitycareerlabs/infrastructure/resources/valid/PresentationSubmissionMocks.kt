/**
 * Created by Michael Avoyan on 5/1/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLVerifiableCredential

class PresentationSubmissionMocks {
    companion object {
        const val PresentationSubmissionResultJson ="{\"token\":\"u7yLD8KS2eTEqkg9aRQE\",\"exchange\":{\"id\":\"64131231\",\"type\":\"DISCLOSURE\",\"disclosureComplete\":true,\"exchangeComplete\":true}}"
        val PresentationRequest = VCLPresentationRequest(
            JwtServiceMocks.JWT,
            JwtServiceMocks.PublicKey,
            DeepLinkMocks.CredentialManifestDeepLinkMainNet
        )

        val SelectionsList = listOf(
            VCLVerifiableCredential("IdDocument", JwtServiceMocks.AdamSmithIdDocumentJwt),
            VCLVerifiableCredential("Email", JwtServiceMocks.AdamSmithEmailJwt)
        )
    }
}