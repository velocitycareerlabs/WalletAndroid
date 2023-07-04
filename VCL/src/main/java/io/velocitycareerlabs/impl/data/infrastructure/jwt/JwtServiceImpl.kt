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
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.domain.infrastructure.jwt.JwtService
import io.velocitycareerlabs.impl.domain.infrastructure.keys.KeyService
import io.velocitycareerlabs.impl.extensions.addClaims
import io.velocitycareerlabs.impl.extensions.addDays
import io.velocitycareerlabs.impl.extensions.randomString
import java.lang.Exception
import java.util.*

internal class JwtServiceImpl(
    private val keyService: KeyService
): JwtService {
    override fun verify(
        jwt: VCLJwt,
        jwkPublic: VCLJwkPublic,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        try {
            completionBlock(
                VCLResult.Success(
                    jwt.signedJwt.verify(ECDSAVerifier(JWK.parse(jwkPublic.valueStr).toECKey()))
                )
            )
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }
    override fun sign(
        kid: String?,
        nonce: String?,
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        getSecretReference(jwtDescriptor.keyId) { ecKeyResult ->
            ecKeyResult.handleResult(
                successHandler = { ecKey ->
                    try {
                        val header = JWSHeader.Builder(JWSAlgorithm.ES256K)
                            .jwk(ecKey.toPublicJWK())
                            .type(JOSEObjectType(GlobalConfig.TypeJwt))
//                        kid?.let { header.keyID(it) } ?: run { header.jwk(ecKey.toPublicJWK()) }
                        kid?.let { header.keyID(it) }
                        val jwtHeader = header.build()

                        val signedJWT = SignedJWT(
                            jwtHeader,
                            generateClaims(nonce, jwtDescriptor)
                        )
                        signedJWT.sign(ECDSASigner(ecKey))

                        completionBlock(VCLResult.Success(VCLJwt(signedJWT)))
                    } catch (ex: Exception) {
                        completionBlock(VCLResult.Failure(VCLError(ex)))
                    }
                },
                errorHandler = { error ->
                    completionBlock(VCLResult.Failure(error))
                }
            )
        }
    }

    private fun getSecretReference(
        keyId: String?,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
        keyId?.let {
            keyService.retrieveSecretReference(keyId = it, completionBlock = completionBlock)
        } ?: run {
            keyService.generateSecret(completionBlock = completionBlock)
        }
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