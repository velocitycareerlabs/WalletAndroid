/**
 * Created by Michael Avoyan on 15/05/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.keys

import com.nimbusds.jose.jwk.ECKey
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.error.VCLError

interface VCLKeyService {
    fun generateDidJwk(
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    )

    /**
     * implemented for local crypto services only
     */
    fun generateSecret(
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
        completionBlock(VCLResult.Failure(VCLError(payload = "implemented for local crypto services only")))
    }
    /**
     * implemented for local crypto services only
     */
    fun retrieveSecretReference(
        keyId: String,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
        completionBlock(VCLResult.Failure(VCLError(payload = "implemented for local crypto services only")))
    }
    /**
     * implemented for local crypto services only
     */
    fun retrievePublicJwk(
        ecKey: ECKey,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
        completionBlock(VCLResult.Failure(VCLError(payload = "implemented for local crypto services only")))
    }
}
