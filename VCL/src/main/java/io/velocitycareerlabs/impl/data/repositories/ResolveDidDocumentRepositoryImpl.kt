/**
 * Created by Michael Avoyan on 03/06/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.ResolveDidDocumentRepository

internal class ResolveDidDocumentRepositoryImpl(
    private val networkService: NetworkService
): ResolveDidDocumentRepository {
    override fun resolveDidDocument(
        did: String,
        completionBlock: (VCLResult<VCLDidDocument>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = Urls.ResolveDid + did,
            method = Request.HttpMethod.GET,
            headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)),
            completionBlock = { result ->
                result.handleResult(
                    { didDocumentResponse ->
                        completionBlock(VCLResult.Success(
                            VCLDidDocument(didDocumentResponse.payload))
                        )
                    },
                    { error ->
                        completionBlock(VCLResult.Failure(error))
                    }
                )
            }
        )
    }
}