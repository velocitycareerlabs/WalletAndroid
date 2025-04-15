/**
 * Created by Michael Avoyan on 09/04/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.extensions

import org.json.JSONObject

internal fun Map<*, *>.toJsonObject(): JSONObject {
    try {
        return JSONObject(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return JSONObject()
}