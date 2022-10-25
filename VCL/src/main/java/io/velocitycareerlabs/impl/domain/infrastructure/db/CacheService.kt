/**
 * Created by Michael Avoyan on 16/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.infrastructure.db

internal interface CacheService {
    fun getCountries(keyUrl: String): String?
    fun setCountries(keyUrl: String, value: String)

    fun getCredentialTypes(keyUrl: String): String?
    fun setCredentialTypes(keyUrl: String, value: String)

    fun getCredentialTypeSchema(keyUrl: String): String?
    fun setCredentialTypeSchema(keyUrl: String, value: String)
}