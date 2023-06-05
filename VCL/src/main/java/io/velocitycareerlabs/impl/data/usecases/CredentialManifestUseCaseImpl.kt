/**
 * Created by Michael Avoyan on 09/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.CredentialManifestRepository
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.repositories.ResolveKidRepository
import io.velocitycareerlabs.impl.domain.usecases.CredentialManifestUseCase
import io.velocitycareerlabs.impl.extensions.encode

internal class CredentialManifestUseCaseImpl(
    private val credentialManifestRepository: CredentialManifestRepository,
    private val resolveKidRepository: ResolveKidRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val executor: Executor
): CredentialManifestUseCase {

    override fun getCredentialManifest(
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        executor.runOnBackground {
            credentialManifestRepository.getCredentialManifest(
                credentialManifestDescriptor
            ) { jwtStrResult ->
                jwtStrResult.handleResult(
                    { jwtStr ->
                        onGetJwtSuccess(
                            jwtStr,
                            credentialManifestDescriptor,
                            completionBlock
                        )
                    },
                    { error ->
                        onError(error, completionBlock)
                    }
                )
            }
        }
    }

    private fun onGetJwtSuccess(
        jwtStr: String,
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        jwtServiceRepository.decode(jwtStr) { jwtResult ->
            jwtResult.handleResult(
                { jwt ->
                    onDecodeJwtSuccess(
                        jwt,
                        credentialManifestDescriptor,
                        completionBlock
                    )
                },
                { error ->
                    onError(error, completionBlock)
                }
            )
        }
    }

    private fun onDecodeJwtSuccess(
        jwt: VCLJwt,
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        jwt.kid?.replace("#", "#".encode())?.let { kid ->
            resolveKidRepository.getPublicKey(kid) { publicKeyResult ->
                publicKeyResult.handleResult(
                    { publicKey ->
                        onResolvePublicKeySuccess(
                            publicKey,
                            jwt,
                            credentialManifestDescriptor,
                            completionBlock
                        )
                    },
                    { error ->
                        onError(error, completionBlock)
                    }
                )
            }
        } ?: run {
            onError(VCLError("Empty KeyID"), completionBlock)
        }
    }

    private fun onResolvePublicKeySuccess(
        jwkPublic: VCLJwkPublic,
        jwt: VCLJwt,
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        jwtServiceRepository.verifyJwt(jwt, jwkPublic)
        { verificationResult ->
            verificationResult.handleResult(
                { isVerified ->
                    onVerificationSuccess(
                        isVerified,
                        jwt,
                        credentialManifestDescriptor,
                        completionBlock
                    )
                },
                { error ->
                    onError(error, completionBlock)
                }
            )
        }
    }

    private fun onVerificationSuccess(
        isVerified: Boolean,
        jwt: VCLJwt,
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        if (isVerified) {
            executor.runOnMain {
                completionBlock(VCLResult.Success(VCLCredentialManifest(
                    jwt,
                    credentialManifestDescriptor.vendorOriginContext
                )))
            }
        } else {
            onError(VCLError("Failed to verify: $jwt"), completionBlock)
        }
    }

    private fun onError(
        error: VCLError,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        executor.runOnMain {
            completionBlock(VCLResult.Failure(error))
        }
    }
}