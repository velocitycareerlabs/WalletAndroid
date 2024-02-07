/**
 * Created by Michael Avoyan on 11/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.domain.verifiers.CredentialDidVerifier
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.FinalizeOffersRepository
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.usecases.FinalizeOffersUseCase
import io.velocitycareerlabs.impl.domain.verifiers.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.domain.verifiers.CredentialsByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.VCLLog
import java.util.UUID

internal class FinalizeOffersUseCaseImpl(
    private val finalizeOffersRepository: FinalizeOffersRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val credentialIssuerVerifier: CredentialIssuerVerifier,
    private val credentialDidVerifier: CredentialDidVerifier,
    private val credentialsByDeepLinkVerifier: CredentialsByDeepLinkVerifier,
    private val executor: Executor
): FinalizeOffersUseCase {
    private val TAG = FinalizeOffersUseCaseImpl::class.simpleName

    override fun finalizeOffers(
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        sessionToken: VCLToken,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) {
        executor.runOnBackground {
            this.jwtServiceRepository.generateSignedJwt(
                jwtDescriptor = VCLJwtDescriptor(
                    iss = finalizeOffersDescriptor.didJwk?.did ?: UUID.randomUUID().toString(),
                    aud = finalizeOffersDescriptor.aud
                ),
                nonce = finalizeOffersDescriptor.offers.challenge,
                didJwk = finalizeOffersDescriptor.didJwk,
            ) { proofJwtResult ->
                proofJwtResult.handleResult(
                    successHandler = { proof ->
                        this.finalizeOffersRepository.finalizeOffers(
                            sessionToken = sessionToken,
                            proof = proof,
                            finalizeOffersDescriptor = finalizeOffersDescriptor
                        ) { jwtCredentialsListResult ->
                            jwtCredentialsListResult.handleResult(
                                successHandler = { jwtCredentials ->
                                    verifyCredentialsByDeepLink(
                                        jwtCredentials,
                                        finalizeOffersDescriptor
                                    ) { verifyCredentialsByDeepLinkResult ->
                                        verifyCredentialsByDeepLinkResult.handleResult(
                                            successHandler = {
                                                verifyCredentialsByIssuer(
                                                    jwtCredentials,
                                                    finalizeOffersDescriptor
                                                ) { verifyCredentialsByIssuerResult ->
                                                    verifyCredentialsByIssuerResult.handleResult({
                                                        verifyCredentialByDid(
                                                            jwtCredentials,
                                                            finalizeOffersDescriptor
                                                        ) { jwtVerifiableCredentialsResult ->
                                                            executor.runOnMain {
                                                                completionBlock(
                                                                    jwtVerifiableCredentialsResult
                                                                )
                                                            }
                                                        }
                                                    }, { error ->
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
    
    private fun verifyCredentialsByDeepLink(
        jwtCredentials: List<VCLJwt>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        finalizeOffersDescriptor.credentialManifest.deepLink?.let { deepLink ->
            credentialsByDeepLinkVerifier.verifyCredentials(
                jwtCredentials,
                deepLink
            ) {
                it.handleResult({ isVerified ->
                    VCLLog.d(TAG, "Credentials by deep link verification result: $isVerified")
                    completionBlock(VCLResult.Success(true))
                }, { error ->
                    completionBlock(VCLResult.Failure(error))
                })
            }
        } ?: run {
            VCLLog.d(TAG, "Deep link was not provided => nothing to verify")
            completionBlock(VCLResult.Success(true))
        }
    }

    private fun verifyCredentialsByIssuer(
        jwtCredentials: List<VCLJwt>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        credentialIssuerVerifier.verifyCredentials(
            jwtCredentials,
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
        encodedJwtCredentialsList: List<VCLJwt>,
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