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
    fun testPresentationRequestDeepLinkValidAggregation() {

        subject = VCLDeepLink(value = DeepLinkMocks.PresentationRequestDeepLinkStr)

        assert(subject.value == DeepLinkMocks.PresentationRequestDeepLinkStr)
        assert(subject.value.decode() == DeepLinkMocks.PresentationRequestDeepLinkStr.decode())
        assert(subject.requestUri == DeepLinkMocks.PresentationRequestRequestDecodedUriStr)
        assert(subject.vendorOriginContext == DeepLinkMocks.PresentationRequestVendorOriginContext)
    }

    @Test
    fun testCredentialManifestDeepLinkValidAggregation() {

        subject = VCLDeepLink(value = DeepLinkMocks.CredentialManifestDeepLinkStr)

        assert(subject.value == DeepLinkMocks.CredentialManifestDeepLinkStr)
        assert(subject.value.decode() == DeepLinkMocks.CredentialManifestDeepLinkStr.decode())
        assert(subject.requestUri == DeepLinkMocks.CredentialManifestRequestDecodedUriStr)
        assert(subject.vendorOriginContext == null)
    }

    @After
    fun tearDown() {
    }
}