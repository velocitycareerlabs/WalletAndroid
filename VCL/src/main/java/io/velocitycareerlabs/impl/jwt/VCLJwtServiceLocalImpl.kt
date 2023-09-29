/**
 * Created by Michael Avoyan on 3/25/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.jwt

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.crypto.ECDSAVerifier
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.api.jwt.VCLJwtService
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.extensions.addClaims
import io.velocitycareerlabs.impl.extensions.addDays
import io.velocitycareerlabs.impl.extensions.randomString
import java.lang.Exception
import java.util.*

internal class VCLJwtServiceLocalImpl(
    private val keyService: VCLKeyService
): VCLJwtService {
    override fun verify(
        jwt: VCLJwt,
        publicPublic: VCLPublicJwk,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        try {
            completionBlock(
                VCLResult.Success(
                    jwt.signedJwt?.verify(ECDSAVerifier(JWK.parse(publicPublic.valueStr).toECKey())) == true
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
                            .type(JOSEObjectType(GlobalConfig.TypeJwt))
                        kid?.let { header.keyID(it) } ?: run { header.jwk(ecKey.toPublicJWK()) }
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
        nonce?.let { claimsSetBuilder.claim(KeyNonce, it) }
        jwtDescriptor.payload?.let { claimsSetBuilder.addClaims(it) }

        return claimsSetBuilder.build()
    }

    companion object CodingKeys {
        const val KeyIss = "iss"
        const val KeyAud = "aud"
        const val KeySub = "sub"
        const val KeyJti = "jti"
        const val KeyIat = "iat"
        const val KeyNbf = "nbf"
        const val KeyExp = "exp"
        const val KeyNonce = "nonce"
    }
}