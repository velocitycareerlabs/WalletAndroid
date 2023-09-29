/**
 * Created by Michael Avoyan on 16/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.infrastructure.db

import android.content.Context
import android.content.SharedPreferences
import com.nimbusds.jose.jwk.ECKey
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.extensions.decodeBase64
import io.velocitycareerlabs.impl.extensions.encodeToBase64
import io.velocitycareerlabs.impl.utils.VCLLog

internal class CacheServiceImpl(
    context: Context
): CacheService {
    companion object {
        private const val NAME = GlobalConfig.VclPackage
        private const val MODE = Context.MODE_PRIVATE

        internal const val KEY_CACHE_SEQUENCE_COUNTRIES = "KEY_CACHE_SEQUENCE_COUNTRIES"
        internal const val KEY_CACHE_SEQUENCE_CREDENTIAL_TYPES = "KEY_CACHE_SEQUENCE_CREDENTIAL_TYPES"
        internal const val KEY_CACHE_SEQUENCE_CREDENTIAL_TYPE_SCHEMA = "KEY_CACHE_SEQUENCE_CREDENTIAL_TYPE_SCHEMA"
    }
    private var preferences: SharedPreferences = context.getSharedPreferences(NAME, MODE)

    // an inline function to put variable and save it
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    internal fun getString(key: String) = preferences.getString(key, null)?.decodeBase64()
    internal fun setString(key: String, value: String) =
        preferences.edit {
            it.putString(key, value.encodeToBase64())
        }

    internal fun getInt(key: String) = preferences.getInt(key, 0)
    internal fun setInt(key: String, value: Int) =
        preferences.edit {
            it.putInt(key, value)
        }

    override fun getCountries(key: String) = getString(key)
    override fun setCountries(key: String, value: String, cacheSequence: Int) {
        setString(key, value)
        setInt(KEY_CACHE_SEQUENCE_COUNTRIES, cacheSequence)
    }
    override fun isResetCacheCountries(cacheSequence: Int) =
        getInt(KEY_CACHE_SEQUENCE_COUNTRIES) < cacheSequence

    override fun getCredentialTypes(key: String) = getString(key)
    override fun setCredentialTypes(key: String, value: String, cacheSequence: Int) {
        setString(key, value)
        setInt(KEY_CACHE_SEQUENCE_CREDENTIAL_TYPES, cacheSequence)
    }
    override fun isResetCacheCredentialTypes(cacheSequence: Int) =
        getInt(KEY_CACHE_SEQUENCE_CREDENTIAL_TYPES) < cacheSequence

    override fun getCredentialTypeSchema(key: String) = getString(key)
    override fun setCredentialTypeSchema(key: String, value: String, cacheSequence: Int) {
        setString(key, value)
        setInt(KEY_CACHE_SEQUENCE_CREDENTIAL_TYPE_SCHEMA, cacheSequence)
    }
    override fun isResetCacheCredentialTypeSchema(cacheSequence: Int) =
        getInt(KEY_CACHE_SEQUENCE_CREDENTIAL_TYPE_SCHEMA) < cacheSequence
}