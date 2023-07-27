/**
 * Created by Michael Avoyan on 20/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.impl.extensions.encodeToBase64URL
import org.junit.Test

class VCLJwtTest {

    lateinit var subject: VCLJwt
    private val jwtStr = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
    private val expectedHeader = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}"
    private val expectedPayload = "{\"sub\":\"1234567890\",\"name\":\"John Doe\",\"iat\":1516239022}"

    @Test
    fun testEmptyJwt() {
        subject = VCLJwt(encodedJwt = "")

        assert(subject.header == null)
        assert(subject.payload == null)
        assert(subject.signature == null)
        assert(subject.encodedJwt == null)
    }

    @Test
    fun testJwt() {
        subject = VCLJwt(encodedJwt = jwtStr)

        assert(subject.header.toString().toCharArray().sort() == expectedHeader.toCharArray().sort())
        assert(subject.payload.toString().toCharArray().sort() == expectedPayload.toCharArray().sort())
        assert(subject.signature.toString() == "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        assert(subject.encodedJwt == jwtStr)
    }
}