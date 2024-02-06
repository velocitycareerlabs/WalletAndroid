/**
 * Created by Michael Avoyan on 02/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.jwt.local

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.api.jwt.VCLJwtSignService
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.extensions.addClaims
import io.velocitycareerlabs.impl.extensions.addDays
import io.velocitycareerlabs.impl.extensions.randomString
import java.lang.Exception
import java.util.Date

class VCLJwtSignServiceLocalImpl(
    private val keyService: VCLKeyService
): VCLJwtSignService {
    override fun sign(
        didJwk: VCLDidJwk,
        nonce: String?,
        jwtDescriptor: VCLJwtDescriptor,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        getSecretReference(didJwk.keyId) { ecKeyResult ->
            ecKeyResult.handleResult(
                successHandler = { ecKey ->
                    try {
                        val header = JWSHeader.Builder(JWSAlgorithm.ES256K)
                            .type(JOSEObjectType(GlobalConfig.TypeJwt))
                            .jwk(ecKey.toPublicJWK()) // always provide
                            .keyID(didJwk.kid) // always provide
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