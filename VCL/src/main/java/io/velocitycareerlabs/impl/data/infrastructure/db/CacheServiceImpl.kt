package io.velocitycareerlabs.impl.data.infrastructure.db

import android.content.Context
import android.content.SharedPreferences
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.extensions.decodeBase64
import io.velocitycareerlabs.impl.extensions.encodeBase64

/**
 * Created by Michael Avoyan on 16/06/2021.
 */
internal class CacheServiceImpl(
    context: Context
): CacheService {
    companion object {
        private const val NAME = GlobalConfig.VclPackage
        private const val MODE = Context.MODE_PRIVATE

        private val CountryCodesKeyValue = Pair("CountryCodes", null)
        private val StateCodesKeyValue = Pair("StateCodes", null)
        private val CredentialTypesKeyValue = Pair("CredentialTypes", null)
        private val CredentialTypeSchemasKeyValue = Pair("CredentialTypeSchemas", null)
    }
    private var preferences: SharedPreferences = context.getSharedPreferences(NAME, MODE)

    // an inline function to put variable and save it
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    override var countryCodes: String?
        get() = preferences.getString(CountryCodesKeyValue.first, CountryCodesKeyValue.second)?.decodeBase64()
        set(value) = preferences.edit {
            it.putString(CountryCodesKeyValue.first, value?.encodeBase64())
        }

    override var stateCodes: String?
        get() = preferences.getString(StateCodesKeyValue.first, StateCodesKeyValue.second)?.decodeBase64()
        set(value) = preferences.edit {
            it.putString(StateCodesKeyValue.first, value?.encodeBase64())
        }

    override var credentialTypes: String?
        get() = preferences.getString(CredentialTypesKeyValue.first, CredentialTypesKeyValue.second)?.decodeBase64()
        set(value) = preferences.edit {
            it.putString(CredentialTypesKeyValue.first, value?.encodeBase64())
        }

    override fun getCredentialTypeSchema(schemaName: String) =
        preferences.getString(
            CredentialTypeSchemasKeyValue.first + schemaName,
            CredentialTypeSchemasKeyValue.second
        )?.decodeBase64()

    override fun setCredentialTypeSchema(schemaName: String, schema: String) =
        preferences.edit {
            it.putString(CredentialTypeSchemasKeyValue.first + schemaName,
                schema.encodeBase64())
        }
}