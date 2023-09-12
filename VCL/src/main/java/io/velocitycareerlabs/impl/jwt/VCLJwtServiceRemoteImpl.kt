/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 *  Copyright 2022 Velocity Career Labs inc.
 *  SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.jwt

import io.velocitycareerlabs.api.entities.VCLJwkPublic
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.api.entities.initialization.VCLJwtServiceUrls
import io.velocitycareerlabs.api.jwt.VCLJwtService
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.repositories.HeaderKeys
import io.velocitycareerlabs.impl.data.repositories.HeaderValues
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.extensions.randomString
import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject
import java.util.Date

internal class VCLJwtServiceRemoteImpl(
    private val networkService: NetworkService,
    private val jwtServiceUrls: VCLJwtServiceUrls
) : VCLJwtService {
    override fun verify(
        jwt: VCLJwt,
        jwkPublic: VCLJwkPublic,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = jwtServiceUrls.jwtVerifyServiceUrl,
            body = generatePayloadToVerify(jwt, jwkPublic).toString(),
            contentType = Request.ContentTypeApplicationJson,
            method = Request.HttpMethod.POST,
            headers = listOf(
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)
            ),
            completionBlock = { verifiedJwtResult ->
                verifiedJwtResult.handleResult(
                    successHandler = {
                        val payloadJson = it.payload.toJsonObject()
                        val isVerified = payloadJson?.optBoolean(CodingKeys.KeyVerified) == true
                        completionBlock(VCLResult.Success(isVerified))
                    },
                    errorHandler = { error ->
                        completionBlock(VCLResult.Failure(error))
                    }
                )
            }
        )
    }

    override fun sign(
        kid: String?,
        nonce: String?,
        jwtDescriptor: VCLJwtDescriptor,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = jwtServiceUrls.jwtSignServiceUrl,
            body = generateJwtPayloadToSign(nonce, jwtDescriptor).toString(),
            contentType = Request.ContentTypeApplicationJson,
            method = Request.HttpMethod.POST,
            headers = listOf(
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)
            ),
            completionBlock = { verifiedJwtResult ->
                verifiedJwtResult.handleResult(
                    successHandler = {
                        it.payload.toJsonObject()?.getString(KeyJwt)?.let { jwtStr ->
                            completionBlock(VCLResult.Success(VCLJwt(jwtStr)))
                        } ?: run {
                            completionBlock(VCLResult.Failure(
                                VCLError(payload = "Failed to parse data from ${jwtServiceUrls.jwtVerifyServiceUrl}"))
                            )
                        }
                    },
                    errorHandler = { error ->
                        completionBlock(VCLResult.Failure(error))
                    }
                )
            }
        )
    }

    private fun generatePayloadToVerify(
        jwt: VCLJwt,
        jwkPublic: VCLJwkPublic
    ): JSONObject {
        val retVal = JSONObject()
        retVal.putOpt(KeyJwt, jwt.encodedJwt)
        retVal.putOpt(KeyPublicKey, jwkPublic.valueJson)
        return retVal
    }

    private fun generateJwtPayloadToSign(
        nonce: String? = null,
        jwtDescriptor: VCLJwtDescriptor
    ): JSONObject {
        val retVal = JSONObject()
        val options = JSONObject()

        options.putOpt(CodingKeys.KeyAudience, jwtDescriptor.aud)
        options.putOpt(CodingKeys.KeyJti, jwtDescriptor.jti)
        val date = Date()
//        options[JwtServiceCodingKeys.KeyIssuedAt] = date.toDouble()
//        options[JwtServiceCodingKeys.KeyNotBefore] = date.toDouble()
//        options[JwtServiceCodingKeys.KeyExpiresIn] = date.addDays(days: 7).toDouble()
        options.putOpt(CodingKeys.KeyNonce, nonce)

        options.putOpt(CodingKeys.KeyIssuer, jwtDescriptor.iss)
        options.putOpt(CodingKeys.KeySubject, randomString(10))

        retVal.putOpt(CodingKeys.KeyPayload, jwtDescriptor.payload)
        retVal.putOpt(CodingKeys.KeyOptions, options)

        return retVal
    }

    companion object CodingKeys {
        val KeyKid = "kid"

        val KeyIssuer = "issuer"
        val KeyAudience = "audience"
        val KeySubject = "subject"
        val KeyJti = "jti"

//        val KeyIssuedAt = "issuedAt"
//        val KeyNotBefore = "notBefore"
//        val KeyExpiresIn = "expiresIn"
        val KeyNonce = "nonce"

        val KeyPayload = "payload"
        val KeyJwt = "jwt"
        val KeyPublicKey = "publicKey"

        val KeyOptions = "options"
        val KeyRequired = "required"

        val KeyVerified = "verified"
    }
}