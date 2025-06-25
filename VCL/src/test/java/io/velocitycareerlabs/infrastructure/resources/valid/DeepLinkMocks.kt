/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.impl.extensions.encode

class DeepLinkMocks {
    companion object {
        private const val DevNetProtocol = "velocity-network-devnet"
        private const val TestNetProtocol = "velocity-network-testnet"
        private const val MainNetProtocol = "velocity-network"

        const val OIDIssuerDid = "did:velocity:0xc257274276a4e539741ca11b590b9447b26a8051"

        const val Issuer =
            "https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Avelocity%3A0xc257274276a4e539741ca11b590b9447b26a8051%2Foidc%26credential_type%3DPastEmploymentPosition%26pre-authorized_code%3D8L1UArquTYvE-ylC2BV_2%26issuerDid%3Ddid%3Avelocity%3A0xc257274276a4e539741ca11b590b9447b26a8051"

        const val OpenidInitiateIssuanceStrDev = "openid-initiate-issuance://?issuer=$Issuer"

        const val InspectorDid = "did:velocity:0xd4df29726d500f9b85bc6c7f1b3c021f16305692"
        const val InspectorId = "987934576974554"

        const val PresentationRequestVendorOriginContext =
            "{\"SubjectKey\":{\"BusinessUnit\":\"ZC\",\"KeyCode\":\"54514480\"},\"Token\":\"832077a4\"}"

        var PresentationRequestRequestDecodedUriStr =
            "https://agent.velocitycareerlabs.io/api/holder/v0.6/org/$InspectorDid/inspect/get-presentation-request?id=62e0e80c5ebfe73230b0becc&inspectorDid=$InspectorDid&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22,%22KeyCode%22%3A%2254514480%22%7D,%22Token%22%3A%22832077a4%22%7D".decode()
        var PresentationRequestRequestDecodedUriWithIdStr =
            "https://agent.velocitycareerlabs.io/api/holder/v0.6/org/$InspectorId/inspect/get-presentation-request?id=62e0e80c5ebfe73230b0becc&inspectorDid=$InspectorDid&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22,%22KeyCode%22%3A%2254514480%22%7D,%22Token%22%3A%22832077a4%22%7D".decode()

        const val PresentationRequestRequestUriStr =
            "https%3A%2F%2Fagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f16305692%2Finspect%2Fget-presentation-request%3Fid%3D62e0e80c5ebfe73230b0becc%26inspectorDid%3Ddid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f16305692%26vendorOriginContext%3D%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22%2C%22KeyCode%22%3A%2254514480%22%7D%2C%22Token%22%3A%22832077a4%22%7D"
        const val PresentationRequestRequestUriWithIdStr =
            "https%3A%2F%2Fagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2F$InspectorId%2Finspect%2Fget-presentation-request%3Fid%3D62e0e80c5ebfe73230b0becc%26inspectorDid%3Ddid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f16305692%26vendorOriginContext%3D%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22%2C%22KeyCode%22%3A%2254514480%22%7D%2C%22Token%22%3A%22832077a4%22%7D"

        const val PresentationRequestDeepLinkDevNetStr =
            "$DevNetProtocol://inspect?request_uri=$PresentationRequestRequestUriStr"
        const val PresentationRequestDeepLinkTestNetStr =
            "$TestNetProtocol://inspect?request_uri=$PresentationRequestRequestUriStr"
        const val PresentationRequestDeepLinkMainNetStr =
            "$MainNetProtocol://inspect?request_uri=$PresentationRequestRequestUriStr"
        const val PresentationRequestDeepLinkMainNetWithIdStr =
            "$MainNetProtocol://inspect?request_uri=$PresentationRequestRequestUriWithIdStr"

        const val IssuerDid = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA"
        const val IssuerId = "843794687t394524"

        const val CredentialManifestRequestDecodedUriStr =
            "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/$IssuerDid/issue/get-credential-manifest?id=611b5836e93d08000af6f1bc&credential_types=PastEmploymentPosition&issuerDid=$IssuerDid"
        const val CredentialManifestRequestDecodedUriWithIdStr =
            "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/$IssuerId/issue/get-credential-manifest?id=611b5836e93d08000af6f1bc&credential_types=PastEmploymentPosition&issuerDid=$IssuerDid"

        const val CredentialManifestRequestUriStr =
            "https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Fissue%2Fget-credential-manifest%3Fid%3D611b5836e93d08000af6f1bc%26credential_types%3DPastEmploymentPosition%26issuerDid%3Ddid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA"
        const val CredentialManifestRequestUriWithIdStr =
            "https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2F$IssuerId%2Fissue%2Fget-credential-manifest%3Fid%3D611b5836e93d08000af6f1bc%26credential_types%3DPastEmploymentPosition%26issuerDid%3Ddid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA"

        const val CredentialManifestDeepLinkDevNetStr = "$DevNetProtocol://issue?request_uri=$CredentialManifestRequestUriStr"
        const val CredentialManifestDeepLinkTestNetStr = "$TestNetProtocol://issue?request_uri=$CredentialManifestRequestUriStr"
        const val CredentialManifestDeepLinkMainNetStr = "$MainNetProtocol://issue?request_uri=$CredentialManifestRequestUriStr"
        const val CredentialManifestDeepLinkMainNetWithIdStr = "$MainNetProtocol://issue?request_uri=$CredentialManifestRequestUriWithIdStr"

        val CredentialManifestDeepLinkDevNet = VCLDeepLink(value = CredentialManifestDeepLinkDevNetStr)
        val CredentialManifestDeepLinkTestNet = VCLDeepLink(value = CredentialManifestDeepLinkTestNetStr)
        val CredentialManifestDeepLinkMainNet = VCLDeepLink(value = CredentialManifestDeepLinkMainNetStr)
        val CredentialManifestDeepLinkMainNetWithId = VCLDeepLink(value = CredentialManifestDeepLinkMainNetWithIdStr)

        val PresentationRequestDeepLinkDevNet = VCLDeepLink(value = DeepLinkMocks.PresentationRequestDeepLinkDevNetStr)
        val PresentationRequestDeepLinkMainNetWithId = VCLDeepLink(value = DeepLinkMocks.PresentationRequestDeepLinkMainNetWithIdStr)
    }
}