/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLPushDelegate

class CredentialManifestDescriptorMocks {
    companion object {

        val DeepLink = DeepLinkMocks.CredentialManifestDeepLinkMainNet
        val IssuerDid = DeepLinkMocks.IssuerDid

        const val DeepLinkRequestUri = DeepLinkMocks.CredentialManifestRequestUriStr

        val CredentialTypesList = listOf("PastEmploymentPosition", "CurrentEmploymentPosition")

        val PushDelegate = VCLPushDelegate(
            pushUrl = "https://devservices.velocitycareerlabs.io/api/push-gateway",
            pushToken = "if0123asd129smw321",
        )

        val IssuingServiceEndPoint =
            "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/$IssuerDid/issue/get-credential-manifest"

        val IssuingServiceJsonStr =
            "{\"id\":\"$IssuerDid#credential-agent-issuer-1\",\"type\":\"VelocityCredentialAgentIssuer_v1.0\",\"credentialTypes\":[\"Course\",\"EducationDegree\",\"Badge\"],\"serviceEndpoint\":\"$IssuingServiceEndPoint\"}"

        val IssuingServiceWithParamEndPoint =
            "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/$IssuerDid/issue/get-credential-manifest?key=value"

        val IssuingServiceWithParamJsonStr =
            "{\"id\":\"$IssuerDid#credential-agent-issuer-1\",\"type\":\"VelocityCredentialAgentIssuer_v1.0\",\"credentialTypes\":[\"Course\",\"EducationDegree\",\"Badge\"],\"serviceEndpoint\":\"$IssuingServiceWithParamEndPoint\"}"

        const val CredentialId1 = "did:velocity:v2:0x2bef092530ccc122f5fe439b78eddf6010685e88:248532930732481:1963"
        const val CredentialId2 = "did:velocity:v2:0x2bef092530ccc122f5fe439b78eddf6010685e88:248532930732481:1963"
    }
}