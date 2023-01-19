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
            pushDelegate = PresentationRequestDescriptorMocks.PushDelegate
        )

        val queryParams =
                    "${VCLPresentationRequestDescriptor.KeyPushDelegatePushUrl}=${PresentationRequestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${VCLPresentationRequestDescriptor.KeyPushDelegatePushToken}=${PresentationRequestDescriptorMocks.PushDelegate.pushToken.encode()}"
        val mockEndpoint = (PresentationRequestDescriptorMocks.RequestUri.decode() + "?" + queryParams)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.pushDelegate!!.pushUrl == PresentationRequestDescriptorMocks.PushDelegate.pushUrl)
        assert(subject.pushDelegate!!.pushToken == PresentationRequestDescriptorMocks.PushDelegate.pushToken)
    }

    @Test
    fun testPresentationRequestDescriptorWithoutPushDelegateOnlySuccess() {
        subject = VCLPresentationRequestDescriptor(
            deepLink = PresentationRequestDescriptorMocks.DeepLink
        )

        assert(subject.endpoint?.isUrlEquivalentTo(PresentationRequestDescriptorMocks.RequestUri.decode())!!)
        assert(subject.pushDelegate == null)
    }

    @Test
    fun testPresentationRequestDescriptorWithQParamsWithPushDelegateSuccess() {
        subject = VCLPresentationRequestDescriptor(
            deepLink = PresentationRequestDescriptorMocks.DeepLinkWithQParams,
            pushDelegate = PresentationRequestDescriptorMocks.PushDelegate
        )

        val queryParams =
            "${VCLPresentationRequestDescriptor.KeyPushDelegatePushUrl}=${PresentationRequestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${VCLPresentationRequestDescriptor.KeyPushDelegatePushToken}=${PresentationRequestDescriptorMocks.PushDelegate.pushToken.encode()}"
        val mockEndpoint = (
                PresentationRequestDescriptorMocks.RequestUri.decode() + "?" + PresentationRequestDescriptorMocks.QParms + "&" + queryParams
                )

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.pushDelegate!!.pushUrl == PresentationRequestDescriptorMocks.PushDelegate.pushUrl)
        assert(subject.pushDelegate!!.pushToken == PresentationRequestDescriptorMocks.PushDelegate.pushToken)
    }

    @Test
    fun testPresentationRequestDescriptorWithQParamsWithoutPushDelegateOnlySuccess() {
        subject = VCLPresentationRequestDescriptor(
            deepLink = PresentationRequestDescriptorMocks.DeepLinkWithQParams
        )

        val mockEndpoint = (PresentationRequestDescriptorMocks.RequestUri.decode() + "?" + PresentationRequestDescriptorMocks.QParms)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.pushDelegate == null)
    }

    @After
    fun tearDown() {
    }
}