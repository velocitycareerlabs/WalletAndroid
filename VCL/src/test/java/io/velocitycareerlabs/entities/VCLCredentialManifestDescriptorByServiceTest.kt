/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptor
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptorByService
import io.velocitycareerlabs.api.entities.VCLServiceCredentialAgentIssuer
import io.velocitycareerlabs.api.entities.VCLIssuingType
import io.velocitycareerlabs.impl.extensions.encode
import io.velocitycareerlabs.impl.extensions.isUrlEquivalentTo
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestDescriptorMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class VCLCredentialManifestDescriptorByServiceTest {

    lateinit var subject: VCLCredentialManifestDescriptorByService

    @Before
    fun setUp() {
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithFullInput1Success() {
        val service =
            VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Career,
            credentialTypes = CredentialManifestDescriptorMocks.CredentialTypesList,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate
        )

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[0]}" +
                    "&${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[1]}" +
                    "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint =
            (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint == mockEndpoint)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithFullInput2Success() {
        val service =
            VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Identity,
            credentialTypes = CredentialManifestDescriptorMocks.CredentialTypesList,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate
        )

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[0]}" +
                    "&${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[1]}" +
                    "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint =
            (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint == mockEndpoint)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithPartialInput2Success() {
        val service =
            VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Career,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate
        )

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint =
            (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithPartialInput3Success() {
        val service =
            VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceWithParamJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Career,
            credentialTypes = CredentialManifestDescriptorMocks.CredentialTypesList
        )

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[0]}" +
                    "&${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[1]}"
        val mockEndpoint =
            (CredentialManifestDescriptorMocks.IssuingServiceWithParamEndPoint + "&" + credentialTypesQuery)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithPartialInput4Success() {
        val service =
            VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceWithParamJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Career,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate
        )

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint =
            (CredentialManifestDescriptorMocks.IssuingServiceWithParamEndPoint + "&" + credentialTypesQuery)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithPartialInput5Success() {
        val service =
            VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceWithParamJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Career,
        )
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceWithParamEndPoint)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithPartialInput6Success() {
        val service =
            VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service
        )
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceEndPoint)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @After
    fun tearDown() {
    }
}