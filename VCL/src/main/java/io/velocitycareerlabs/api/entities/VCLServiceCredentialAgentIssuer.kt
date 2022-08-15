package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toList
import org.json.JSONObject

class VCLServiceCredentialAgentIssuer(payload: JSONObject): VCLService(payload) {
    val credentialTypes: List<String>? =
        payload.optJSONArray(VCLService.KeyCredentialTypes)?.toList() as? List<String>
}