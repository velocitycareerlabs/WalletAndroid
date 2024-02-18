/**
 * Created by Michael Avoyan on 05/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import com.nimbusds.jose.jwk.ECKey
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLDidJwkDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.keys.VCLKeyService

class VCLKeyServiceMock: VCLKeyService {
    override fun generateDidJwk(
        didJwkDescriptor: VCLDidJwkDescriptor,
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    ) {
    }

    override fun generateSecret(
        signatureAlgorithm: VCLSignatureAlgorithm,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
    }

    override fun retrieveSecretReference(
        keyId: String,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
    }

    override fun retrievePublicJwk(
        ecKey: ECKey,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
    }
}