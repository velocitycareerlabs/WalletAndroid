/**
 * Created by Michael Avoyan on 02/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.jwt.local

import com.nimbusds.jose.crypto.ECDSAVerifier
import com.nimbusds.jose.jwk.JWK
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.jwt.VCLJwtVerifyService
import java.lang.Exception

class VCLJwtVerifyServiceLocalImpl: VCLJwtVerifyService {
    override fun verify(
        jwt: VCLJwt,
        publicPublic: VCLPublicJwk,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        try {
            completionBlock(
                VCLResult.Success(
                    jwt.signedJwt?.verify(ECDSAVerifier(JWK.parse(publicPublic.valueStr).toECKey())) == true
                )
            )
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }
}