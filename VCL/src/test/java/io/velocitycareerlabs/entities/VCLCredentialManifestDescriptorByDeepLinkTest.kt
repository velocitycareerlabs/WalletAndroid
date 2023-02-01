/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptorByDeepLink
import io.velocitycareerlabs.api.entities.VCLServiceType
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.impl.extensions.isUrlEquivalentTo
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestDescriptorMocks

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
            serviceType = VCLServiceType.Issuer
        )

        assert(
            subject.endpoint?.isUrlEquivalentTo(CredentialManifestDescriptorMocks.DeepLinkRequestUri.decode())!!
        )
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @After
    fun tearDown() {
    }
}