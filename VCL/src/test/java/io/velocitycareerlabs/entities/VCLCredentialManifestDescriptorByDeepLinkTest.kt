/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptorByDeepLink
import io.velocitycareerlabs.api.entities.VCLIssuingType
import io.velocitycareerlabs.api.entities.VCLPushDelegate
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.impl.extensions.isUrlEquivalentTo
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestDescriptorMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks

import org.junit.After
import org.junit.Before
import org.junit.Test

internal class VCLCredentialManifestDescriptorByDeepLinkTest {

    lateinit var subject: VCLCredentialManifestDescriptorByDeepLink

    @Before
    fun setUp() {
    }

    @Test
    fun testCredentialManifestDescriptorFullValidByDeepLinkSuccess() {
        subject = VCLCredentialManifestDescriptorByDeepLink(
            deepLink = CredentialManifestDescriptorMocks.DeepLink,
            issuingType = VCLIssuingType.Career,
            pushDelegate = VCLPushDelegate(pushUrl = "some_url", pushToken = "some_token"),
            didJwk = DidJwkMocks.DidJwk
        )

        assert(
            subject.endpoint?.decode()?.isUrlEquivalentTo(
                CredentialManifestDescriptorMocks.DeepLinkRequestUri.decode() + "&push_delegate.push_url=some_url&push_delegate.push_token=some_token"
            ) == true
        )
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
        assert(subject.pushDelegate?.pushUrl == "some_url")
        assert(subject.pushDelegate?.pushToken == "some_token")
    }

    @After
    fun tearDown() {
    }
}