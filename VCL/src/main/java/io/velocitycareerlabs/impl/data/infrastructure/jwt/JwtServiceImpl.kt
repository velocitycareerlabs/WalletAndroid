/**
 * Created by Michael Avoyan on 3/25/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.infrastructure.jwt

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.crypto.ECDSAVerifier
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.util.Base64URL.encode
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.domain.infrastructure.jwt.JwtService
import io.velocitycareerlabs.impl.domain.infrastructure.keys.KeyService
import io.velocitycareerlabs.impl.extensions.addClaims
import io.velocitycareerlabs.impl.extensions.addDays
import io.velocitycareerlabs.impl.extensions.randomString
import java.text.ParseException
import java.util.*

internal class JwtServiceImpl(
    private val keyService: KeyService
): JwtService {

    @Throws(ParseException::class)
    override fun decode(jwt: String): VCLJwt = VCLJwt(SignedJWT.parse(jwt))

    override fun encode(str: String): String = encode(str.toByteArray()).toString()

    @Throws(JOSEException::class)
    override fun verify(
        jwt: VCLJwt,
        jwk: VCLJwkPublic
    ): Boolean =
        jwt.signedJwt.verify(ECDSAVerifier(JWK.parse(jwk.valueStr).toECKey()))

    override fun sign(
        kid: String?,
        nonce: String?,
        jwtDescriptor: VCLJwtDescriptor
    ): VCLJwt {
        val ecKey = jwtDescriptor.keyId?.let {
                keyId -> keyService.retrieveKey(keyId)
        } ?: run {
            keyService.generateKey()
        }

        val header = JWSHeader.Builder(JWSAlgorithm.ES256K)
            .jwk(keyService.retrievePublicJwk(ecKey))
            .type(JOSEObjectType(GlobalConfig.TypeJwt))
        kid?.let { header.keyID(it) }
        val jwtHeader = header.build()

        val signedJWT = SignedJWT(
            jwtHeader,
            generateClaims(nonce, jwtDescriptor)
        )
        val signer: JWSSigner = ECDSASigner(ecKey)
        signedJWT.sign(signer)

        return VCLJwt(signedJWT)
    }

    private fun generateClaims(
        nonce: String?,
        jwtDescriptor: VCLJwtDescriptor
    ): JWTClaimsSet {
        val curDate = Date()
        val claimsSetBuilder = JWTClaimsSet.Builder()
            .audience(jwtDescriptor.aud)
            .issuer(jwtDescriptor.iss)
            .jwtID(jwtDescriptor.jti) // jti
            .issueTime(curDate) // iat
            .notBeforeTime(curDate) // nbf
            .expirationTime(curDate.addDays(7)) // exp
            .subject(randomString(10))
        nonce?.let { claimsSetBuilder.claim("nonce", it) }
        jwtDescriptor.payload?.let { claimsSetBuilder.addClaims(it) }

        return claimsSetBuilder.build()
    }

    companion object CodingKeys {
        val KeyIss = "iss"
        val KeyAud = "aud"
        val KeySub = "sub"
        val KeyJti = "jti"
        val KeyIat = "iat"
        val KeyNbf = "nbf"
        val KeyExp = "exp"
        val KeyNonce = "nonce"
    }
}