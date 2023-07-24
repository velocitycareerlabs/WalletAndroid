/**
 * Created by Michael Avoyan on 11/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.impl.domain.utils.CredentialDidVerifier
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.FinalizeOffersRepository
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.usecases.FinalizeOffersUseCase
import io.velocitycareerlabs.impl.domain.utils.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.utils.VCLLog
import java.util.UUID

internal class FinalizeOffersUseCaseImpl(
    private val finalizeOffersRepository: FinalizeOffersRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val credentialIssuerVerifier: CredentialIssuerVerifier,
    private val credentialDidVerifier: CredentialDidVerifier,
    private val executor: Executor
): FinalizeOffersUseCase {
    override fun finalizeOffers(
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        didJwk: VCLDidJwk?,
        token: VCLToken,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) {
        executor.runOnBackground {
            this.jwtServiceRepository.generateSignedJwt(
                kid = didJwk?.kid,
                nonce = finalizeOffersDescriptor.offers.challenge,
                jwtDescriptor = VCLJwtDescriptor(
                    keyId = didJwk?.keyId,
                    iss = didJwk?.value ?: UUID.randomUUID().toString(),
                    aud = finalizeOffersDescriptor.issuerId
                )
            ) { proofJwtResult ->
                proofJwtResult.handleResult(
                    successHandler = { proof ->
                        this.finalizeOffersRepository.finalizeOffers(
                            token = token,
                            proof = proof,
                            finalizeOffersDescriptor = finalizeOffersDescriptor
                        ) { encodedJwtCredentialsListResult ->
                            encodedJwtCredentialsListResult.handleResult(
                                successHandler = { encodedJwtCredentialsList ->
                                    verifyCredentialsByIssuer(
                                        encodedJwtCredentialsList,
                                        finalizeOffersDescriptor
                                    ) {
                                        it.handleResult(
                                            successHandler = {
                                                verifyCredentialByDid(
                                                    encodedJwtCredentialsList,
                                                    finalizeOffersDescriptor
                                                ) { jwtVerifiableCredentialsResult ->
                                                    executor.runOnMain {
                                                        completionBlock(jwtVerifiableCredentialsResult)
                                                    }
                                                }
                                            },
                                            errorHandler = { error ->
                                                onError(error, completionBlock)
                                            })
                                    }
                                },
                                errorHandler = { error ->
                                    onError(error, completionBlock)
                                })
                        }
                    },
                    errorHandler = { error ->
                        onError(error, completionBlock)
                    }
                )
            }
        }
    }

    private fun verifyCredentialsByIssuer(
        encodedJwtCredentialsList: List<String>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        credentialIssuerVerifier.verifyCredentials(
            encodedJwtCredentialsList,
            finalizeOffersDescriptor
        ) { credentialIssuerVerifierResult ->
            credentialIssuerVerifierResult.handleResult(
                successHandler = {
                    completionBlock(VCLResult.Success(true))
                },
                errorHandler = { error ->
                    completionBlock(VCLResult.Failure(error))
                }
            )
        }
    }

    private fun verifyCredentialByDid(
        encodedJwtCredentialsList: List<String>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) {
        this.credentialDidVerifier.verifyCredentials(
            encodedJwtCredentialsList,
            finalizeOffersDescriptor
        ) { jwtVerifiableCredentialsResult ->
            jwtVerifiableCredentialsResult.handleResult(
                successHandler = { jwtVerifiableCredentials ->
                    completionBlock(VCLResult.Success(jwtVerifiableCredentials))
                },
                errorHandler = { error ->
                    completionBlock(VCLResult.Failure(error))
                }
            )
        }
    }

    private fun onError(
        error: VCLError,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) = executor.runOnMain { completionBlock(VCLResult.Failure(error)) }
}