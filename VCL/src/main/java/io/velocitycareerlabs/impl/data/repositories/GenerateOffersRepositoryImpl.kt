/**
 * Created by Michael Avoyan on 10/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.GenerateOffersRepository
import java.lang.Exception

internal class GenerateOffersRepositoryImpl(
    private val networkService: NetworkService
): GenerateOffersRepository {
    private val TAG = GenerateOffersRepositoryImpl::class.simpleName

    override fun generateOffers(
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        sessionToken: VCLToken,
        completionBlock: (VCLResult<VCLOffers>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = generateOffersDescriptor.checkOffersUri,
            headers = listOf(
                Pair(HeaderKeys.Authorization, "${HeaderValues.PrefixBearer} ${sessionToken.value}"),
                Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)
            ),
            body = generateOffersDescriptor.payload.toString(),
            method = Request.HttpMethod.POST,
            contentType = Request.ContentTypeApplicationJson,
            completionBlock = { result ->
                result.handleResult(
                    { offersResponse ->
                        try {
                            completionBlock(
                                VCLResult.Success(
                                    VCLOffers.fromPayload(
                                        offersResponse.payload,
                                        offersResponse.code,
                                        sessionToken
                                    )
                                )
                            )
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