/**
 * Created by Michael Avoyan on 11/12/2022.
 *
 *  Copyright 2022 Velocity Career Labs inc.
 *  SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLIdentificationSubmission
import io.velocitycareerlabs.api.entities.VCLPresentationSubmission
import io.velocitycareerlabs.api.entities.VCLPushDelegate
import io.velocitycareerlabs.api.entities.VCLSubmission
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toList
import io.velocitycareerlabs.infrastructure.resources.CommonMocks
import io.velocitycareerlabs.infrastructure.resources.valid.JwtServiceMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationSubmissionMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.junit.Before
import org.junit.Test

class VCLSubmissionTest {
    private lateinit var subjectPresentationSubmission: VCLSubmission
    private lateinit var subjectIdentificationSubmission: VCLSubmission

    @Before
    fun setUp() {
        subjectPresentationSubmission = VCLPresentationSubmission(
            PresentationSubmissionMocks.PresentationRequest,
            PresentationSubmissionMocks.SelectionsList,
            iss = "inspection iss"
        )
        subjectIdentificationSubmission = VCLIdentificationSubmission(
            VCLCredentialManifest(
                jwt = CommonMocks.JWT,
                verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!)
            ),
            PresentationSubmissionMocks.SelectionsList,
            iss = "issuing iss"
        )
    }

    @Test
    fun testPayload() {
        assert(subjectPresentationSubmission.payload.optString(VCLSubmission.KeyJti) == subjectPresentationSubmission.jti)
        assert(subjectPresentationSubmission.payload.optString(VCLSubmission.KeyIss) == subjectPresentationSubmission.iss)

        assert(subjectIdentificationSubmission.payload.optString(VCLSubmission.KeyJti) == subjectIdentificationSubmission.jti)
        assert(subjectIdentificationSubmission.payload.optString(VCLSubmission.KeyIss) == subjectIdentificationSubmission.iss)
    }

    @Test
    fun testPushDelegate() {
        assert(subjectPresentationSubmission.pushDelegate!!.pushUrl == PresentationSubmissionMocks.PushDelegate.pushUrl)
        assert(subjectPresentationSubmission.pushDelegate!!.pushToken == PresentationSubmissionMocks.PushDelegate.pushToken)
    }

    @Test
    fun testRequestBody() {
        val requestBodyJsonObj = subjectPresentationSubmission.generateRequestBody(JwtServiceMocks.JWT)
        assert(requestBodyJsonObj.optString(VCLSubmission.KeyExchangeId) == subjectPresentationSubmission.exchangeId)
        assert(requestBodyJsonObj.optJSONArray(VCLSubmission.KeyContext)!!.toList() == VCLSubmission.ValueContextList)

        val pushDelegateBodyJsonObj = requestBodyJsonObj.optJSONObject(VCLSubmission.KeyPushDelegate)!!

        assert(pushDelegateBodyJsonObj.optString(VCLPushDelegate.KeyPushUrl) == PresentationSubmissionMocks.PushDelegate.pushUrl)
        assert(pushDelegateBodyJsonObj.optString(VCLPushDelegate.KeyPushToken) == PresentationSubmissionMocks.PushDelegate.pushToken)

        assert(pushDelegateBodyJsonObj.optString(VCLPushDelegate.KeyPushUrl) == subjectPresentationSubmission.pushDelegate!!.pushUrl)
        assert(pushDelegateBodyJsonObj.optString(VCLPushDelegate.KeyPushToken) == subjectPresentationSubmission.pushDelegate!!.pushToken)
    }

    @Test
    fun testContext() {
        assert(VCLSubmission.KeyContext == "@context")
        assert(VCLSubmission.ValueContextList == listOf("https://www.w3.org/2018/credentials/v1"))
    }
}