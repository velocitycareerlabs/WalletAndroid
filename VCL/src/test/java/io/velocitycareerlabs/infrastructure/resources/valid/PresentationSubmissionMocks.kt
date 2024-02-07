/**
 * Created by Michael Avoyan on 5/1/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLPushDelegate
import io.velocitycareerlabs.api.entities.VCLVerifiableCredential

class PresentationSubmissionMocks {
    companion object {
        val PushDelegate = VCLPushDelegate(
            pushUrl = "https://devservices.velocitycareerlabs.io/api/push-gateway",
            pushToken = "if0123asd129smw321"
        )
        const val PresentationSubmissionResultJson =
            "{\"token\":\"u7yLD8KS2eTEqkg9aRQE\",\"exchange\":{\"id\":\"64131231\",\"type\":\"DISCLOSURE\",\"disclosureComplete\":true,\"exchangeComplete\":true}}"
        val PresentationRequest = VCLPresentationRequest(
            jwt = JwtServiceMocks.JWT,
            publicJwk = JwtServiceMocks.PublicJwk,
            deepLink = DeepLinkMocks.CredentialManifestDeepLinkMainNet,
            pushDelegate = PushDelegate,
            didJwk = DidJwkMocks.DidJwk
        )

        val SelectionsList = listOf(
            VCLVerifiableCredential(
                inputDescriptor = "PhoneV1.0",
                jwtVc = JwtServiceMocks.AdamSmithPhoneJwt
            ),
            VCLVerifiableCredential(
                inputDescriptor = "EmailV1.0",
                jwtVc = JwtServiceMocks.AdamSmithEmailJwt
            )
        )

        val PresentationSubmissionJwt = PresentationRequestMocks.PresentationRequestJwt
    }
}