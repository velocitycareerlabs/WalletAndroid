/**
 * Created by Michael Avoyan on 4/5/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.repositories.PresentationRequestRepository
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.utils.ErrorTaxonomy
import org.json.JSONObject

internal class PresentationRequestRepositoryImpl(
    private val networkService: NetworkService
): PresentationRequestRepository {
    val TAG = PresentationRequestRepositoryImpl::class.simpleName

    override fun getPresentationRequest(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        completionBlock: (VCLResult<String>) -> Unit
    ) {
        val endpoint = presentationRequestDescriptor.endpoint.orEmpty()
        networkService.sendRequest(
            endpoint = endpoint,
            contentType = Request.ContentTypeApplicationJson,
            method = Request.HttpMethod.GET,
            headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)),
            completionBlock = { encodedJwtResult ->
                encodedJwtResult.handleResult({ presentationRequestResponse ->
                    val encodedJwtStr = runCatching {
                        JSONObject(presentationRequestResponse.payload)
                            .optString(VCLPresentationRequest.KeyPresentationRequest)
                    }.getOrDefault("")
                    if (encodedJwtStr.isBlank()) {
                        completionBlock(
                            VCLResult.Failure(
                                ErrorTaxonomy.toRequestValidationError(
                                    VCLError(message = "Missing presentation_request"),
                                    requestKind = ErrorTaxonomy.RequestKindPresentation,
                                    requestDid = presentationRequestDescriptor.did,
                                    requestUri = endpoint,
                                )
                            )
                        )
                    } else {
                        completionBlock(VCLResult.Success(encodedJwtStr))
                    }
                }, {
                    completionBlock(
                        VCLResult.Failure(
                            ErrorTaxonomy.toClientRequestFetchError(
                                it,
                                requestUri = endpoint,
                                requestKind = ErrorTaxonomy.RequestKindPresentation,
                            )
                        )
                    )
                })
            }
        )
    }
}
