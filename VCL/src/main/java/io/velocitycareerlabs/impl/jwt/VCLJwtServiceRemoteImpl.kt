/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 *  Copyright 2022 Velocity Career Labs inc.
 *  SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.jwt

import io.velocitycareerlabs.api.entities.VCLPublicJwk
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
import io.velocitycareerlabs.impl.extensions.addDays
import io.velocitycareerlabs.impl.extensions.copy
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
        publicPublic: VCLPublicJwk,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = jwtServiceUrls.jwtVerifyServiceUrl,
            body = generatePayloadToVerify(jwt, publicPublic).toString(),
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
            completionBlock = { signedJwtResult ->
                signedJwtResult.handleResult(
                    successHandler = {
                        it.payload.toJsonObject()?.optString(CodingKeys.KeyCompactJwt)?.let { jwtStr ->
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
        publicJwk: VCLPublicJwk
    ): JSONObject {
        val retVal = JSONObject()
        retVal.putOpt(KeyJwt, jwt.encodedJwt)
        retVal.putOpt(KeyPublicKey, publicJwk.valueJson)
        return retVal
    }

    private fun generateJwtPayloadToSign(
        nonce: String? = null,
        jwtDescriptor: VCLJwtDescriptor
    ): JSONObject {
        val retVal = JSONObject()
        val options = JSONObject()
        val payload = jwtDescriptor.payload?.copy() ?: JSONObject()

        options.putOpt(CodingKeys.KeyKeyId, jwtDescriptor.keyId)
        options.putOpt(CodingKeys.KeyAud, jwtDescriptor.aud)
        options.putOpt(CodingKeys.KeyJti, jwtDescriptor.jti)
        options.putOpt(CodingKeys.KeyIss, jwtDescriptor.iss)
        options.putOpt(CodingKeys.KeyIss, jwtDescriptor.iss)

        payload.putOpt(CodingKeys.KeyNonce, nonce)

        retVal.putOpt(CodingKeys.KeyOptions, options)
        retVal.putOpt(CodingKeys.KeyPayload, payload)

        return retVal
    }

    companion object CodingKeys {
        const val KeyKeyId = "keyId"
        const val KeyIss = "iss"
        const val KeyAud = "aud"
        const val KeyJti = "jti"
        const val KeyNonce = "nonce"

        const val KeyOptions = "options"
        const val KeyPayload = "payload"

        const val KeyJwt = "jwt"
        const val KeyCompactJwt = "compactJwt"
        const val KeyVerified = "verified"

        const val KeyPublicKey = "publicKey"
    }
}