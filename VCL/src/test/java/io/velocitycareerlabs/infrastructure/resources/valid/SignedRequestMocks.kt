/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

/**
 * Organization signing keys, DID documents and request JWTs generated with Nimbus at test time.
 * Keys and signatures use BouncyCastle so both P-256 and secp256k1 fixtures can be produced on any JVM.
 */
class SignedRequestMocks {
    companion object {
        const val DidDocumentId = "did:web:devregistrar.velocitynetwork.foundation:d:example-es256.com"
        const val ExchangeKeyFragment = "#exchange-key-1"
        const val Kid = "$DidDocumentId$ExchangeKeyFragment"

        fun credentialManifestJwt(
            signingKey: ECKey,
            signatureAlgorithm: VCLSignatureAlgorithm,
        ): String =
            signedJwt(
                signingKey,
                signatureAlgorithm,
                JWTClaimsSet.Builder()
                    .issuer(DeepLinkMocks.IssuerDid)
                    .claim("issuer", mapOf("id" to DeepLinkMocks.IssuerDid))
                    .claim("exchange_id", "645e315309237c760ac022b1")
                    .claim(
                        "metadata",
                        mapOf(
                            "client_name" to "ES256 Test Issuer",
                            "finalize_offers_uri" to "$IssuingAgentUri/issue/finalize-offers",
                            "check_offers_uri" to "$IssuingAgentUri/issue/credential-offers",
                            "submit_presentation_uri" to "$IssuingAgentUri/issue/submit-identification",
                        )
                    )
                    .claim(
                        "presentation_definition",
                        mapOf(
                            "id" to "645e315309237c760ac022b1.6384a3ad148b1991687f67c9",
                            "input_descriptors" to listOf(emailInputDescriptor()),
                        )
                    ),
            )

        fun didDocument(publicKey: ECKey): String =
            JSONObject()
                .put("id", DidDocumentId)
                .put("@context", JSONArray().put("https://www.w3.org/ns/did/v1"))
                .put(
                    "verificationMethod",
                    JSONArray().put(
                        JSONObject()
                            .put("id", ExchangeKeyFragment)
                            .put("type", "JsonWebKey2020")
                            .put("controller", DidDocumentId)
                            .put("publicKeyJwk", JSONObject(publicKey.toPublicJWK().toJSONString()))
                    )
                )
                .put("assertionMethod", JSONArray().put(ExchangeKeyFragment))
                .put(
                    "alsoKnownAs",
                    JSONArray()
                        .put(DeepLinkMocks.InspectorDid)
                        .put(DeepLinkMocks.IssuerDid)
                )
                .toString()

        fun generateKey(signatureAlgorithm: VCLSignatureAlgorithm): ECKey =
            ECKeyGenerator(signatureAlgorithm.curve)
                .provider(bouncyCastle)
                .generate()

        fun presentationRequestJwt(
            signingKey: ECKey,
            signatureAlgorithm: VCLSignatureAlgorithm,
        ): String =
            signedJwt(
                signingKey,
                signatureAlgorithm,
                JWTClaimsSet.Builder()
                    .issuer(DeepLinkMocks.InspectorDid)
                    .claim("exchange_id", "60ec1f766d274e00086748ca")
                    .claim("metadata", mapOf("client_name" to "ES256 Test Verifier"))
                    .claim(
                        "presentation_definition",
                        mapOf(
                            "id" to "60ec1f766d274e00086748ca.60ec143e6d274e00086748bc",
                            "purpose" to "Id Check",
                            "input_descriptors" to listOf(emailInputDescriptor()),
                        )
                    ),
            )

        private const val IssuingAgentUri =
            "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/${DeepLinkMocks.IssuerDid}"
        private const val OneWeekMillis = 7L * 24 * 60 * 60 * 1000

        private val bouncyCastle = BouncyCastleProvider()

        private fun signedJwt(
            signingKey: ECKey,
            signatureAlgorithm: VCLSignatureAlgorithm,
            claims: JWTClaimsSet.Builder,
        ): String {
            val now = Date()
            val jwt = SignedJWT(
                JWSHeader.Builder(signatureAlgorithm.jwsAlgorithm)
                    .type(JOSEObjectType.JWT)
                    .keyID(Kid)
                    .build(),
                claims
                    .issueTime(now)
                    .notBeforeTime(now)
                    .expirationTime(Date(now.time + OneWeekMillis))
                    .build(),
            )
            jwt.sign(ECDSASigner(signingKey).apply { jcaContext.provider = bouncyCastle })
            return jwt.serialize()
        }

        private fun emailInputDescriptor() =
            mapOf(
                "id" to "EmailV1.0",
                "schema" to listOf(
                    mapOf("uri" to "https://devregistrar.velocitynetwork.foundation/schemas/email-v1.0.schema.json")
                ),
            )
    }
}
