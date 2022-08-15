package io.velocitycareerlabs.impl.domain.infrastructure.db

/**
 * Created by Michael Avoyan on 16/06/2021.
 */
internal interface CacheService {
    var countryCodes: String?
    var stateCodes: String?
    var credentialTypes: String?
    fun getCredentialTypeSchema(schemaName: String): String?
    fun setCredentialTypeSchema(schemaName: String, schema: String)
}