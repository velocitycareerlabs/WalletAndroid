/**
 * Created by Michael Avoyan on 02/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.infrastructure.keys

import com.nimbusds.jose.jwk.ECKey
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.infrastructure.keys.KeyService
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService

internal class KeyServiceRemoteImpl(
    private val networkService: NetworkService
) : KeyService {
    override fun generateDidJwk(
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun generateSecret(
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun retrieveSecretReference(
        keyId: String,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun retrievePublicJwk(
        ecKey: ECKey,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}