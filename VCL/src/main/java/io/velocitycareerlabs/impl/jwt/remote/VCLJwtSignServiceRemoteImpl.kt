/**
 * Created by Michael Avoyan on 02/10/2023.
 *
 *  Copyright 2022 Velocity Career Labs inc.
 *  SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.jwt.remote

import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.api.jwt.VCLJwtSignService
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.repositories.HeaderKeys
import io.velocitycareerlabs.impl.data.repositories.HeaderValues
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.extensions.copy
import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject

internal class VCLJwtSignServiceRemoteImpl(
    private val networkService: NetworkService,
    private val jwtSignServiceUrl: String
): VCLJwtSignService {
    override fun sign(
        kid: String?,
        nonce: String?,
        jwtDescriptor: VCLJwtDescriptor,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLJwt>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = jwtSignServiceUrl,
            body = generateJwtPayloadToSign(kid, nonce, jwtDescriptor).toString(),
            contentType = Request.ContentTypeApplicationJson,
            method = Request.HttpMethod.POST,
            headers = listOf(
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion),
                Pair(HeaderKeys.Authorization, "${HeaderKeys.Bearer} ${remoteCryptoServicesToken?.value}")
            ),
            completionBlock = { signedJwtResult ->
                signedJwtResult.handleResult(
                    successHandler = {
                        it.payload.toJsonObject()?.optString(KeyCompactJwt)?.let { jwtStr ->
                            completionBlock(VCLResult.Success(VCLJwt(jwtStr)))
                        } ?: run {
                            completionBlock(
                                VCLResult.Failure(
                                VCLError(payload = "Failed to parse data from $jwtSignServiceUrl")
                                )
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

    private fun generateJwtPayloadToSign(
        kid: String?,
        nonce: String?,
        jwtDescriptor: VCLJwtDescriptor
    ): JSONObject {
        val retVal = JSONObject()
        val header = JSONObject()
        val options = JSONObject()
        val payload = jwtDescriptor.payload?.copy() ?: JSONObject()

//        Base assumption:
//        HeaderValues.XVnfProtocolVersion == VCLXVnfProtocolVersion.XVnfProtocolVersion2
        header.putOpt(KeyKid, kid)

        options.putOpt(KeyKeyId, jwtDescriptor.keyId)
        options.putOpt(KeyAud, jwtDescriptor.aud)
        options.putOpt(KeyJti, jwtDescriptor.jti)
        options.putOpt(KeyIss, jwtDescriptor.iss)
        options.putOpt(KeyIss, jwtDescriptor.iss)

        payload.putOpt(KeyNonce, nonce)

        retVal.putOpt(KeyHeader, header)
        retVal.putOpt(KeyOptions, options)
        retVal.putOpt(KeyPayload, payload)

        return retVal
    }

    companion object CodingKeys {
        const val KeyKeyId = "keyId"
        const val KeyKid = "kid"
        const val KeyIss = "iss"
        const val KeyAud = "aud"
        const val KeyJti = "jti"
        const val KeyNonce = "nonce"

        const val KeyHeader = "header"
        const val KeyOptions = "options"
        const val KeyPayload = "payload"

        const val KeyCompactJwt = "compactJwt"
    }
}