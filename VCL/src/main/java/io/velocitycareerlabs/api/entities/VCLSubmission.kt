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
import java.util.*

abstract class VCLSubmission(
    val submitUri: String,
    val exchangeId: String,
    val presentationDefinitionId: String,
    val verifiableCredentials: List<VCLVerifiableCredential>? = null,
    val pushDelegate: VCLPushDelegate? = null,
    val vendorOriginContext: String? = null
) {
    val jti = UUID.randomUUID().toString()
    val submissionId = UUID.randomUUID().toString()

    internal fun generatePayload(iss: String?): JSONObject {
        val retVal = JSONObject()
        retVal.putOpt(VCLSubmission.KeyJti, jti)
            .putOpt(VCLSubmission.KeyIss, iss)
        val vp = JSONObject()
            .putOpt(
                VCLSubmission.KeyType,
                VCLSubmission.ValueVerifiablePresentation
            )
            .putOpt(VCLSubmission.KeyPresentationSubmission, JSONObject()
                .putOpt(VCLSubmission.KeyId, submissionId)
                .putOpt(VCLSubmission.KeyDefinitionId, presentationDefinitionId)
                .putOpt(VCLSubmission.KeyDescriptorMap, JSONArray(verifiableCredentials?.mapIndexed { index, credential ->
                    JSONObject()
                        .putOpt(VCLSubmission.KeyId, credential.inputDescriptor)
                        .putOpt(VCLSubmission.KeyPath, "$.verifiableCredential[$index]")
                        .putOpt(VCLSubmission.KeyFormat, VCLSubmission.ValueJwtVc) })))
        vp.putOpt(VCLSubmission.KeyVerifiableCredential, JSONArray(verifiableCredentials?.map { credential -> credential.jwtVc }))
        vendorOriginContext?.let { vp.putOpt(VCLSubmission.KeyVendorOriginContext, vendorOriginContext) }
        retVal.putOpt(VCLSubmission.KeyVp, vp)
        return retVal
    }

    fun generateRequestBody(jwt: VCLJwt): JSONObject = JSONObject()
        .putOpt(VCLSubmission.KeyExchangeId, exchangeId)
        .putOpt(VCLSubmission.KeyJwtVp, jwt.encodedJwt)
        .putOpt(VCLSubmission.KeyPushDelegate, pushDelegate?.toJsonObject())
        .putOpt(VCLSubmission.KeyContext, VCLSubmission.ValueContextList.toJsonArray())

    companion object CodingKeys {
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

        const val ValueJwtVc = "jwt_vc"
        const val ValueVerifiablePresentation = "VerifiablePresentation"

        const val KeyContext = "@context"
        val ValueContextList = listOf("https://www.w3.org/2018/credentials/v1")
    }
}

