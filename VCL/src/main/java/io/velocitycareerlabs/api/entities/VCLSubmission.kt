/**
 * Created by Michael Avoyan on 8/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.impl.extensions.toJsonArray
import org.json.JSONArray
import org.json.JSONObject

interface VCLSubmission {
    val submitUri: String
    val exchangeId: String
    val presentationDefinitionId: String
    val verifiableCredentials: List<VCLVerifiableCredential>?
    val pushDelegate: VCLPushDelegate?
    val vendorOriginContext: String?
    val didJwk: VCLDidJwk
    val remoteCryptoServicesToken: VCLToken?
    val jti: String
    val submissionId: String

    fun generatePayload(iss: String?): JSONObject {
        val retVal = JSONObject()
        retVal.putOpt(SubmissionCodingKeys.KeyJti, jti)
            .putOpt(SubmissionCodingKeys.KeyIss, iss)
        val vp = JSONObject()
            .putOpt(
                SubmissionCodingKeys.KeyType,
                SubmissionCodingKeys.ValueVerifiablePresentation
            )
            .putOpt(SubmissionCodingKeys.KeyPresentationSubmission, JSONObject()
                .putOpt(SubmissionCodingKeys.KeyId, submissionId)
                .putOpt(SubmissionCodingKeys.KeyDefinitionId, presentationDefinitionId)
                .putOpt(SubmissionCodingKeys.KeyDescriptorMap, JSONArray(verifiableCredentials?.mapIndexed { index, credential ->
                    JSONObject()
                        .putOpt(SubmissionCodingKeys.KeyId, credential.inputDescriptor)
                        .putOpt(SubmissionCodingKeys.KeyPath, "$.verifiableCredential[$index]")
                        .putOpt(SubmissionCodingKeys.KeyFormat, SubmissionCodingKeys.ValueJwtVcFormat) })))
        vp.putOpt(SubmissionCodingKeys.KeyVerifiableCredential, JSONArray(verifiableCredentials?.map { credential -> credential.jwtVc }))
        vendorOriginContext?.let { vp.putOpt(SubmissionCodingKeys.KeyVendorOriginContext, vendorOriginContext) }
        retVal.putOpt(SubmissionCodingKeys.KeyVp, vp)
        return retVal
    }

    fun generateRequestBody(jwt: VCLJwt): JSONObject = JSONObject()
        .putOpt(SubmissionCodingKeys.KeyExchangeId, exchangeId)
        .putOpt(SubmissionCodingKeys.KeyJwtVp, jwt.encodedJwt)
        .putOpt(SubmissionCodingKeys.KeyPushDelegate, pushDelegate?.toJsonObject())
        .putOpt(SubmissionCodingKeys.KeyContext, SubmissionCodingKeys.ValueContextList.toJsonArray())
}

object SubmissionCodingKeys {
    const val KeyJti = "jti"
    const val KeyIss = "iss"
    const val KeyId = "id"
    const val KeyVp = "vp"
    const val KeyDid = "did"
    const val KeyPushDelegate = "push_delegate"
    const val KeyType = "type"
    const val KeyPresentationSubmission = "presentation_submission"
    const val KeyDefinitionId = "definition_id"
    const val KeyDescriptorMap = "descriptor_map"
    const val KeyExchangeId = "exchange_id"
    const val KeyJwtVp = "jwt_vp"
    const val KeyPath = "path"
    const val KeyFormat = "format"
    const val KeyVerifiableCredential = "verifiableCredential"
    const val KeyVendorOriginContext = "vendorOriginContext"
    const val KeyInputDescriptor = "input_descriptor"
    const val ValueVerifiablePresentation = "VerifiablePresentation"
    const val ValueJwtVcFormat = "jwt_vc"
    const val KeyContext = "@context"
    val ValueContextList = listOf("https://www.w3.org/2018/credentials/v1")
}
