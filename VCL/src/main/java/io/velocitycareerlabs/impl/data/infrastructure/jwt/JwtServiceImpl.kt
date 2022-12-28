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
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import com.nimbusds.jose.util.Base64URL.encode
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.jwt.JwtService
import io.velocitycareerlabs.impl.extensions.addClaims
import io.velocitycareerlabs.impl.extensions.addDaysToNow
import io.velocitycareerlabs.impl.extensions.encodeToBase64
import io.velocitycareerlabs.impl.extensions.randomString
import java.text.ParseException
import java.util.*

internal class JwtServiceImpl: JwtService {

    @Throws(ParseException::class)
    override fun parse(jwt: String): SignedJWT? = SignedJWT.parse(jwt)

    override fun encode(str: String): String = encode(str.toByteArray()).toString()

    @Throws(JOSEException::class)
    override fun verify(jwt: VCLJwt, jwk: String): Boolean =
        jwt.signedJwt.verify(ECDSAVerifier(JWK.parse(jwk).toECKey()))

    override fun sign(jwtDescriptor: VCLJwtDescriptor): SignedJWT? {
        try {
            val jwk: ECKey = generateJwkSECP256K1()

            val header = JWSHeader.Builder(JWSAlgorithm.ES256K).jwk(jwk.toPublicJWK())
                .type(JOSEObjectType("JWT")).build()

            val claimsSetBuilder = JWTClaimsSet.Builder()
//            OPTIONS:    https://dev.azure.com/velocitycareerlabs/velocity/_wiki/wikis/velocity.wiki/100/SDK?anchor=jwt-options-(jwt-options)
//            {
//                "audience": "did:velocity:0xc257274276a4e539741ca11b590b9447b26a8051", // presentation_request.iss
//                "issuer": "31903091301-12332-32111-000001",  // generate a uuid. will be the holder's DID in the future
//                "expiresIn": "P1W" // 1 week encoded using https://en.wikipedia.org/wiki/ISO_8601#Durations
//            }
                .audience(jwtDescriptor.iss)
                .issuer(jwtDescriptor.iss)
                .jwtID(jwtDescriptor.jti) // jti
                .issueTime(Date()) // iat
                .expirationTime(Date().addDaysToNow(7)) // nbf
                .subject(randomString(10))

            claimsSetBuilder.addClaims(jwtDescriptor.payload)

            val signedJWT = SignedJWT(header, claimsSetBuilder.build())

            val signer: JWSSigner = ECDSASigner(jwk)

            signedJWT.sign(signer)

            return signedJWT
        } catch (e: Exception) {
            return null
        }
    }

    override fun generateDidJwk() =
        VCLDidJwk("${VCLDidJwk.DidJwkPrefix}${generateJwkPublic().valueStr.encodeToBase64()}")

//  https://connect2id.com/products/nimbus-jose-jwt/examples/jwk-generation
    private fun generateJwkPublic() =
        VCLJwkPublic(generateJwkSECP256K1().toPublicJWK().toJSONString().toString())

    private fun generateJwkSECP256K1() =
        ECKeyGenerator(Curve.SECP256K1)
        .keyUse(KeyUse.SIGNATURE) // indicate the intended use of the key
        .keyID(UUID.randomUUID().toString()) // give the key a unique ID
        .generate()
}