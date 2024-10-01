/**
 * Created by Michael Avoyan on 4/20/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject

data class VCLPublicJwk(
    val valueStr: String,
    val valueJson: JSONObject
) {
    constructor(valueStr: String) : this(
        valueStr = valueStr,
        valueJson = valueStr.toJsonObject() ?: JSONObject("{}")
    )

    constructor(valueJson: JSONObject) : this(
        valueStr = valueJson.toString(),
        valueJson = valueJson
    )

    val curve: String get() = valueJson.optString("crv")

    internal enum class Format(val value: String) {
        jwk("jwk"),
        hex("hex"),
        pem("pem"),
        base58("base58")
    }
}