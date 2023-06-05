/**
 * Created by Michael Avoyan on 31/05/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.infrastructure.db

import com.nimbusds.jose.jwk.JWK

interface SecretStoreService {
    fun storeKey(keyId: String, key: JWK)
    fun retrieveKey(keyId: String): JWK
    fun containsKey(keyId: String): Boolean
}