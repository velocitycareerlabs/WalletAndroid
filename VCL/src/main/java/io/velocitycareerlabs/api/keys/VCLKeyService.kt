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

interface VCLKeyService {
    fun generateDidJwk(
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    )
    fun generateSecret(
        completionBlock: (VCLResult<ECKey>) -> Unit
    )
    fun retrieveSecretReference(
        keyId: String,
        completionBlock: (VCLResult<ECKey>) -> Unit
    )
    fun retrievePublicJwk(
        ecKey: ECKey,
        completionBlock: (VCLResult<ECKey>) -> Unit
    )
}
