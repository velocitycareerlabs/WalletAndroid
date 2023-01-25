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
import org.json.JSONObject
import java.lang.Exception

internal class VerifiedProfileRepositoryImpl(
    private val networkService: NetworkService
): VerifiedProfileRepository {
    override fun getVerifiedProfile(
        verifiedProfileDescriptor: VCLVerifiedProfileDescriptor,
        completionBlock: (VCLResult<VCLVerifiedProfile>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = Urls.VerifiedProfile.replace(Params.Did, verifiedProfileDescriptor.did),
            method = Request.HttpMethod.GET,
            headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderKValues.XVnfProtocolVersion)),
            completionBlock = { result ->
                result.handleResult(
                    { verifiedProfileResponse ->
                        try {
                            verifyServiceType(
                                verifiedProfilePayload = verifiedProfileResponse.payload,
                                expectedServiceType = verifiedProfileDescriptor.serviceType,
                                completionBlock = completionBlock
                            )
                        } catch (ex: Exception){
                            completionBlock(VCLResult.Failure(VCLError(ex.message)))
                        }
                    },
                    { error ->
                        completionBlock(VCLResult.Failure(error))
                    }
                )
            }
        )
    }

    private fun verifyServiceType(
        verifiedProfilePayload: String,
        expectedServiceType: VCLServiceType?,
        completionBlock: (VCLResult<VCLVerifiedProfile>) -> Unit
    ) {
        val verifiedProfile = VCLVerifiedProfile(JSONObject(verifiedProfilePayload))
        expectedServiceType?.let {
            if (verifiedProfile.serviceTypes.contains(it))
                completionBlock(VCLResult.Success(verifiedProfile))
            else
                completionBlock(VCLResult.Failure(VCLError(
                    "Wrong service type - expected: ${it.value}, found: ${verifiedProfile.serviceTypes.all}",
                    VCLErrorCode.VerificationError
                )))
        } ?: run {
            completionBlock(VCLResult.Success(verifiedProfile))
        }
    }
}