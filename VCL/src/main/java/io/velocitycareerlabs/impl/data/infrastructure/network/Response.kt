/**
 * Created by Michael Avoyan on 1/11/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.infrastructure.network

internal data class Response(
    val payload: String,
    val code: Int
)