/**
 * Created by Michael Avoyan on 11/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.FinalizeOffersRepository
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.usecases.FinalizeOffersUseCase

internal class FinalizeOffersUseCaseImpl(
    private val finalizeOffersRepository: FinalizeOffersRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val executor: Executor
): FinalizeOffersUseCase {
    override fun finalizeOffers(
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        didJwk: VCLDidJwk,
        token: VCLToken,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) {
        executor.runOnBackground {
            this.jwtServiceRepository.generateSignedJwt(
                kid = didJwk.kid,
                nonce = finalizeOffersDescriptor.offers.challenge,
                jwtDescriptor = VCLJwtDescriptor(
                    iss = didJwk.value,
                    aud = finalizeOffersDescriptor.issuerId
                )
            ) { proofJwtResult ->
                proofJwtResult.handleResult(
                    successHandler = { proof ->
                        this.finalizeOffersRepository.finalizeOffers(
                            token = token,
                            proof = proof,
                            finalizeOffersDescriptor = finalizeOffersDescriptor
                        ) { encodedJwtOffersListResult ->
                            encodedJwtOffersListResult.handleResult(
                                successHandler = { encodedJwtOffersList ->
                                    this.verifyCredentials(
                                        encodedJwtOffersList,
                                        finalizeOffersDescriptor,
                                        completionBlock
                                    )
                                },
                                errorHandler = { error ->
                                    executor.runOnMain {
                                        completionBlock(VCLResult.Failure(error))
                                    }
                                }
                            )
                        }
                    },
                    errorHandler = { error ->
                        executor.runOnMain {
                            completionBlock(VCLResult.Failure(error))
                        }
                    }
                )
            }
        }
    }

    private fun verifyCredentials(
        encodedJwts: List<String>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) {
        val passedCredentials = mutableListOf<VCLJwt>()
        val failedCredentials = mutableListOf<VCLJwt>()
        encodedJwts.forEach { encodedJwtOffer ->
            this.jwtServiceRepository.decode(encodedJwt = encodedJwtOffer) { jwtResult ->
                jwtResult.handleResult(
                    successHandler = { jwtCredential ->
                        if (this.verifyJwtCredential(jwtCredential, finalizeOffersDescriptor)) {
                            passedCredentials += jwtCredential
                        } else {
                            failedCredentials += jwtCredential
                        }
                        if (encodedJwts.size == passedCredentials.size + failedCredentials.size) {
                            this.executor.runOnMain {
                                completionBlock(
                                    VCLResult.Success(
                                        VCLJwtVerifiableCredentials(
                                            passedCredentials = passedCredentials,
                                            failedCredentials = failedCredentials
                                        )
                                    )
                                )
                            }
                        }
                    },
                    errorHandler = { error ->
                        this.onError(error, completionBlock)
                    }
                )
            }
        }
        if (encodedJwts.isEmpty()) {
            executor.runOnMain {
                completionBlock(
                    VCLResult.Success(VCLJwtVerifiableCredentials(
                            passedCredentials = passedCredentials,
                            failedCredentials = failedCredentials
                        ))
                )
            }
        }
    }

    private fun verifyJwtCredential (
        jwtCredential: VCLJwt,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor
    ) = jwtCredential.payload.toJSONObject()["iss"] as? String == finalizeOffersDescriptor.did

    private fun onError(
        error: VCLError,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) = executor.runOnMain {
            completionBlock(VCLResult.Failure(error))
        }
}