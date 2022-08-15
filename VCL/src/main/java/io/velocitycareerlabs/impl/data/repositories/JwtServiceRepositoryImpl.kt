package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.jwt.JwtService
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import org.json.JSONObject
import java.lang.Exception

/**
 * Created by Michael Avoyan on 4/6/21.
 */
internal class JwtServiceRepositoryImpl(
        private val jwtService: JwtService
): JwtServiceRepository {

    override fun decode(encodedJwt: String, completionBlock: (VCLResult<VCLJWT>) -> Unit) {
        try {
            jwtService.parse(encodedJwt)?.let { completionBlock(VCLResult.Success(VCLJWT(it))) }
                    ?: throw Exception("Failed to parse $encodedJwt")
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex.message)))
        }
    }

    override fun verifyJwt(jwt: VCLJWT, publicKey: VCLPublicKey, completionBlock: (VCLResult<Boolean>) -> Unit) {
        try {
            completionBlock(VCLResult.Success(jwtService.verify(jwt, publicKey.jwkStr)))
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex.message)))
        }
    }

    override fun generateSignedJwt(payload: JSONObject, iss: String, completionBlock: (VCLResult<VCLJWT>) -> Unit) {
        try {
            jwtService.sign(payload, iss)?.let { completionBlock(VCLResult.Success(VCLJWT(it))) }
                    ?: throw Exception("Failed to sign $payload")
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex.message)))
        }
    }
}