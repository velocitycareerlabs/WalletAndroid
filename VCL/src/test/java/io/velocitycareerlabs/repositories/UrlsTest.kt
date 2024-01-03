/**
 * Created by Michael Avoyan on 9/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.repositories

import io.velocitycareerlabs.api.VCLEnvironment
import io.velocitycareerlabs.api.VCLXVnfProtocolVersion
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.data.repositories.HeaderValues
import io.velocitycareerlabs.impl.data.repositories.Urls
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class UrlsTest {

    lateinit var  subject: Urls

    @Before
    fun setUp() {
    }

    @Test
    fun testProdEnvironment() {
        val registrarPrefix = "https://registrar.velocitynetwork.foundation"

        GlobalConfig.CurrentEnvironment = VCLEnvironment.Prod

        verifyUrlsPrefix(registrarPrefix)
    }

    @Test
    fun testStagingEnvironment() {
        val registrarPrefix = "https://stagingregistrar.velocitynetwork.foundation"

        GlobalConfig.CurrentEnvironment = VCLEnvironment.Staging

        verifyUrlsPrefix(registrarPrefix)
    }

    @Test
    fun testQaEnvironment() {
        val registrarPrefix = "https://qaregistrar.velocitynetwork.foundation"

        GlobalConfig.CurrentEnvironment = VCLEnvironment.Qa

        verifyUrlsPrefix(registrarPrefix)
    }

    @Test
    fun testDevEnvironment() {
        val registrarPrefix = "https://devregistrar.velocitynetwork.foundation"

        GlobalConfig.CurrentEnvironment = VCLEnvironment.Dev

        verifyUrlsPrefix(registrarPrefix)
    }

    private fun verifyUrlsPrefix(registrarPrefix: String) {
        assert(Urls.CredentialTypes.startsWith(registrarPrefix)) {"expected: $registrarPrefix, actual: ${Urls.CredentialTypes}"}
        assert(Urls.ServiceTypes.startsWith(registrarPrefix)) {"expected: $registrarPrefix, actual: ${Urls.ServiceTypes}"}
        assert(Urls.CredentialTypeSchemas.startsWith(registrarPrefix)) {"expected: $registrarPrefix, actual: ${Urls.CredentialTypeSchemas}"}
        assert(Urls.Countries.startsWith(registrarPrefix)) {"expected: $registrarPrefix, actual: ${Urls.Countries}"}
        assert(Urls.Organizations.startsWith(registrarPrefix)) {"expected: $registrarPrefix, actual: ${Urls.Organizations}"}
        assert(Urls.ResolveKid.startsWith(registrarPrefix)) {"expected: $registrarPrefix, actual: ${Urls.ResolveKid}"}
        assert(Urls.CredentialTypesFormSchema.startsWith(registrarPrefix)) {"expected: $registrarPrefix, actual: ${Urls.CredentialTypesFormSchema}"}
    }

    @Test
    fun testXVnfProtocolVersion() {
        GlobalConfig.XVnfProtocolVersion = VCLXVnfProtocolVersion.XVnfProtocolVersion1
        assert(HeaderValues.XVnfProtocolVersion == "1.0")

        GlobalConfig.XVnfProtocolVersion = VCLXVnfProtocolVersion.XVnfProtocolVersion2
        assert(HeaderValues.XVnfProtocolVersion == "2.0")
    }

    @After
    fun tearDown() {
    }
}