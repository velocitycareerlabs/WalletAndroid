/**
 * Created by Michael Avoyan on 4/20/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.ResolveKidRepository

internal class ResolveKidRepositoryImpl(
        private val networkService: NetworkService
): ResolveKidRepository {

    override fun getPublicKey(keyID: String, completionBlock: (VCLResult<VCLPublicKey>) -> Unit) {
        networkService.sendRequest(
            endpoint = Urls.ResolveKid + keyID + "?format=${VCLPublicKey.Format.jwk}",
            method = Request.HttpMethod.GET,
            completionBlock = { result ->
                result.handleResult(
                    { publicKeyResponse ->
                        completionBlock(VCLResult.Success(VCLPublicKey(publicKeyResponse.payload)))
                    },
                    { error ->
                        completionBlock(VCLResult.Failure(error))
                    }
                )
            }
        )
    }
}