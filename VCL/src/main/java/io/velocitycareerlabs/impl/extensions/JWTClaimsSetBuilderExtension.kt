package io.velocitycareerlabs.impl.extensions

import com.nimbusds.jwt.JWTClaimsSet
import org.json.JSONObject

/**
 * Created by Michael Avoyan on 4/23/21.
 */

internal fun JWTClaimsSet.Builder.addClaims(jsonObj: JSONObject) {
    val mapObj = jsonObj.toMap()
    mapObj.map {
        this.claim(it.key, it.value)
    }
}