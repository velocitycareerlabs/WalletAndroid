/**
 * Created by Michael Avoyan on 10/04/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.VCLAuthToken
import io.velocitycareerlabs.api.entities.VCLAuthTokenDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.AuthTokenRepository
import io.velocitycareerlabs.impl.extensions.toJsonObject

internal class AuthTokenRepositoryImpl(
    private val networkService: NetworkService
): AuthTokenRepository {
    override fun getAuthToken(
        authTokenDescriptor: VCLAuthTokenDescriptor,
        completionBlock: (VCLResult<VCLAuthToken>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = authTokenDescriptor.authTokenUri,
            headers = listOf(
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)
            ),
            body = authTokenDescriptor.generateRequestBody().toString(),
            method = Request.HttpMethod.POST,
            contentType = Request.ContentTypeApplicationJson,
            completionBlock = { result ->
                result.handleResult(
                    { authTokenResponse ->
                        try {
                            authTokenResponse.payload.toJsonObject()?.let { payload ->
                                completionBlock(
                                    VCLResult.Success(
                                        VCLAuthToken(
                                            payload,
                                            authTokenDescriptor.authTokenUri,
                                            authTokenDescriptor.walletDid,
                                            authTokenDescriptor.relyingPartyDid
                                        )
                                    )
                                )
                            } ?: run {
                                completionBlock(
                                    VCLResult.Failure(
                                        VCLError("Failed to parse: auth token response")
                                    )
                                )
                            }
                        } catch (ex: Exception) {
                            completionBlock(VCLResult.Failure(VCLError(ex)))
                        }
                    },
                    { error ->
                        completionBlock(VCLResult.Failure(error))
                    }
                )
            }
        )
    }
}