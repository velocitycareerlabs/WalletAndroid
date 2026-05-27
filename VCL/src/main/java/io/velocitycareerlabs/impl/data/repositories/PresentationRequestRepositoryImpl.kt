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
import java.lang.Exception

internal class PresentationRequestRepositoryImpl(
    private val networkService: NetworkService
): PresentationRequestRepository {
    val TAG = PresentationRequestRepositoryImpl::class.simpleName

    override fun getPresentationRequest(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        completionBlock: (VCLResult<String>) -> Unit
    ) {
        presentationRequestDescriptor.endpoint?.let { endpoint ->
            networkService.sendRequest(
                endpoint = endpoint,
                contentType = Request.ContentTypeApplicationJson,
                method = Request.HttpMethod.GET,
                headers = listOf(Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)),
                completionBlock = { encodedJwtResult ->
                    encodedJwtResult.handleResult({ presentationRequestResponse ->
                        try {
                            val encodedJwtStr = JSONObject(presentationRequestResponse.payload)
                                .optString(VCLPresentationRequest.KeyPresentationRequest)
                            if (encodedJwtStr.isBlank()) {
                                completionBlock(
                                    VCLResult.Failure(
                                        ErrorTaxonomy.toClientRequestFetchError(
                                            VCLError(message = "Missing presentation_request"),
                                            requestUri = endpoint,
                                            requestKind = ErrorTaxonomy.RequestKindPresentation,
                                        )
                                    )
                                )
                            } else {
                                completionBlock(VCLResult.Success(encodedJwtStr))
                            }
                        } catch (ex: Exception) {
                            completionBlock(
                                VCLResult.Failure(
                                    ErrorTaxonomy.toClientRequestFetchError(
                                        VCLError(ex),
                                        requestUri = endpoint,
                                        requestKind = ErrorTaxonomy.RequestKindPresentation,
                                    )
                                )
                            )
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
        } ?: run {
            completionBlock(
                VCLResult.Failure(
                    VCLError(message = "presentationRequestDescriptor.endpoint = null")
                )
            )
        }
    }
}
