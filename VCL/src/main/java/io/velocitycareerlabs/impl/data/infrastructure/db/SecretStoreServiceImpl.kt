/**
 * Created by Michael Avoyan on 31/05/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.infrastructure.db

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.nimbusds.jose.jwk.JWK
import io.velocitycareerlabs.impl.domain.infrastructure.db.SecretStoreService

/**
 * Inspired by https://github.com/microsoft/VerifiableCredential-SDK-Android/blob/4920a90e5383dcf26b65b61df8b2dd5fb0eacf6f/sdk/src/main/java/com/microsoft/did/sdk/crypto/keyStore/EncryptedKeyStore.kt#L13
 */
class SecretStoreServiceImpl(
    val context: Context
): SecretStoreService {
    companion object {
        private const val KEY_PREFIX = "key_"
        private const val FILE_NAME = "encrypted_keys"
    }

    private val encryptedSharedPreferences by lazy { getSharedPreferences(context) }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            FILE_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun storeKey(keyId: String, key: JWK) {
        encryptedSharedPreferences.edit().putString(KEY_PREFIX + keyId, key.toJSONString()).apply()
    }

    override fun retrieveKey(keyId: String): JWK {
        val keyJson = encryptedSharedPreferences.getString(KEY_PREFIX + keyId, null)
            ?: throw Exception("Key $keyId not found")
        return JWK.parse(keyJson)
    }

    override fun containsKey(keyId: String): Boolean {
        return encryptedSharedPreferences.contains(KEY_PREFIX + keyId)
    }
}