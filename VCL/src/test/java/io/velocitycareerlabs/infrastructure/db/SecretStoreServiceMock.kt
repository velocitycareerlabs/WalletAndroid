/**
 * Created by Michael Avoyan on 31/05/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.db

import com.nimbusds.jose.jwk.JWK
import io.velocitycareerlabs.impl.domain.infrastructure.db.SecretStoreService

class SecretStoreServiceMock private constructor(): SecretStoreService {

    companion object {
        val Instance = SecretStoreServiceMock()
    }
    private val map = mutableMapOf<String, Any>()

    override fun storeKey(keyId: String, key: JWK) {
        map[keyId] = key
    }

    override fun retrieveKey(keyId: String): JWK {
        if (containsKey(keyId)) {
            return map[keyId] as JWK
        } else {
            throw Exception("Failed to retrieve JWK fo keyId: $keyId")
        }
    }
    override fun containsKey(keyId: String): Boolean = map.containsKey(keyId)
}