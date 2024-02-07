/**
 * Created by Michael Avoyan on 28/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.VCLService
import io.velocitycareerlabs.api.entities.VCLOrganizationsSearchDescriptor
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.repositories.OrganizationsRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.OrganizationsUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.OrganizationsUseCase
import io.velocitycareerlabs.impl.extensions.toList
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.OrganizationsMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class OrganizationsUseCaseTest {

    lateinit var subject: OrganizationsUseCase

    @Before
    fun setUp() {
        subject = OrganizationsUseCaseImpl(
            OrganizationsRepositoryImpl(
                NetworkServiceSuccess(
                    OrganizationsMocks.OrganizationJsonResult
                ),
            ),
            EmptyExecutor()
        )
    }

    @Test
    fun testSearchForOrganizationsSuccess() {
        val serviceJsonMock = JSONObject(OrganizationsMocks.ServiceJsonStr)

        subject.searchForOrganizations(VCLOrganizationsSearchDescriptor(query = "")) {
            it.handleResult(
                { orgs ->
                    val serviceCredentialAgentIssuer = orgs.all[0].serviceCredentialAgentIssuers[0]
                    assert(serviceCredentialAgentIssuer.payload.toString() == serviceJsonMock.toString())
                    assert(serviceCredentialAgentIssuer.id == serviceJsonMock.getString(VCLService.KeyId))
                    assert(serviceCredentialAgentIssuer.type == serviceJsonMock.getString(VCLService.KeyType))
                    assert(
                        serviceCredentialAgentIssuer.credentialTypes == serviceJsonMock.getJSONArray(
                            VCLService.KeyCredentialTypes
                        ).toList()
                    )
                    assert(serviceCredentialAgentIssuer.serviceEndpoint == OrganizationsMocks.ServiceEndpoint)
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }
}