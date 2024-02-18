/**
 * Created by Michael Avoyan on 4/20/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toJsonObject
import org.json.JSONObject

class VCLPublicJwk {
    val valueStr: String
    val valueJson: JSONObject

    constructor(valueStr: String) {
        this.valueStr = valueStr
        this.valueJson = this.valueStr.toJsonObject() ?: JSONObject("{}")
    }

    constructor(valueJson: JSONObject) {
        this.valueJson = valueJson
        this.valueStr = this.valueJson.toString()
    }

    val curve: String get() = valueJson.optString("crv")

    internal enum class Format(val value: String) {
        jwk("jwk"),
        hex("hex"),
        pem("pem"),
        base58("base58")
    }
}