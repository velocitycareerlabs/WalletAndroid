/**
 * Created by Michael Avoyan on 4/28/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.network

import io.velocitycareerlabs.api.entities.VCLError
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.infrastructure.network.Response
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService

internal class NetworkServiceFailure(
    private val errorMessage: String
): NetworkService {

    override fun sendRequest(
        endpoint: String,
        body: String?,
        contentType: String,
        method: Request.HttpMethod,
        headers: List<Pair<String, String>>?,
        useCaches: Boolean,
        completionBlock: (VCLResult<Response>) -> Unit
    ) {
        completionBlock(VCLResult.Failure(VCLError(errorMessage)))
    }
}