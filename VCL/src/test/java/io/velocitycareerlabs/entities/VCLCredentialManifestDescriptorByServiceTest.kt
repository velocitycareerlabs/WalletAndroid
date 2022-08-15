package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptor
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptorByService
import io.velocitycareerlabs.api.entities.VCLServiceCredentialAgentIssuer
import io.velocitycareerlabs.impl.extensions.encode
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
    fun testCredentialManifestDescriptorWithFullInputByServiceSuccess() {
        val service = VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            credentialTypes = CredentialManifestDescriptorMocks.CredentialTypesList,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate
        )

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[0]}" +
                "&${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[1]}" +
                "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint == mockEndpoint)
    }

    @Test
    fun testCredentialManifestDescriptorWithCredentialTypesByServiceSuccess() {
        val service = VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            credentialTypes = CredentialManifestDescriptorMocks.CredentialTypesList)

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[0]}" +
                    "&${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[1]}"
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint == mockEndpoint)
    }

    @Test
    fun testCredentialManifestDescriptorWithPushDelegateByServiceSuccess() {
        val service = VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate
        )

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceEndPoint + "?" + credentialTypesQuery)

        assert(subject.endpoint == mockEndpoint)
    }

    @Test
    fun testCredentialManifestDescriptorServiceOnlySuccess() {
        val service = VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service
        )
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceEndPoint)

        assert(subject.endpoint == mockEndpoint)
    }

    @Test
    fun testCredentialManifestDescriptorWithFullInputByServiceWithParamSuccess() {
        val service = VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceWithPAramJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            credentialTypes = CredentialManifestDescriptorMocks.CredentialTypesList,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate
        )

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[0]}" +
                    "&${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[1]}" +
                    "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceWithParamEndPoint + "&" + credentialTypesQuery)

        assert(subject.endpoint == mockEndpoint)
    }

    @Test
    fun testCredentialManifestDescriptorWithCredentialTypesByServiceWithParamSuccess() {
        val service = VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceWithPAramJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            credentialTypes = CredentialManifestDescriptorMocks.CredentialTypesList)

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[0]}" +
                    "&${VCLCredentialManifestDescriptor.KeyCredentialTypes}=${CredentialManifestDescriptorMocks.CredentialTypesList[1]}"
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceWithParamEndPoint + "&" + credentialTypesQuery)

        assert(subject.endpoint == mockEndpoint)
    }

    @Test
    fun testCredentialManifestDescriptorWithPushDelegateByServiceWithPAramSuccess() {
        val service = VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceWithPAramJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service,
            pushDelegate = CredentialManifestDescriptorMocks.PushDelegate
        )

        val credentialTypesQuery =
            "${VCLCredentialManifestDescriptor.KeyPushDelegatePushUrl}=${CredentialManifestDescriptorMocks.PushDelegate.pushUrl.encode()}" +
                    "&${VCLCredentialManifestDescriptor.KeyPushDelegatePushToken}=${CredentialManifestDescriptorMocks.PushDelegate.pushToken}"
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceWithParamEndPoint + "&" + credentialTypesQuery)

        assert(subject.endpoint == mockEndpoint)
    }

    @Test
    fun testCredentialManifestDescriptorServiceWitParamOnlySuccess() {
        val service = VCLServiceCredentialAgentIssuer(JSONObject(CredentialManifestDescriptorMocks.IssuingServiceWithPAramJsonStr))
        subject = VCLCredentialManifestDescriptorByService(
            service = service
        )
        val mockEndpoint = (CredentialManifestDescriptorMocks.IssuingServiceWithParamEndPoint)

        assert(subject.endpoint == mockEndpoint)
    }

    @After
    fun tearDown() {
    }
}