/**
 * Created by Michael Avoyan on 4/30/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.data.repositories.CredentialTypesRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.CredentialTypesUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.CredentialTypesUseCase
import io.velocitycareerlabs.infrastructure.resources.EmptyCacheService
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialTypesMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class CredentialTypesUseCaseTest {

    lateinit var subject: CredentialTypesUseCase

    @Before
    fun setUp() {
    }

    @Test
    fun testGetCredentialTypesSuccess() {
        subject = CredentialTypesUseCaseImpl(
            CredentialTypesRepositoryImpl(
                NetworkServiceSuccess(CredentialTypesMocks.CredentialTypesJson),
                EmptyCacheService()
            ),
            EmptyExecutor()
        )

        subject.getCredentialTypes(0) {
            it.handleResult(
                { credentialTypes ->
                    compareCredentialTypes(credentialTypes.all!!, geExpectedCredentialTypesArr())
                    compareCredentialTypes(credentialTypes.recommendedTypes!!, geExpectedRecommendedCredentialTypesArr())
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testGetCredentialTypesFailure() {
        subject = CredentialTypesUseCaseImpl(
            CredentialTypesRepositoryImpl(
                NetworkServiceSuccess("wrong payload"),
                EmptyCacheService()
            ),
            EmptyExecutor()
        )

        subject.getCredentialTypes(0) {
            it.handleResult(
                successHandler = {
                    assert(false) { "${VCLErrorCode.SdkError.value} error code is expected" }
                },
                errorHandler = { error ->
                    assert(error.errorCode == VCLErrorCode.SdkError.value)
                }
            )
        }
    }

    private fun compareCredentialTypes(
        credentialTypesArr1: List<VCLCredentialType>,
        credentialTypesArr2: List<VCLCredentialType>
    ) {
        for (i in credentialTypesArr1.indices) {
            assert(credentialTypesArr1[i].id == credentialTypesArr2[i].id)
            assert(credentialTypesArr1[i].schema == credentialTypesArr2[i].schema)
            assert(credentialTypesArr1[i].createdAt == credentialTypesArr2[i].createdAt)
            assert(credentialTypesArr1[i].schemaName == credentialTypesArr2[i].schemaName)
            assert(credentialTypesArr1[i].credentialType == credentialTypesArr2[i].credentialType)
            assert(credentialTypesArr1[i].recommended == credentialTypesArr2[i].recommended)
        }
    }

    private fun geExpectedCredentialTypesArr(): List<VCLCredentialType> {
        val credentialTypesArr = mutableListOf<VCLCredentialType>()
        credentialTypesArr.add(
            VCLCredentialType(
                payload = JSONObject(CredentialTypesMocks.CredentialType1),
                id = "5fe4a315d8b45dd2e80bd739",
                schema = "",
                createdAt = "2022-03-17T09:24:38.448Z",
                schemaName = "education-degree",
                credentialType = "EducationDegree",
                recommended = false
        ))
        credentialTypesArr.add(
            VCLCredentialType(
                payload = JSONObject(CredentialTypesMocks.CredentialType2),
                id = "5fe4a315d8b45dd2e80bd73a",
                schema = "",
                createdAt = "2022-03-17T09:24:38.448Z",
                schemaName = "current-employment-position",
                credentialType = "CurrentEmploymentPosition",
                recommended = true
            ))
        credentialTypesArr.add(
            VCLCredentialType(
                payload = JSONObject(CredentialTypesMocks.CredentialType3),
                id = "5fe4a315d8b45dd2e80bd73b",
                schema = "",
                createdAt = "2022-03-17T09:24:38.448Z",
                schemaName = "past-employment-position",
                credentialType = "PastEmploymentPosition",
                recommended = false
            ))
        return credentialTypesArr
    }

    private fun geExpectedRecommendedCredentialTypesArr(): List<VCLCredentialType> {
        val credentialTypesArr = mutableListOf<VCLCredentialType>()
        credentialTypesArr.add(
            VCLCredentialType(
                payload = JSONObject(CredentialTypesMocks.CredentialType2),
                id = "5fe4a315d8b45dd2e80bd73a",
                schema = "",
                createdAt = "2022-03-17T09:24:38.448Z",
                schemaName = "current-employment-position",
                credentialType = "CurrentEmploymentPosition",
                recommended = true
            ))
        return credentialTypesArr
    }

    @After
    fun tearDown() {
    }
}