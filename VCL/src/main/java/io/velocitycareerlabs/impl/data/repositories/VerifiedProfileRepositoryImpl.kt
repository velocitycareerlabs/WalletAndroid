/**
 * Created by Michael Avoyan on 3/13/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.repositories.VerifiedProfileRepository
import io.velocitycareerlabs.impl.utils.VCLLog
import org.json.JSONObject
import java.lang.Exception

internal class VerifiedProfileRepositoryImpl(
    private val networkService: NetworkService
): VerifiedProfileRepository {
    private val TAG = VerifiedProfileRepositoryImpl::class.simpleName
    override fun getVerifiedProfile(
        verifiedProfileDescriptor: VCLVerifiedProfileDescriptor,
        completionBlock: (VCLResult<VCLVerifiedProfile>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = Urls.VerifiedProfile.replace(Params.Did, verifiedProfileDescriptor.did),
            method = Request.HttpMethod.GET,
            headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)),
            completionBlock = { result ->
                result.handleResult(
                    { verifiedProfileResponse ->
                        try {
                            completionBlock(VCLResult.Success(VCLVerifiedProfile(JSONObject(verifiedProfileResponse.payload))))
                        } catch (ex: Exception){
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