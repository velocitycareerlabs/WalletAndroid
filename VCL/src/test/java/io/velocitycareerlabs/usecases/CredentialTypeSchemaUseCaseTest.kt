/**
 * Created by Michael Avoyan on 4/29/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.repositories.CredentialTypeSchemaRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.CredentialTypeSchemasUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.CredentialTypeSchemasUseCase
import io.velocitycareerlabs.infrastructure.EmptyExecutor
import io.velocitycareerlabs.infrastructure.db.CacheServiceEmptyMock
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialTypeSchemaMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class CredentialTypeSchemaUseCaseTest {

    lateinit var subject: CredentialTypeSchemasUseCase

    @Before
    fun setUp() {
    }

    @Test
    fun testGetCredentialTypeSchemas() {
//        Arrange
        subject = CredentialTypeSchemasUseCaseImpl(
                CredentialTypeSchemaRepositoryImpl(
                    NetworkServiceSuccess(CredentialTypeSchemaMocks.CredentialTypeSchemaJson),
                    CacheServiceEmptyMock()
                ),
            CredentialTypeSchemaMocks.CredentialTypes,
                EmptyExecutor()
        )
        var result: VCLResult<VCLCredentialTypeSchemas>? = null

//        Action
        subject.getCredentialTypeSchemas{
            result = it
        }

//        Assert
        assert(
            result?.data!!.all[CredentialTypeSchemaMocks.CredentialType.schemaName!!]!!.payload.toString() ==
                    JSONObject(CredentialTypeSchemaMocks.CredentialTypeSchemaJson).toString()
        )
    }

    @After
    fun tearDown() {
    }
}