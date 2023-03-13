/**
 * Created by Michael Avoyan on 9/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.repositories

import io.velocitycareerlabs.api.VCLEnvironment
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
        val expectedUrlPrefix = "https://registrar.velocitynetwork.foundation"

        GlobalConfig.CurrentEnvironment = VCLEnvironment.PROD

        assert(Urls.CredentialTypes.startsWith(expectedUrlPrefix))
        assert(Urls.CredentialTypeSchemas.startsWith(expectedUrlPrefix))
        assert(Urls.Countries.startsWith(expectedUrlPrefix))
        assert(Urls.Organizations.startsWith(expectedUrlPrefix))
        assert(Urls.ResolveKid.startsWith(expectedUrlPrefix))
        assert(Urls.CredentialTypesFormSchema.startsWith(expectedUrlPrefix))
    }

    @Test
    fun testStagingEnvironment() {
        val expectedUrlPrefix = "https://stagingregistrar.velocitynetwork.foundation"

        GlobalConfig.CurrentEnvironment = VCLEnvironment.STAGING

        assert(Urls.CredentialTypes.startsWith(expectedUrlPrefix))
        assert(Urls.CredentialTypeSchemas.startsWith(expectedUrlPrefix))
        assert(Urls.Countries.startsWith(expectedUrlPrefix))
        assert(Urls.Organizations.startsWith(expectedUrlPrefix))
        assert(Urls.ResolveKid.startsWith(expectedUrlPrefix))
        assert(Urls.CredentialTypesFormSchema.startsWith(expectedUrlPrefix))
    }

    @Test
    fun testQaEnvironment() {
        val expectedUrlPrefix = "https://qaregistrar.velocitynetwork.foundation"

        GlobalConfig.CurrentEnvironment = VCLEnvironment.QA

        assert(Urls.CredentialTypes.startsWith(expectedUrlPrefix))
        assert(Urls.CredentialTypeSchemas.startsWith(expectedUrlPrefix))
        assert(Urls.Countries.startsWith(expectedUrlPrefix))
        assert(Urls.Organizations.startsWith(expectedUrlPrefix))
        assert(Urls.ResolveKid.startsWith(expectedUrlPrefix))
        assert(Urls.CredentialTypesFormSchema.startsWith(expectedUrlPrefix))
    }

    @Test
    fun testDevEnvironment() {
        val expectedUrlPrefix = "https://devregistrar.velocitynetwork.foundation"

        GlobalConfig.CurrentEnvironment = VCLEnvironment.DEV

        assert(Urls.CredentialTypes.startsWith(expectedUrlPrefix))
        assert(Urls.CredentialTypeSchemas.startsWith(expectedUrlPrefix))
        assert(Urls.Countries.startsWith(expectedUrlPrefix))
        assert(Urls.Organizations.startsWith(expectedUrlPrefix))
        assert(Urls.ResolveKid.startsWith(expectedUrlPrefix))
        assert(Urls.CredentialTypesFormSchema.startsWith(expectedUrlPrefix))
    }

    @Test
    fun testVersion() {
        assert(HeaderValues.XVnfProtocolVersion == "1.0")
    }

    @After
    fun tearDown() {
    }
}