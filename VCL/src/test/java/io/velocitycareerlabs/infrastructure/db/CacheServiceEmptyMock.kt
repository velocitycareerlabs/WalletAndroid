package io.velocitycareerlabs.infrastructure.db

import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService

/**
 * Created by Michael Avoyan on 17/06/2021.
 */
internal class CacheServiceEmptyMock: CacheService {
    override var countryCodes: String?
        get() = null
        set(value) {}
    override var stateCodes: String?
        get() = null
        set(value) {}
    override var credentialTypes: String?
        get() = null
        set(value) {}

    override fun getCredentialTypeSchema(schemaName: String) = null

    override fun setCredentialTypeSchema(schemaName: String, schema: String) {
    }
}