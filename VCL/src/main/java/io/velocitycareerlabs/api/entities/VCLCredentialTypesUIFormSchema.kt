package io.velocitycareerlabs.api.entities

import org.json.JSONObject

/**
 * Created by Michael Avoyan on 13/06/2021.
 */
data class VCLCredentialTypesUIFormSchema(val payload: JSONObject) {
    companion object CodingKeys {
        const val KeyAddressRegion = "addressRegion"
        const val KeyAddressCountry = "addressCountry"
        const val KeyUiEnum = "ui:enum"
        const val KeyUiNames = "ui:enumNames"
    }
}
