/**
 * Created by Michael Avoyan on 4/28/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.jwt

import com.nimbusds.jose.*
import io.velocitycareerlabs.api.entities.*

interface VCLJwtService {
    @Throws(JOSEException::class)
    fun verify(
        jwt: VCLJwt,
        jwkPublic: VCLJwkPublic,
        completionBlock: (VCLResult<Boolean>) -> Unit
    )

    fun sign(
        kid: String? = null,
        nonce: String? = null,
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    )

    companion object CodingKeys {
        val KeyIss = "iss"
        val KeyAud = "aud"
        val KeySub = "sub"
        val KeyJti = "jti"
        val KeyIat = "iat"
        val KeyNbf = "nbf"
        val KeyExp = "exp"
        val KeyNonce = "nonce"

        val KeyPayload = "payload"
        val KeyJwt = "jwt"
        val KeyPublicKey = "publicKey"
    }
}