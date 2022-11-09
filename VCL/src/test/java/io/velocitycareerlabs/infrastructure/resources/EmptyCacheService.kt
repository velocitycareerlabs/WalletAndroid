/**
 * Created by Michael Avoyan on 17/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources

import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService

internal class EmptyCacheService: CacheService {
    override fun getCountries(key: String) = null
    override fun setCountries(key: String, value: String, cacheSequence: Int) {}
    override fun isResetCacheCountries(cacheSequence: Int) = false
    override fun getCredentialTypes(key: String) = null
    override fun setCredentialTypes(key: String, value: String, cacheSequence: Int) {}
    override fun isResetCacheCredentialTypes(cacheSequence: Int) = false
    override fun getCredentialTypeSchema(key: String) = null
    override fun setCredentialTypeSchema(key: String, value: String, cacheSequence: Int) {}
    override fun isResetCacheCredentialTypeSchema(cacheSequence: Int) = false
}