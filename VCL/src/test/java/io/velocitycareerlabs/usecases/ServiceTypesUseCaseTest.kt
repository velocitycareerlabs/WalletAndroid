/**
 * Created by Michael Avoyan on 29/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.data.repositories.ServiceTypesRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.ServiceTypesUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.ServiceTypesUseCase
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.EmptyCacheService
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.ServiceTypesMocks
import org.junit.Before
import org.junit.Test

internal class ServiceTypesUseCaseTest {

    internal lateinit var subject: ServiceTypesUseCase

    private val CredentialgroupAllowedValues = mutableListOf("Career", "IdDocument", "Contact", "")

    @Before
    fun setUp() {
        subject = ServiceTypesUseCaseImpl(
            ServiceTypesRepositoryImpl(
                NetworkServiceSuccess(ServiceTypesMocks.ServiceTypesJsonStr),
                EmptyCacheService()
            ),
            EmptyExecutor()
        )
    }

    @Test
    fun testGetServiceTypes() {
        subject.getServiceTypes(cacheSequence = 1) {
            it.handleResult(
                successHandler = { serviceTypes ->
                    assert(serviceTypes.all.size == 10)
                    serviceTypes.all.forEach { serviceType ->
                        assert(CredentialgroupAllowedValues.contains(serviceType.credentialGroup))
                    }
                },
                errorHandler = { error ->
                    assert(false) { error }
                })
        }
    }
}