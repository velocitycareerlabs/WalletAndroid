/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLDeepLink

class DeepLinkMocks {
    companion object {
        private const val DevNetProtocol = "velocity-network-devnet"
        private const val TestNetProtocol = "velocity-network-testnet"
        private const val MainNetProtocol = "velocity-network"

        const val PresentationRequestRequestDecodedUriStr =
            "https://stagingagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA/inspect/get-presentation-request?id=62e0e80c5ebfe73230b0becc&inspectorDid=did%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22,%22KeyCode%22%3A%2254514480%22%7D,%22Token%22%3A%22832077a4%22%7D"

        const val PresentationRequestRequestUriStr =
            "https%3A%2F%2Fstagingagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA%2Finspect%2Fget-presentation-request%3Fid%3D62e0e80c5ebfe73230b0becc&inspectorDid=did%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22,%22KeyCode%22%3A%2254514480%22%7D,%22Token%22%3A%22832077a4%22%7D"

        const val PresentationRequestDeepLinkDevNetStr =
            "$DevNetProtocol://inspect?request_uri=$PresentationRequestRequestUriStr"
        const val PresentationRequestDeepLinkTestNetStr =
            "$TestNetProtocol://inspect?request_uri=$PresentationRequestRequestUriStr"
        const val PresentationRequestDeepLinkMainNetStr =
            "$MainNetProtocol://inspect?request_uri=$PresentationRequestRequestUriStr"

        const val PresentationRequestVendorOriginContext =
            "{\"SubjectKey\":{\"BusinessUnit\":\"ZC\",\"KeyCode\":\"54514480\"},\"Token\":\"832077a4\"}"

        const val CredentialManifestRequestDecodedUriStr =
            "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:velocity:0xd4df29726d500f9b85bc6c7f1b3c021f16305692/issue/get-credential-manifest?id=611b5836e93d08000af6f1bc&credential_types=PastEmploymentPosition"

        const val CredentialManifestRequestUriStr =
            "https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f16305692%2Fissue%2Fget-credential-manifest%3Fid%3D611b5836e93d08000af6f1bc%26credential_types%3DPastEmploymentPosition"

        const val CredentialManifestDeepLinkDevNetStr = "$DevNetProtocol://issue?request_uri=$CredentialManifestRequestUriStr"
        const val CredentialManifestDeepLinkTestNetStr = "$TestNetProtocol://issue?request_uri=$CredentialManifestRequestUriStr"
        const val CredentialManifestDeepLinkMainNetStr = "$MainNetProtocol://issue?request_uri=$CredentialManifestRequestUriStr"

        val CredentialManifestDeepLinkDevNet = VCLDeepLink(value = CredentialManifestDeepLinkDevNetStr)
        val CredentialManifestDeepLinkTestNet = VCLDeepLink(value = CredentialManifestDeepLinkTestNetStr)
        val CredentialManifestDeepLinkMainNet = VCLDeepLink(value = CredentialManifestDeepLinkMainNetStr)
    }
}