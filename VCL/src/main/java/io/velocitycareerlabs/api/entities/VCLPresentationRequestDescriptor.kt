/**
 * Created by Michael Avoyan on 20/11/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.appendQueryParams
import io.velocitycareerlabs.impl.extensions.encode
import java.net.URI

class VCLPresentationRequestDescriptor(
    val deepLink: VCLDeepLink,
    val pushDelegate: VCLPushDelegate? = null
) {
    companion object CodingKeys {
        const val KeyId = "id"

        const val KeyPushDelegatePushUrl = "push_delegate.push_url"
        const val KeyPushDelegatePushToken = "push_delegate.push_token"
    }

    val endpoint get() = generateQueryParams()?.let { queryParams ->
        deepLink.requestUri.appendQueryParams(queryParams)
    } ?: deepLink.requestUri

    private fun generateQueryParams(): String? {
        val pPushDelegate = pushDelegate?.let {
            "${KeyPushDelegatePushUrl}=${it.pushUrl.encode()}&" + "${KeyPushDelegatePushToken}=${it.pushToken}"
        }
        val qParams = listOfNotNull(pPushDelegate).filter { it.isNotBlank() }
        return if(qParams.isNotEmpty()) qParams.joinToString("&") else null
    }
}