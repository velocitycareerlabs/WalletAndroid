/**
 * Created by Michael Avoyan on 26/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import org.json.JSONObject

data class VCLJwtDescriptor(
    val payload: JSONObject,
    val iss: String,
    val jti:String
)
