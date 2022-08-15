package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLJWT
import io.velocitycareerlabs.api.entities.VCLPublicKey
import io.velocitycareerlabs.api.entities.VCLResult
import org.json.JSONObject

/**
 * Created by Michael Avoyan on 4/6/21.
 */
internal interface JwtServiceRepository {
    fun decode(encodedJwt: String, completionBlock: (VCLResult<VCLJWT>) -> Unit)
    fun verifyJwt(jwt: VCLJWT, publicKey: VCLPublicKey, completionBlock: (VCLResult<Boolean>) -> Unit)
    fun generateSignedJwt(payload: JSONObject, iss: String, completionBlock: (VCLResult<VCLJWT>) -> Unit)
}