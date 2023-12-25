/**
 * Created by Michael Avoyan on 24/12/2023.
 *
 *  Copyright 2022 Velocity Career Labs inc.
 *  SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.infrastructure.resources.valid.TokenMocks
import org.junit.Test

class VCLTokenTest {
    lateinit var subject: VCLToken

    @Test
    fun testToken1() {
        subject = VCLToken(value = TokenMocks.TokenStr)

        assert(subject.value == TokenMocks.TokenStr)
        assert(subject.jwtValue.encodedJwt == TokenMocks.TokenStr)
        assert(subject.expiresIn == 1704020514L)
    }

    @Test
    fun testToken2() {
        subject = VCLToken(jwtValue = TokenMocks.TokenJwt)

        assert(subject.value == TokenMocks.TokenJwt.encodedJwt)
        assert(subject.jwtValue.encodedJwt == TokenMocks.TokenJwt.encodedJwt)
        assert(subject.expiresIn == 1704020514L)
    }
}