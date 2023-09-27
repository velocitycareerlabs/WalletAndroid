/**
 * Created by Michael Avoyan on 04/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.utils

import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLJwtVerifiableCredentials
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.utils.CredentialDidVerifier
import java.util.concurrent.CompletableFuture

internal class CredentialDidVerifierImpl: CredentialDidVerifier {

    override fun verifyCredentials(
        jwtEncodedCredentials: List<String>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) {
        val passedCredentials = mutableListOf<VCLJwt>()
        val failedCredentials = mutableListOf<VCLJwt>()
        val completableFutures = jwtEncodedCredentials.map { jwtEncodedCredential ->
            CompletableFuture.supplyAsync {
                val jwtCredential = VCLJwt(jwtEncodedCredential)
                if (verifyCredential(jwtCredential, finalizeOffersDescriptor)) {
                    passedCredentials += jwtCredential
                } else {
                    failedCredentials += jwtCredential
                }
            }
        }
        val allFutures = CompletableFuture.allOf(*completableFutures.toTypedArray())
        allFutures.join()

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
    ) = jwtCredential.payload?.toJSONObject()?.get("iss") as? String == finalizeOffersDescriptor.did
}