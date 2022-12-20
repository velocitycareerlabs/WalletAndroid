/**
 * Created by Michael Avoyan on 28/10/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import org.json.JSONArray
import org.json.JSONObject

data class VCLVerifiedProfile(val payload: JSONObject) {

    val credentialSubject: JSONObject? get() = payload.optJSONObject(KeyCredentialSubject)

    val name get() = credentialSubject?.optString(KeyName)
    val logo get() = credentialSubject?.optString(KeyLogo)
    val id get() = credentialSubject?.optString(KeyId)
    val serviceTypes get() = retrieveServiceTypes(credentialSubject?.optJSONArray(KeyServiceType))

    private fun retrieveServiceTypes(serviceCategoriesJsonArr: JSONArray?): VCLServiceTypes {
        val retVal = arrayListOf<VCLServiceType>()
        serviceCategoriesJsonArr?.let {
            for (i in 0 until it.length()) {
                retVal.add(VCLServiceType.fromString(it.optString(i)))
            }
        }
        return VCLServiceTypes(retVal)
    }

    companion object CodingKeys {
        const val KeyCredentialSubject = "credentialSubject"

        const val KeyName = "name"
        const val KeyLogo = "logo"
        const val KeyId = "id"
        const val KeyServiceType = "permittedVelocityServiceCategory"
    }
}