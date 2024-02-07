/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.impl.extensions.isUrlEquivalentTo
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class VCLDeepLinkTest {

    lateinit var subject: VCLDeepLink

    @Test
    fun testOpenidInitiateIssuance() {
        subject = VCLDeepLink(value = DeepLinkMocks.OpenidInitiateIssuanceStrDev)

        assert(subject.value == DeepLinkMocks.OpenidInitiateIssuanceStrDev)
        assert(subject.value.decode() == DeepLinkMocks.OpenidInitiateIssuanceStrDev.decode())
        assert(subject.requestUri == null)
        assert(subject.did == DeepLinkMocks.OIDIssuerDid)
    }

    @Test
    fun testPresentationRequestDeepLinkDevNetValidAggregation() {
        subject = VCLDeepLink(value = DeepLinkMocks.PresentationRequestDeepLinkDevNetStr)

        assert(subject.value == DeepLinkMocks.PresentationRequestDeepLinkDevNetStr)
        assert(subject.value.decode() == DeepLinkMocks.PresentationRequestDeepLinkDevNetStr.decode())
        assert(
            subject.requestUri?.decode()
                ?.isUrlEquivalentTo(DeepLinkMocks.PresentationRequestRequestDecodedUriStr) == true
        )
        assert(subject.vendorOriginContext == DeepLinkMocks.PresentationRequestVendorOriginContext)
        assert(subject.did == DeepLinkMocks.InspectorDid)
    }

    @Test
    fun testPresentationRequestDeepLinkTestNetValidAggregation() {
        subject = VCLDeepLink(value = DeepLinkMocks.PresentationRequestDeepLinkTestNetStr)

        assert(subject.value == DeepLinkMocks.PresentationRequestDeepLinkTestNetStr)
        assert(subject.value.decode() == DeepLinkMocks.PresentationRequestDeepLinkTestNetStr.decode())
        assert(
            subject.requestUri?.decode()
                ?.isUrlEquivalentTo(DeepLinkMocks.PresentationRequestRequestDecodedUriStr) == true
        )
        assert(subject.vendorOriginContext == DeepLinkMocks.PresentationRequestVendorOriginContext)
        assert(subject.did == DeepLinkMocks.InspectorDid)
    }

    @Test
    fun testPresentationRequestDeepLinkMainNetValidAggregation() {
        subject = VCLDeepLink(value = DeepLinkMocks.PresentationRequestDeepLinkMainNetStr)

        assert(subject.value == DeepLinkMocks.PresentationRequestDeepLinkMainNetStr)
        assert(subject.value.decode() == DeepLinkMocks.PresentationRequestDeepLinkMainNetStr.decode())
        assert(
            subject.requestUri?.decode()
                ?.isUrlEquivalentTo(DeepLinkMocks.PresentationRequestRequestDecodedUriStr) == true
        )
        assert(subject.vendorOriginContext == DeepLinkMocks.PresentationRequestVendorOriginContext)
        assert(subject.did == DeepLinkMocks.InspectorDid)
    }

    @Test
    fun testCredentialManifestDeepLinkDevNetValidAggregation() {
        subject = VCLDeepLink(value = DeepLinkMocks.CredentialManifestDeepLinkDevNetStr)

        assert(subject.value == DeepLinkMocks.CredentialManifestDeepLinkDevNetStr)
        assert(subject.value.decode() == DeepLinkMocks.CredentialManifestDeepLinkDevNetStr.decode())
        assert(
            subject.requestUri?.decode()
                ?.isUrlEquivalentTo(DeepLinkMocks.CredentialManifestRequestDecodedUriStr) == true
        )
        assert(subject.vendorOriginContext == null)
        assert(subject.did == DeepLinkMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDeepLinkTestNetValidAggregation() {
        subject = VCLDeepLink(value = DeepLinkMocks.CredentialManifestDeepLinkTestNetStr)

        assert(subject.value == DeepLinkMocks.CredentialManifestDeepLinkTestNetStr)
        assert(subject.value.decode() == DeepLinkMocks.CredentialManifestDeepLinkTestNetStr.decode())
        assert(
            subject.requestUri?.decode()
                ?.isUrlEquivalentTo(DeepLinkMocks.CredentialManifestRequestDecodedUriStr) == true
        )
        assert(subject.vendorOriginContext == null)
        assert(subject.did == DeepLinkMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDeepLinkMainNetValidAggregation() {
        subject = VCLDeepLink(value = DeepLinkMocks.CredentialManifestDeepLinkMainNetStr)

        assert(subject.value == DeepLinkMocks.CredentialManifestDeepLinkMainNetStr)
        assert(subject.value.decode() == DeepLinkMocks.CredentialManifestDeepLinkMainNetStr.decode())
        assert(
            subject.requestUri?.decode()
                ?.isUrlEquivalentTo(DeepLinkMocks.CredentialManifestRequestDecodedUriStr) == true
        )
        assert(subject.vendorOriginContext == null)
        assert(subject.did == DeepLinkMocks.IssuerDid)
    }

    @After
    fun tearDown() {
    }
}