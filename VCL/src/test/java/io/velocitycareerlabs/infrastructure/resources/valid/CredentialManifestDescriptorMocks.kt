package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLPushDelegate

/**
 * Created by Michael Avoyan on 8/15/21.
 */
class CredentialManifestDescriptorMocks {
    companion object {

        val DeepLink = DeepLinkMocks.CredentialManifestDeepLink

        const val DeepLinkRequestUri = DeepLinkMocks.CredentialManifestRequestUriStr

        val CredentialTypesList = listOf("PastEmploymentPosition", "CurrentEmploymentPosition")

        val PushDelegate = VCLPushDelegate(
            pushUrl = "https://devservices.velocitycareerlabs.io/api/push-gateway",
            pushToken = "if0123asd129smw321"
        )

        const val IssuingServiceEndPoint =
            "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3/issue/get-credential-manifest"

        const val IssuingServiceJsonStr =
            "{\"id\":\"did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3#credential-agent-issuer-1\",\"type\":\"VelocityCredentialAgentIssuer_v1.0\",\"credentialTypes\":[\"Course\",\"EducationDegree\",\"Badge\"],\"serviceEndpoint\":\"$IssuingServiceEndPoint\"}"

        const val IssuingServiceWithParamEndPoint =
            "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3/issue/get-credential-manifest?key=value"

        const val IssuingServiceWithPAramJsonStr =
            "{\"id\":\"did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3#credential-agent-issuer-1\",\"type\":\"VelocityCredentialAgentIssuer_v1.0\",\"credentialTypes\":[\"Course\",\"EducationDegree\",\"Badge\"],\"serviceEndpoint\":\"$IssuingServiceWithParamEndPoint\"}"

        const val CredentialId1 = "did:velocity:v2:0x2bef092530ccc122f5fe439b78eddf6010685e88:248532930732481:1963"
        const val CredentialId2 = "did:velocity:v2:0x2bef092530ccc122f5fe439b78eddf6010685e88:248532930732481:1963"
    }
}