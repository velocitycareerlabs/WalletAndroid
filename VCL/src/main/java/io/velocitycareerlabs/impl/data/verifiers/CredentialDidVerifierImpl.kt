/**
 * Created by Michael Avoyan on 04/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.verifiers

import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtVerifiableCredentials
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.verifiers.CredentialDidVerifier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

internal class CredentialDidVerifierImpl: CredentialDidVerifier {

    override fun verifyCredentials(
        jwtCredentials: List<VCLJwt>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) {
        val passedCredentials = mutableListOf<VCLJwt>()
        val failedCredentials = mutableListOf<VCLJwt>()
        jwtCredentials.forEach { jwtCredential ->
            if (verifyCredential(jwtCredential, finalizeOffersDescriptor)) {
                passedCredentials += jwtCredential
            } else {
                failedCredentials += jwtCredential
            }
        }
        completionBlock(
            VCLResult.Success(
                VCLJwtVerifiableCredentials(
                    passedCredentials = passedCredentials,
                    failedCredentials = failedCredentials
                )
            )
        )
    }

    private fun verifyCredential(
        jwtCredential: VCLJwt,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor
        // iss == vc.issuer.id
    ) = jwtCredential.payload?.toJSONObject()?.get("iss") as? String == finalizeOffersDescriptor.issuerId
}