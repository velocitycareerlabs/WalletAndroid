/**
 * Created by Michael Avoyan on 3/11/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toList
import org.json.JSONObject
import java.lang.StringBuilder

class VCLServiceCredentialAgentIssuer(payload: JSONObject): VCLService(payload) {
    val credentialTypes: List<*>? = payload.optJSONArray(VCLService.KeyCredentialTypes)?.toList()

    override fun toPropsString() =
        StringBuilder(super.toPropsString())
            .append("\ncredentialTypes: $credentialTypes")
            .toString()
}