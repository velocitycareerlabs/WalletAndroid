/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.CredentialManifestDescriptorCodingKeys
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptorByService
import io.velocitycareerlabs.api.entities.VCLIssuingType
import io.velocitycareerlabs.api.entities.VCLService
import io.velocitycareerlabs.impl.extensions.encode
import io.velocitycareerlabs.impl.extensions.isUrlEquivalentTo
import io.velocitycareerlabs.infrastructure.resources.valid.CredentialManifestDescriptorMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks
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
            VCLService(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Career,
            credentialTypes = CredentialManifestDescriptorMocks.CredentialTypesList,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate,
            didJwk = DidJwkMocks.DidJwk
        )

        val credentialTypesQuery =
            "${CredentialManifestDescriptorCodingKeys.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[0]}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[1]}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint =
            (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint == mockEndpoint)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithFullInput2Success() {
        val service =
            VCLService(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Identity,
            credentialTypes = CredentialManifestDescriptorMocks.CredentialTypesList,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate,
            didJwk = DidJwkMocks.DidJwk
        )

        val credentialTypesQuery =
            "${CredentialManifestDescriptorCodingKeys.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[0]}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[1]}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint =
            (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint == mockEndpoint)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithPartialInput2Success() {
        val service =
            VCLService(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Career,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate,
            didJwk = DidJwkMocks.DidJwk
        )

        val credentialTypesQuery =
            "${CredentialManifestDescriptorCodingKeys.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint =
            (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithPartialInput3Success() {
        val service =
            VCLService(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceWithParamJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Career,
            credentialTypes = CredentialManifestDescriptorMocks.CredentialTypesList,
            didJwk = DidJwkMocks.DidJwk
        )

        val credentialTypesQuery =
            "${CredentialManifestDescriptorCodingKeys.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[0]}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[1]}"
        val mockEndpoint =
            (CredentialManifestDescriptorMocks.IssuingServiceWithParamEndPoint + "&" + credentialTypesQuery)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithPartialInput4Success() {
        val service =
            VCLService(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceWithParamJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Career,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate,
            didJwk = DidJwkMocks.DidJwk
        )

        val credentialTypesQuery =
            "${CredentialManifestDescriptorCodingKeys.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${CredentialManifestDescriptorCodingKeys.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint =
            (CredentialManifestDescriptorMocks.IssuingServiceWithParamEndPoint + "&" + credentialTypesQuery)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithPartialInput5Success() {
        val service =
            VCLService(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceWithParamJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            issuingType = VCLIssuingType.Career,
            didJwk = DidJwkMocks.DidJwk
        )
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceWithParamEndPoint)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @Test
    fun testCredentialManifestDescriptorByServiceWithPartialInput6Success() {
        val service =
            VCLService(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            didJwk = DidJwkMocks.DidJwk
        )
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceEndPoint)

        assert(subject.endpoint?.isUrlEquivalentTo(mockEndpoint)!!)
        assert(subject.did == CredentialManifestDescriptorMocks.IssuerDid)
    }

    @After
    fun tearDown() {
    }
}