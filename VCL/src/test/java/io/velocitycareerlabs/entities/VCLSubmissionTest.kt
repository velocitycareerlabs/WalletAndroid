/**
 * Created by Michael Avoyan on 11/12/2022.
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLPresentationSubmission
import io.velocitycareerlabs.api.entities.VCLPushDelegate
import io.velocitycareerlabs.api.entities.VCLSubmission
import io.velocitycareerlabs.impl.extensions.toList
import io.velocitycareerlabs.infrastructure.resources.valid.JwtServiceMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationSubmissionMocks
import org.junit.Before
import org.junit.Test

class VCLSubmissionTest {
    internal lateinit var subject: VCLSubmission

    @Before
    fun setUp() {
        subject = VCLPresentationSubmission(
            PresentationSubmissionMocks.PresentationRequest,
            PresentationSubmissionMocks.SelectionsList
        )
    }

    @Test
    fun testPayload() {
        assert(subject.payload.optString(VCLSubmission.KeyJti) == subject.jti)
        assert(subject.payload.optString(VCLSubmission.KeyIss) == subject.iss)
    }

    @Test
    fun testPushDelegate() {
        assert(subject.pushDelegate!!.pushUrl == PresentationSubmissionMocks.PushDelegate.pushUrl)
        assert(subject.pushDelegate!!.pushToken == PresentationSubmissionMocks.PushDelegate.pushToken)
    }

    @Test
    fun testRequestBody() {
        val requestBodyJsonObj = subject.generateRequestBody(JwtServiceMocks.JWT)
        assert(requestBodyJsonObj.optString(VCLSubmission.KeyExchangeId) == subject.exchangeId)
        assert(requestBodyJsonObj.optJSONArray(VCLSubmission.KeyContext)!!.toList() == VCLSubmission.ValueContextList)

        val pushDelegateBodyJsonObj = requestBodyJsonObj.optJSONObject(VCLSubmission.KeyPushDelegate)!!

        assert(pushDelegateBodyJsonObj.optString(VCLPushDelegate.KeyPushUrl) == PresentationSubmissionMocks.PushDelegate.pushUrl)
        assert(pushDelegateBodyJsonObj.optString(VCLPushDelegate.KeyPushToken) == PresentationSubmissionMocks.PushDelegate.pushToken)

        assert(pushDelegateBodyJsonObj.optString(VCLPushDelegate.KeyPushUrl) == subject.pushDelegate!!.pushUrl)
        assert(pushDelegateBodyJsonObj.optString(VCLPushDelegate.KeyPushToken) == subject.pushDelegate!!.pushToken)
    }
}