/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.CredentialManifestDescriptorCodingKeys
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptor
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptorRefresh
import io.velocitycareerlabs.api.entities.VCLService
import io.velocitycareerlabs.api.entities.VCLIssuingType
import io.velocitycareerlabs.impl.extensions.encode
import io.velocitycareerlabs.impl.extensions.isUrlEquivalentTo
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestDescriptorMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class VCLCredentialManifestDescriptorRefreshTest {

    lateinit var subject: VCLCredentialManifestDescriptorRefresh

    @Before
    fun setUp() {
    }
    
    @Test
    fun testCredentialManifestDescriptorWith2CredentialIdsSuccess() {
        val service = VCLService(
            JSONObject(
                CredentialManifestDescriptorMocks.IssuingServiceJsonStr)
        )
        subject = VCLCredentialManifestDescriptorRefresh(
            service = service,
            credentialIds = listOf(
                CredentialManifestDescriptorMocks.CredentialId1,
                CredentialManifestDescriptorMocks.CredentialId2
            ),
            didJwk = DidJwkMocks.DidJwk,
            did = "123"
        )

        val credentialTypesQuery = "${CredentialManifestDescriptorCodingKeys.KeyRefresh}=${true}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyCredentialId}=${CredentialManifestDescriptorMocks.CredentialId1.encode()}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyCredentialId}=${CredentialManifestDescriptorMocks.CredentialId2.encode()}"
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint.isUrlEquivalentTo(mockEndpoint))
        assert(subject.did == "123")
    }

    @Test
    fun testCredentialManifestDescriptorWith1CredentialIdsSuccess() {
        val service = VCLService(
            JSONObject(
                CredentialManifestDescriptorMocks.IssuingServiceJsonStr)
        )
        subject = VCLCredentialManifestDescriptorRefresh(
            service = service,
            credentialIds = listOf(CredentialManifestDescriptorMocks.CredentialId1),
            didJwk = DidJwkMocks.DidJwk,
            did = "123"
        )

        val credentialTypesQuery = "${CredentialManifestDescriptorCodingKeys.KeyRefresh}=${true}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyCredentialId}=${CredentialManifestDescriptorMocks.CredentialId1.encode()}"
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint.isUrlEquivalentTo(mockEndpoint))
        assert(subject.did == "123")
    }

    @Test
    fun testCredentialManifestDescriptorWith0CredentialIdsSuccess() {
        val service = VCLService(
            JSONObject(
                CredentialManifestDescriptorMocks.IssuingServiceJsonStr)
        )
        subject = VCLCredentialManifestDescriptorRefresh(
            service = service,
            credentialIds = listOf(),
            didJwk = DidJwkMocks.DidJwk,
            did = "123"
        )

        val credentialTypesQuery = "${CredentialManifestDescriptorCodingKeys.KeyRefresh}=${true}"
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint.isUrlEquivalentTo(mockEndpoint))
        assert(subject.did == "123")
    }

    @After
    fun tearDown() {
    }
}