/**
 * Created by Michael Avoyan on 15/05/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.infrastructure.keys

import com.nimbusds.jose.jwk.ECKey
import io.velocitycareerlabs.api.entities.VCLDidJwk

internal interface KeyService {
    fun generateDidJwk(): VCLDidJwk
    fun generateKey(): ECKey
    fun retrieveKey(keyId: String): ECKey
    fun retrievePublicJwk(ecKey: ECKey): ECKey
}
