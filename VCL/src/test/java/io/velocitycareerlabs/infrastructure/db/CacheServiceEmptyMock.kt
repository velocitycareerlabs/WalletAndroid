/**
 * Created by Michael Avoyan on 17/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.db

import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService

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