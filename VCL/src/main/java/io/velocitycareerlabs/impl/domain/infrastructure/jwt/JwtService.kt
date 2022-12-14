/**
 * Created by Michael Avoyan on 4/28/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.infrastructure.jwt

import com.nimbusds.jose.*
import com.nimbusds.jwt.SignedJWT
import io.velocitycareerlabs.api.entities.*
import java.text.ParseException

internal interface JwtService {
    @Throws(ParseException::class)
    fun parse(jwt: String): SignedJWT?

    fun encode(str: String): String

    @Throws(JOSEException::class)
    fun verify(jwt: VCLJwt, jwk: String): Boolean

    fun sign(jwtDescriptor: VCLJwtDescriptor): SignedJWT?

    fun generateDidJwk(): VCLDidJwk
}