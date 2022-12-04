/**
 * Created by Michael Avoyan on 16/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.infrastructure.db

internal interface CacheService {
    fun getCountries(key: String): String?
    fun setCountries(key: String, value: String, cacheSequence: Int)
    fun isResetCacheCountries(cacheSequence: Int): Boolean

    fun getCredentialTypes(key: String): String?
    fun setCredentialTypes(key: String, value: String, cacheSequence: Int)
    fun isResetCacheCredentialTypes(cacheSequence: Int): Boolean

    fun getCredentialTypeSchema(key: String): String?
    fun setCredentialTypeSchema(key: String, value: String, cacheSequence: Int)
    fun isResetCacheCredentialTypeSchema(cacheSequence: Int): Boolean

}