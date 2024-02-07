/**
 * Created by Michael Avoyan on 19/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.junit.Before
import org.junit.Test
import java.lang.Exception

class VCLCredentialManifestTest {
    lateinit var subject: VCLCredentialManifest

    @Before
    fun setUp() {
        try {
            subject = VCLCredentialManifest(
                jwt = VCLJwt(encodedJwt = CredentialManifestMocks.JwtCredentialManifest1),
                verifiedProfile = VCLVerifiedProfile(payload = VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!),
                didJwk = DidJwkMocks.DidJwk
            )
        } catch (ex: Exception) {
            assert(false) {"$ex"}
        }
    }

    @Test
    fun testProps() {
        assert(subject.iss == "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA")
        assert(subject.did == "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA")
        assert(subject.issuerId == "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA")
        assert(subject.aud == "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA")
        assert(subject.exchangeId == "645e315309237c760ac022b1")
        assert(subject.presentationDefinitionId == "645e315309237c760ac022b1.6384a3ad148b1991687f67c9")
        assert(subject.finalizeOffersUri == "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/issue/finalize-offers")
        assert(subject.checkOffersUri == "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/issue/credential-offers")
        assert(subject.submitPresentationUri == "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/issue/submit-identification")
    }
}