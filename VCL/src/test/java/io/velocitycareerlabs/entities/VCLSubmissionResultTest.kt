/**
 * Created by Michael Avoyan on 30/11/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLExchange
import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLSubmissionResult
import io.velocitycareerlabs.api.entities.VCLToken
import org.junit.Before
import org.junit.Test

class VCLSubmissionResultTest {
    internal lateinit var subject: VCLSubmissionResult

    @Before
    fun setUp() {
        subject = VCLSubmissionResult(
            VCLToken("token123"),
            VCLExchange(
                "id123",
                "type123",
                true,
                true
            ),
            "jti123",
            "submissionId123"
        )
    }

    @Test
    fun testProps() {
        assert(subject.token.value == "token123")
        assert(subject.exchange.id == "id123")
        assert(subject.exchange.type == "type123")
        assert(subject.exchange.exchangeComplete)
        assert(subject.exchange.disclosureComplete)
        assert(subject.jti == "jti123")
        assert(subject.submissionId == "submissionId123")
    }
}