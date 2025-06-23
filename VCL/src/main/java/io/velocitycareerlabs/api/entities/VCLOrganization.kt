/**
 * Created by Michael Avoyan on 8/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toList
import io.velocitycareerlabs.impl.utils.VCLLog
import org.json.JSONObject
import java.lang.Exception

data class VCLOrganization(val payload: JSONObject) {

    val TAG = VCLOrganization::class.simpleName

    val serviceCredentialAgentIssuers: List<VCLService>
        get() = parseServiceCredentialAgentIssuers()

    val did: String
        get() = payload.optJSONArray(KeyAlsoKnownAs)?.toList()?.filterIsInstance<String>()?.get(0)
            ?: payload.optString(CodingKeys.KeyId)

    private fun parseServiceCredentialAgentIssuers(): List<VCLService> {
        val retVal = mutableListOf<VCLService>()
        try {
            payload.optJSONArray(CodingKeys.KeyService)?.let { serviceJsonArr ->
                for (i in 0 until serviceJsonArr.length()) {
                    serviceJsonArr.optJSONObject(i)
                        ?.let { retVal.add(VCLService(it)) }
                }
            }
        } catch (ex: Exception) {
            VCLLog.e(TAG, "", ex)
        }
        return retVal
    }

    companion object CodingKeys {
        const val KeyService = "service"
        const val KeyId = "id"
        const val KeyAlsoKnownAs = "alsoKnownAs"
    }
}
