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
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.EmptyCacheService
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.CountriesMocks
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
        subject = CredentialTypeSchemasUseCaseImpl(
                CredentialTypeSchemaRepositoryImpl(
                    NetworkServiceSuccess(CredentialTypeSchemaMocks.CredentialTypeSchemaJson),
                    EmptyCacheService()
                ),
            CredentialTypeSchemaMocks.CredentialTypes,
                EmptyExecutor()
        )

        subject.getCredentialTypeSchemas(0) {
            it.handleResult(
                successHandler = { credTypeSchemas ->
                    assert(
                        credTypeSchemas.all[CredentialTypeSchemaMocks.CredentialType.schemaName!!]!!.payload.toString() ==
                                JSONObject(CredentialTypeSchemaMocks.CredentialTypeSchemaJson).toString()
                    )
                },
                errorHandler = {
                    assert(false) { "$it" }
                }
            )
        }
    }

    @After
    fun tearDown() {
    }
}