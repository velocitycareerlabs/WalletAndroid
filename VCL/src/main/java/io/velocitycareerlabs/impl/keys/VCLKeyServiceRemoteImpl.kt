/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.keys

import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.api.entities.initialization.VCLKeyServiceUrls
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.repositories.HeaderKeys
import io.velocitycareerlabs.impl.data.repositories.HeaderValues
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject

internal class VCLKeyServiceRemoteImpl(
    private val networkService: NetworkService,
    private val keyServiceUrls: VCLKeyServiceUrls
) : VCLKeyService {
    override fun generateDidJwk(
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = keyServiceUrls.createDidKeyServiceUrl,
            body = generatePayloadToCreateDidJwk().toString(),
            contentType = Request.ContentTypeApplicationJson,
            method = Request.HttpMethod.POST,
            headers = listOf(
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion),
                Pair(HeaderKeys.Authorization, "${HeaderKeys.Bearer} ${remoteCryptoServicesToken?.value}")
            ),
            completionBlock = { didJwkResult ->
                didJwkResult.handleResult(
                    successHandler = {
                        it.payload.toJsonObject()?.let { didJwkJson ->
                            completionBlock(
                                VCLResult.Success(
                                    VCLDidJwk(
                                        did = didJwkJson.optString(CodingKeys.KeyDid),
                                        publicJwk = VCLPublicJwk(
                                            didJwkJson.optJSONObject(
                                                KeyPublicJwk
                                            ) ?: JSONObject()
                                        ),
                                        kid = didJwkJson.optString(CodingKeys.KeyKid),
                                        keyId = didJwkJson.optString(CodingKeys.KeyKeyId)
                                    )
                                )
                            )
                        } ?: run {
                            completionBlock(
                                VCLResult.Failure(
                                    VCLError("Failed to create did:jwk from the provided URL: ${keyServiceUrls.createDidKeyServiceUrl}")
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

    private fun generatePayloadToCreateDidJwk(): JSONObject {
        val retVal = JSONObject()
        retVal.putOpt(CodingKeys.KeyCrv, GlobalConfig.SignatureAlgorithm.curve)
        return retVal
    }

    companion object CodingKeys {
        const val KeyCrv = "crv"

        const val KeyDid = "did"
        const val KeyKid = "kid"
        const val KeyKeyId = "keyId"
        const val KeyPublicJwk = "publicJwk"
    }
}