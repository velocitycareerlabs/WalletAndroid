/**
 * Created by Michael Avoyan on 06/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import org.json.JSONObject

data class VCLPushDelegate(
    /**
     * the url of the endpoint that will send pushes to the device
     */
    val pushUrl: String,
    /**
     * the token to use for identifying the group of devices this device belongs to
     */
    val pushToken: String,
) {
    fun toPropsString() =
        StringBuilder()
            .append("\npushUrl: $pushUrl")
            .append("\npushToken: $pushToken")
            .toString()
    companion object CodingKeys {
        const val KeyPushUrl = "pushUrl"
        const val KeyPushToken = "pushToken"
    }

    internal fun toJsonObject() =
        JSONObject().putOpt(KeyPushUrl, pushUrl)
            .putOpt(KeyPushToken, pushToken)
}
