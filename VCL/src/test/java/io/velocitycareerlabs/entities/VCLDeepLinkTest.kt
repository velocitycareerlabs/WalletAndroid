package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Michael Avoyan on 8/15/21.
 */
internal class VCLDeepLinkTest {

    lateinit var subject: VCLDeepLink

    @Before
    fun setUp() {
    }

    @Test
    fun testPresentationRequestDeepLinkDevNetValidAggregation() {

        subject = VCLDeepLink(value = DeepLinkMocks.PresentationRequestDeepLinkDevNetStr)

        assert(subject.value == DeepLinkMocks.PresentationRequestDeepLinkDevNetStr)
        assert(subject.value.decode() == DeepLinkMocks.PresentationRequestDeepLinkDevNetStr.decode())
        assert(subject.requestUri == DeepLinkMocks.PresentationRequestRequestDecodedUriStr)
        assert(subject.vendorOriginContext == DeepLinkMocks.PresentationRequestVendorOriginContext)
    }

    @Test
    fun testPresentationRequestDeepLinkTestNetValidAggregation() {

        subject = VCLDeepLink(value = DeepLinkMocks.PresentationRequestDeepLinkTestNetStr)

        assert(subject.value == DeepLinkMocks.PresentationRequestDeepLinkTestNetStr)
        assert(subject.value.decode() == DeepLinkMocks.PresentationRequestDeepLinkTestNetStr.decode())
        assert(subject.requestUri == DeepLinkMocks.PresentationRequestRequestDecodedUriStr)
        assert(subject.vendorOriginContext == DeepLinkMocks.PresentationRequestVendorOriginContext)
    }

    @Test
    fun testPresentationRequestDeepLinkMainNetValidAggregation() {

        subject = VCLDeepLink(value = DeepLinkMocks.PresentationRequestDeepLinkMainNetStr)

        assert(subject.value == DeepLinkMocks.PresentationRequestDeepLinkMainNetStr)
        assert(subject.value.decode() == DeepLinkMocks.PresentationRequestDeepLinkMainNetStr.decode())
        assert(subject.requestUri == DeepLinkMocks.PresentationRequestRequestDecodedUriStr)
        assert(subject.vendorOriginContext == DeepLinkMocks.PresentationRequestVendorOriginContext)
    }

    @Test
    fun testCredentialManifestDeepLinkDevNetValidAggregation() {

        subject = VCLDeepLink(value = DeepLinkMocks.CredentialManifestDeepLinkDevNetStr)

        assert(subject.value == DeepLinkMocks.CredentialManifestDeepLinkDevNetStr)
        assert(subject.value.decode() == DeepLinkMocks.CredentialManifestDeepLinkDevNetStr.decode())
        assert(subject.requestUri == DeepLinkMocks.CredentialManifestRequestDecodedUriStr)
        assert(subject.vendorOriginContext == null)
    }

    @Test
    fun testCredentialManifestDeepLinkTestNetValidAggregation() {

        subject = VCLDeepLink(value = DeepLinkMocks.CredentialManifestDeepLinkTestNetStr)

        assert(subject.value == DeepLinkMocks.CredentialManifestDeepLinkTestNetStr)
        assert(subject.value.decode() == DeepLinkMocks.CredentialManifestDeepLinkTestNetStr.decode())
        assert(subject.requestUri == DeepLinkMocks.CredentialManifestRequestDecodedUriStr)
        assert(subject.vendorOriginContext == null)
    }

    @Test
    fun testCredentialManifestDeepLinkMainNetValidAggregation() {

        subject = VCLDeepLink(value = DeepLinkMocks.CredentialManifestDeepLinkMainNetStr)

        assert(subject.value == DeepLinkMocks.CredentialManifestDeepLinkMainNetStr)
        assert(subject.value.decode() == DeepLinkMocks.CredentialManifestDeepLinkMainNetStr.decode())
        assert(subject.requestUri == DeepLinkMocks.CredentialManifestRequestDecodedUriStr)
        assert(subject.vendorOriginContext == null)
    }

    @After
    fun tearDown() {
    }
}