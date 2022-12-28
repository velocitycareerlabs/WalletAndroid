/**
 * Created by Michael Avoyan on 27/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLJwkPublic
import io.velocitycareerlabs.infrastructure.resources.valid.JwtServiceMocks
import org.json.JSONObject
import org.junit.Test

internal class VCLJwkPublicTest {

    lateinit var subject: VCLJwkPublic

    companion object {
        val jwkJson = JSONObject(JwtServiceMocks.JWK)
    }

    @Test
    fun testJwkPublicFromStr() {
        subject = VCLJwkPublic(valueStr = JwtServiceMocks.JWK)

        assert(subject.valueStr == JwtServiceMocks.JWK)
    }

    @Test
    fun testJwkPublicFromJson() {
        subject = VCLJwkPublic(valueJson = jwkJson)

        assert(subject.valueJson == jwkJson)
    }
}