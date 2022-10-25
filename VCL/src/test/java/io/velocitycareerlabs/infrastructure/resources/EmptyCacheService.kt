/**
 * Created by Michael Avoyan on 17/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources

import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService

internal class EmptyCacheService: CacheService {
    override fun getCountries(keyUrl: String): String? = null
    override fun setCountries(keyUrl: String, value: String) {
    }

    override fun getCredentialTypes(keyUrl: String): String? = null
    override fun setCredentialTypes(keyUrl: String, value: String) {
    }

    override fun getCredentialTypeSchema(keyUrl: String): String? = null
    override fun setCredentialTypeSchema(keyUrl: String, value: String) {
    }
}