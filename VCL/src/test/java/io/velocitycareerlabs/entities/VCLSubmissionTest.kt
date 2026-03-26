/**
 * Created by Michael Avoyan on 11/12/2022.
 *
 *  Copyright 2022 Velocity Career Labs inc.
 *  SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.SubmissionCodingKeys
import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLIdentificationSubmission
import io.velocitycareerlabs.api.entities.VCLPresentationSubmission
import io.velocitycareerlabs.api.entities.VCLPushDelegate
import io.velocitycareerlabs.api.entities.VCLSubmission
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.resources.CommonMocks
import io.velocitycareerlabs.infrastructure.resources.valid.DidJwkMocks
import io.velocitycareerlabs.infrastructure.resources.valid.JwtServiceMocks
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationSubmissionMocks
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.junit.Before
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.json.JSONArray
import org.json.JSONObject

class VCLSubmissionTest {
    private lateinit var subjectPresentationSubmission: VCLSubmission
    private lateinit var subjectIdentificationSubmission: VCLSubmission
    private lateinit var credentialManifest: VCLCredentialManifest

    private val issuingIss = "issuing iss"
    private val inspectionIss = "inspection iss"
    private val uuidRegex =
        Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")

    @Before
    fun setUp() {
        subjectPresentationSubmission = VCLPresentationSubmission(
            PresentationSubmissionMocks.PresentationRequest,
            PresentationSubmissionMocks.SelectionsList
        )
        credentialManifest = VCLCredentialManifest(
            jwt = CommonMocks.JWT,
            verifiedProfile = VCLVerifiedProfile(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1.toJsonObject()!!),
            didJwk = DidJwkMocks.DidJwk
        )
        subjectIdentificationSubmission = VCLIdentificationSubmission(
            credentialManifest,
            PresentationSubmissionMocks.SelectionsList
        )
    }

    private fun expectedPayload(
        iss: String,
        presentationDefinitionId: String,
        vendorOriginContext: String?
    ): JSONObject = JSONObject()
        .put(SubmissionCodingKeys.KeyJti, "<uuid>")
        .put(SubmissionCodingKeys.KeyIss, iss)
        .put(
            SubmissionCodingKeys.KeyVp,
            JSONObject()
                .put(
                    SubmissionCodingKeys.KeyContext,
                    JSONArray(SubmissionCodingKeys.ValueContextList)
                )
                .put(
                    SubmissionCodingKeys.KeyType,
                    SubmissionCodingKeys.ValueVerifiablePresentation
                )
                .put(
                    SubmissionCodingKeys.KeyPresentationSubmission,
                    JSONObject()
                        .put(SubmissionCodingKeys.KeyId, "<uuid>")
                        .put(
                            SubmissionCodingKeys.KeyDefinitionId,
                            presentationDefinitionId
                        )
                        .put(
                            SubmissionCodingKeys.KeyDescriptorMap,
                            JSONArray(
                                PresentationSubmissionMocks.SelectionsList.mapIndexed { index, credential ->
                                    JSONObject()
                                        .put(
                                            SubmissionCodingKeys.KeyId,
                                            credential.inputDescriptor
                                        )
                                        .put(
                                            SubmissionCodingKeys.KeyPath,
                                            "$.verifiableCredential[$index]"
                                        )
                                        .put(
                                            SubmissionCodingKeys.KeyFormat,
                                            SubmissionCodingKeys.ValueJwtVcFormat
                                        )
                                }
                            )
                        )
                )
                .put(
                    SubmissionCodingKeys.KeyVerifiableCredential,
                    JSONArray(
                        PresentationSubmissionMocks.SelectionsList.map { credential ->
                            credential.jwtVc
                        }
                    )
                )
                .apply {
                    vendorOriginContext?.let {
                        put(SubmissionCodingKeys.KeyVendorOriginContext, it)
                    }
                }
        )

    private fun assertMatchesUuid(value: String?) {
        assert(value != null && uuidRegex.matches(value))
    }

    private fun normalizedPayload(payload: JSONObject): JSONObject {
        val normalizedPayload = JSONObject(payload.toString())

        assertMatchesUuid(normalizedPayload.optString(SubmissionCodingKeys.KeyJti))
        normalizedPayload.put(SubmissionCodingKeys.KeyJti, "<uuid>")

        val normalizedVp = normalizedPayload.getJSONObject(SubmissionCodingKeys.KeyVp)
        val normalizedPresentationSubmission =
            normalizedVp.getJSONObject(SubmissionCodingKeys.KeyPresentationSubmission)
        assertMatchesUuid(normalizedPresentationSubmission.optString(SubmissionCodingKeys.KeyId))
        normalizedPresentationSubmission.put(SubmissionCodingKeys.KeyId, "<uuid>")
        normalizedVp.put(
            SubmissionCodingKeys.KeyPresentationSubmission,
            normalizedPresentationSubmission
        )
        normalizedPayload.put(SubmissionCodingKeys.KeyVp, normalizedVp)

        return normalizedPayload
    }

    @Test
    fun testPayload() {
        val presentationSubmissionPayload = subjectPresentationSubmission.generatePayload(inspectionIss)
        JSONAssert.assertEquals(
            expectedPayload(
                iss = inspectionIss,
                presentationDefinitionId = PresentationSubmissionMocks.PresentationRequest.presentationDefinitionId,
                vendorOriginContext = PresentationSubmissionMocks.PresentationRequest.vendorOriginContext
            ),
            normalizedPayload(presentationSubmissionPayload),
            JSONCompareMode.STRICT
        )

        val identificationSubmissionPayload = subjectIdentificationSubmission.generatePayload(issuingIss)
        JSONAssert.assertEquals(
            expectedPayload(
                iss = issuingIss,
                presentationDefinitionId = credentialManifest.presentationDefinitionId,
                vendorOriginContext = credentialManifest.vendorOriginContext
            ),
            normalizedPayload(identificationSubmissionPayload),
            JSONCompareMode.STRICT
        )
    }

    @Test
    fun testRequestBody() {
        val requestBodyJsonObj = subjectPresentationSubmission.generateRequestBody(JwtServiceMocks.JWT)
        JSONAssert.assertEquals(
            JSONObject()
                .put(
                    SubmissionCodingKeys.KeyExchangeId,
                    PresentationSubmissionMocks.PresentationRequest.exchangeId
                )
                .put(SubmissionCodingKeys.KeyJwtVp, JwtServiceMocks.JWT.encodedJwt)
                .put(
                    SubmissionCodingKeys.KeyPushDelegate,
                    JSONObject()
                        .put(
                            VCLPushDelegate.KeyPushUrl,
                            PresentationSubmissionMocks.PushDelegate.pushUrl
                        )
                        .put(
                            VCLPushDelegate.KeyPushToken,
                            PresentationSubmissionMocks.PushDelegate.pushToken
                        )
                ),
            requestBodyJsonObj,
            JSONCompareMode.STRICT
        )
    }

    @Test
    fun testContext() {
        assert(SubmissionCodingKeys.KeyContext == "@context")
        assert(SubmissionCodingKeys.ValueContextList == listOf("https://www.w3.org/2018/credentials/v1"))
    }
}
