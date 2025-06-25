/**
 * Created by Michael Avoyan on 21/11/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.impl.extensions.encode
import io.velocitycareerlabs.impl.extensions.isUrlEquivalentTo
import io.velocitycareerlabs.infrastructure.resources.valid.DeepLinkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestDescriptorMocks
import org.junit.After
import org.junit.Before
import org.junit.Test

class VCLPresentationRequestDescriptorTest {

    lateinit var subject: VCLPresentationRequestDescriptor

    @Before
    fun setUp() {
    }

    @Test
    fun testPresentationRequestDescriptorWithPushDelegateSuccess() {
        subject = VCLPresentationRequestDescriptor(
            deepLink = PresentationRequestDescriptorMocks.DeepLink,
            pushDelegate = PresentationRequestDescriptorMocks.PushDelegate,
            didJwk = DidJwkMocks.DidJwk
        )

        val queryParams =
                    "${VCLPresentationRequestDescriptor.KeyPushDelegatePushUrl}=${PresentationRequestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${VCLPresentationRequestDescriptor.KeyPushDelegatePushToken}=${PresentationRequestDescriptorMocks.PushDelegate.pushToken.encode()}"
        val mockEndpoint = (PresentationRequestDescriptorMocks.RequestUri.decode() + "&" + queryParams)

        assert(subject.endpoint?.decode()?.isUrlEquivalentTo(mockEndpoint.decode()) == true)
        assert(subject.pushDelegate!!.pushUrl == PresentationRequestDescriptorMocks.PushDelegate.pushUrl)
        assert(subject.pushDelegate!!.pushToken == PresentationRequestDescriptorMocks.PushDelegate.pushToken)
        assert(subject.did == PresentationRequestDescriptorMocks.InspectorDid)
    }

    @Test
    fun testPresentationRequestDescriptorWithoutPushDelegateOnlySuccess() {
        subject = VCLPresentationRequestDescriptor(
            deepLink = PresentationRequestDescriptorMocks.DeepLink,
            didJwk = DidJwkMocks.DidJwk
        )

        assert(subject.endpoint?.decode()?.isUrlEquivalentTo(PresentationRequestDescriptorMocks.RequestUri.decode()) == true)
        assert(subject.pushDelegate == null)
        assert(subject.did == PresentationRequestDescriptorMocks.InspectorDid)
    }

    @Test
    fun testPresentationRequestDescriptorWithQParamsWithPushDelegateSuccess() {
        subject = VCLPresentationRequestDescriptor(
            deepLink = PresentationRequestDescriptorMocks.DeepLinkWithQParams,
            pushDelegate = PresentationRequestDescriptorMocks.PushDelegate,
            didJwk = DidJwkMocks.DidJwk
        )

        val queryParams =
            "${VCLPresentationRequestDescriptor.KeyPushDelegatePushUrl}=${PresentationRequestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${VCLPresentationRequestDescriptor.KeyPushDelegatePushToken}=${PresentationRequestDescriptorMocks.PushDelegate.pushToken.encode()}"
        val mockEndpoint = (
                PresentationRequestDescriptorMocks.RequestUri.decode() + "&" + PresentationRequestDescriptorMocks.QParams + "&" + queryParams
                )

        assert(subject.endpoint?.decode()?.isUrlEquivalentTo(mockEndpoint.decode()) == true)
        assert(subject.pushDelegate!!.pushUrl == PresentationRequestDescriptorMocks.PushDelegate.pushUrl)
        assert(subject.pushDelegate!!.pushToken == PresentationRequestDescriptorMocks.PushDelegate.pushToken)
        assert(subject.did == PresentationRequestDescriptorMocks.InspectorDid)
    }

    @Test
    fun testPresentationRequestDescriptorWithQParamsWithoutPushDelegateOnlySuccess() {
        subject = VCLPresentationRequestDescriptor(
            deepLink = PresentationRequestDescriptorMocks.DeepLinkWithQParams,
            didJwk = DidJwkMocks.DidJwk
        )

        val mockEndpoint =
            (PresentationRequestDescriptorMocks.RequestUri.decode() + "&" + PresentationRequestDescriptorMocks.QParams)

        assert(subject.endpoint?.decode()?.isUrlEquivalentTo(mockEndpoint.decode()) == true)
        assert(subject.pushDelegate == null)
        assert(subject.did == PresentationRequestDescriptorMocks.InspectorDid)
    }

    @Test
    fun testPresentationRequestDescriptorWithInspectorIdSuccess() {
        subject = VCLPresentationRequestDescriptor(
            deepLink = DeepLinkMocks.PresentationRequestDeepLinkMainNetWithId,
            didJwk = DidJwkMocks.DidJwk
        )

        val mockEndpoint = DeepLinkMocks.PresentationRequestRequestDecodedUriWithIdStr

        assert(subject.endpoint?.decode()?.isUrlEquivalentTo(mockEndpoint) == true)
        assert(subject.pushDelegate == null)
        assert(subject.did == PresentationRequestDescriptorMocks.InspectorDid)
    }

    @After
    fun tearDown() {
    }
}