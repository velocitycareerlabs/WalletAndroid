/**
 * Created by Michael Avoyan on 4/28/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.infrastructure.network

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.infrastructure.network.Response
import java.util.*

internal interface NetworkService {
    fun sendRequest(
        endpoint: String,
        body: String? = null,
        contentType: String = Request.ContentTypeApplicationJson,
        method: Request.HttpMethod,
        headers: List<Pair<String, String>>? = null,
        useCaches: Boolean = true,
        completionBlock: (VCLResult<Response>) -> Unit
    )
}