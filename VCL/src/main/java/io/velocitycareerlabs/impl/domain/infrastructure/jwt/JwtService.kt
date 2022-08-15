package io.velocitycareerlabs.impl.domain.infrastructure.jwt

import com.nimbusds.jose.*
import com.nimbusds.jwt.SignedJWT
import io.velocitycareerlabs.api.entities.VCLJWT
import org.json.JSONObject
import java.text.ParseException

/**
 * Created by Michael Avoyan on 4/28/21.
 */
internal interface JwtService {
    @Throws(ParseException::class)
    fun parse(jwt: String): SignedJWT?

    fun encode(str: String): String

    @Throws(JOSEException::class)
    fun verify(jwt: VCLJWT, jwk: String): Boolean

    fun sign(payload: JSONObject, iss: String): SignedJWT?
}