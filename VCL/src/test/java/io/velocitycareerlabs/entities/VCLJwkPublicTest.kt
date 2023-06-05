/**
 * Created by Michael Avoyan on 27/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLJwkPublic
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.resources.valid.KeyServiceMocks
import org.junit.Test

internal class VCLJwkPublicTest {

    lateinit var subject: VCLJwkPublic

    companion object {
        val jwkJson = KeyServiceMocks.JWK.toJsonObject()
    }

    @Test
    fun testJwkPublicFromStr() {
        subject = VCLJwkPublic(valueStr = KeyServiceMocks.JWK)

        assert(subject.valueStr == KeyServiceMocks.JWK)
    }
    @Test
    fun testJwkPublicFromJson() {
        subject = VCLJwkPublic(valueJson = jwkJson!!)

        assert(subject.valueJson == jwkJson)
    }
}