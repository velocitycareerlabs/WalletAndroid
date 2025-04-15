/**
 * Created by Michael Avoyan on 02/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.jwt.remote

import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.api.jwt.VCLJwtVerifyService
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.repositories.HeaderKeys
import io.velocitycareerlabs.impl.data.repositories.HeaderValues
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject

internal class VCLJwtVerifyServiceRemoteImpl(
    private val networkService: NetworkService,
    private val jwtVerifyServiceUrl: String
): VCLJwtVerifyService {
    override fun verify(
        jwt: VCLJwt,
        publicJwk: VCLPublicJwk,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = jwtVerifyServiceUrl,
            body = generatePayloadToVerify(jwt, publicJwk).toString(),
            contentType = Request.ContentTypeApplicationJson,
            method = Request.HttpMethod.POST,
            headers = listOf(
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion),
                Pair(HeaderKeys.Authorization, "${HeaderValues.PrefixBearer} ${remoteCryptoServicesToken?.value}")
            ),
            completionBlock = { verifiedJwtResult ->
                verifiedJwtResult.handleResult(
                    successHandler = {
                        val payloadJson = it.payload.toJsonObject()
                        val isVerified = payloadJson?.optBoolean(KeyVerified) == true
                        completionBlock(VCLResult.Success(isVerified))
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

    companion object CodingKeys {
        const val KeyJwt = "jwt"
        const val KeyVerified = "verified"

        const val KeyPublicKey = "publicKey"
    }
}