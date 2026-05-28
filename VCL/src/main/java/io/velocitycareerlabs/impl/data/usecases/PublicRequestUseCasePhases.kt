/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.repositories.ResolveDidDocumentRepository
import io.velocitycareerlabs.impl.utils.ErrorTaxonomy

internal class PublicRequestUseCasePhases(
    private val resolveDidDocumentRepository: ResolveDidDocumentRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val executor: Executor,
) {
    fun didResolution(
        did: String,
        requestKind: String,
    ): VCLAsyncResult<VCLDidDocument> =
        vclAsyncResult { completion ->
            resolveDidDocumentRepository.resolveDidDocument(did) { didDocumentResult ->
                completion(
                    when (didDocumentResult) {
                        is VCLResult.Failure -> VCLResult.Failure(
                            ErrorTaxonomy.toDidResolutionError(
                                didDocumentResult.error,
                                requestKind = requestKind,
                                requestDid = did,
                            )
                        )
                        is VCLResult.Success -> didDocumentResult.data
                            .validatedForResolution(did, requestKind)
                            ?: VCLResult.Success(didDocumentResult.data)
                    }
                )
            }
        }

    fun requestValidationVerifyJwt(
        jwt: VCLJwt,
        didDocument: VCLDidDocument,
        remoteCryptoServicesToken: VCLToken?,
        requestDid: String?,
        requestKind: String,
    ): VCLAsyncResult<Unit> =
        vclAsyncResult { completion ->
            val kid = jwt.kid
            if (kid == null) {
                completion(VCLResult.Failure(missingJwtKidError(requestDid, requestKind)))
                return@vclAsyncResult
            }

            val publicJwk = didDocument.getPublicJwk(kid)
            if (publicJwk == null) {
                completion(VCLResult.Failure(unresolvedJwtKeyError(kid, requestDid, requestKind)))
                return@vclAsyncResult
            }

            jwtServiceRepository.verifyJwt(
                jwt,
                publicJwk,
                remoteCryptoServicesToken
            ) { jwtVerificationResult ->
                completion(
                    when (jwtVerificationResult) {
                        is VCLResult.Failure -> VCLResult.Failure(
                            requestValidationError(
                                jwtVerificationResult.error,
                                requestDid,
                                requestKind,
                            )
                        )
                        is VCLResult.Success -> VCLResult.Success(Unit)
                    }
                )
            }
        }

    fun <T> mainThreadCompletion(completionBlock: (VCLResult<T>) -> Unit): (VCLResult<T>) -> Unit =
        { result ->
            executor.runOnMain {
                completionBlock(result)
            }
        }

    fun requestValidationError(
        error: VCLError,
        requestDid: String?,
        requestKind: String,
    ): VCLError =
        ErrorTaxonomy.toRequestValidationError(
            error,
            requestKind = requestKind,
            requestDid = requestDid,
        )

    private fun VCLDidDocument.validatedForResolution(
        requestDid: String?,
        requestKind: String,
    ): VCLResult.Failure? =
        if (payload.length() == 0 ||
            (payload.optJSONArray(VCLDidDocument.KeyVerificationMethod)?.length() ?: 0) == 0
        ) {
            VCLResult.Failure(
                ErrorTaxonomy.toDidResolutionError(
                    VCLError(message = "public jwk not found for kid"),
                    requestKind = requestKind,
                    requestDid = requestDid,
                )
            )
        } else {
            null
        }

    private fun missingJwtKidError(requestDid: String?, requestKind: String): VCLError =
        requestValidationError(
            VCLError(message = "JWT kid is missing"),
            requestDid,
            requestKind,
        )

    private fun unresolvedJwtKeyError(
        kid: String,
        requestDid: String?,
        requestKind: String,
    ): VCLError =
        requestValidationError(
            VCLError(message = "public jwk not found for kid: $kid"),
            requestDid,
            requestKind,
        )
}
