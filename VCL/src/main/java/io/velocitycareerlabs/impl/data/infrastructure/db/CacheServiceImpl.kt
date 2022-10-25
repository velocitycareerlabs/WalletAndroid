/**
 * Created by Michael Avoyan on 16/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.infrastructure.db

import android.content.Context
import android.content.SharedPreferences
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.extensions.decodeBase64
import io.velocitycareerlabs.impl.extensions.encodeToBase64

internal class CacheServiceImpl(
    context: Context
): CacheService {
    companion object {
        private const val NAME = GlobalConfig.VclPackage
        private const val MODE = Context.MODE_PRIVATE
    }
    private var preferences: SharedPreferences = context.getSharedPreferences(NAME, MODE)

    // an inline function to put variable and save it
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    private fun getString(key: String) = preferences.getString(key, null)?.decodeBase64()
    private fun setString(key: String, value: String) =
        preferences.edit {
            it.putString(key, value.encodeToBase64())
        }

    override fun getCountries(keyUrl: String) = getString(keyUrl)
    override fun setCountries(keyUrl: String, value: String) = setString(keyUrl, value)

    override fun getCredentialTypes(keyUrl: String) = getString(keyUrl)
    override fun setCredentialTypes(keyUrl: String, value: String) = setString(keyUrl, value)

    override fun getCredentialTypeSchema(keyUrl: String) = getString(keyUrl)
    override fun setCredentialTypeSchema(keyUrl: String, value: String) = setString(keyUrl, value)
}