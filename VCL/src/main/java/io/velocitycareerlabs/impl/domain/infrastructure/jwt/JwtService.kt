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
    fun decode(jwt: String): VCLJwt

    fun encode(str: String): String

    @Throws(JOSEException::class)
    fun verify(
        jwt: VCLJwt,
        jwk: VCLJwkPublic
    ): Boolean

    fun sign(
        kid: String? = null,
        nonce: String? = null,
        jwtDescriptor: VCLJwtDescriptor
    ): VCLJwt
}