/**
 * Created by Michael Avoyan on 4/26/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.Payload
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import io.velocitycareerlabs.impl.utils.VCLLog
import java.lang.Exception

class VCLJwt {
    internal var signedJwt: SignedJWT? = null

    constructor(signedJwt: SignedJWT) {
        this.signedJwt = signedJwt
    }

    constructor(encodedJwt: String) {
        try {
            val encodedJwtArr = encodedJwt.split(".")
            this.signedJwt = SignedJWT(
                Base64URL(if (encodedJwtArr.isNotEmpty()) encodedJwtArr.component1() else ""),
                Base64URL(if (encodedJwtArr.size >= 2) encodedJwtArr.component2() else ""),
                Base64URL(if (encodedJwtArr.size >= 3) encodedJwtArr.component3() else "")
            )
        } catch (ex: Exception) {
            VCLLog.e("", ex.toString())
        }
    }

    val header: JWSHeader? get() = signedJwt?.header
    val payload: Payload? get() = signedJwt?.payload
    val signature: Base64URL? get() = signedJwt?.signature
    val encodedJwt: String? get() = signedJwt?.serialize()

    companion object CodingKeys {
        const val KeyTyp = "typ"
        const val KeyAlg = "alg"
        const val KeyKid = "kid"
        const val KeyJwk = "jwk"

        const val KeyX = "x"
        const val KeyY = "y"

        const val KeyHeader = "header"
        const val KeyPayload = "payload"
        const val KeySignature = "signature"

        const val KeyIss = "iss"
        const val KeyAud = "aud"
        const val KeySub = "sub"
        const val KeyJti = "jti"
        const val KeyIat = "iat"
        const val KeyNbf = "nbf"
        const val KeyExp = "exp"
        const val KeyNonce = "nonce"
    }

    val kid: String?
        get() = header?.keyID
            ?: ((header?.toJSONObject()?.getOrDefault(
                CodingKeys.KeyJwk,
                null
            )) as? Map<*, *>)?.getOrDefault(CodingKeys.KeyKid, null) as? String
    val iss: String?
        get() = this.payload?.toJSONObject()?.getOrDefault(CodingKeys.KeyIss, null) as? String
    val aud: String?
        get() = this.payload?.toJSONObject()?.getOrDefault(CodingKeys.KeyAud, null) as? String
    val sub: String?
        get() = this.payload?.toJSONObject()?.getOrDefault(CodingKeys.KeySub, null) as? String
    val jti: String?
        get() = this.payload?.toJSONObject()?.getOrDefault(CodingKeys.KeyJti, null) as? String
    val iat: String?
        get() = this.payload?.toJSONObject()?.getOrDefault(CodingKeys.KeyIat, null) as? String
    val nbf: String?
        get() = this.payload?.toJSONObject()?.getOrDefault(CodingKeys.KeyNbf, null) as? String
    val exp: String?
        get() = this.payload?.toJSONObject()?.getOrDefault(CodingKeys.KeyExp, null) as? String
    val nonce: String?
        get() = this.payload?.toJSONObject()?.getOrDefault(CodingKeys.KeyNonce, null) as? String
}