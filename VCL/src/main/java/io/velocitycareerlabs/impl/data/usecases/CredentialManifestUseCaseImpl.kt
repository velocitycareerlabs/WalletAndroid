/**
 * Created by Michael Avoyan on 09/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.CredentialManifestRepository
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.repositories.ResolveKidRepository
import io.velocitycareerlabs.impl.domain.usecases.CredentialManifestUseCase
import io.velocitycareerlabs.impl.domain.verifiers.CredentialManifestByDeepLinkVerifier
import io.velocitycareerlabs.impl.extensions.encode
import io.velocitycareerlabs.impl.utils.VCLLog

internal class CredentialManifestUseCaseImpl(
    private val credentialManifestRepository: CredentialManifestRepository,
    private val resolveKidRepository: ResolveKidRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val credentialManifestByDeepLinkVerifier: CredentialManifestByDeepLinkVerifier,
    private val executor: Executor
): CredentialManifestUseCase {

    private val TAG = CredentialManifestUseCaseImpl::class.simpleName

    override fun getCredentialManifest(
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        verifiedProfile: VCLVerifiedProfile,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        executor.runOnBackground {
            credentialManifestRepository.getCredentialManifest(
                credentialManifestDescriptor
            ) { jwtStrResult ->
                jwtStrResult.handleResult(
                    { jwtStr ->
                        try {
                            onGetCredentialManifestSuccess(
                                VCLCredentialManifest(
                                    VCLJwt(jwtStr),
                                    credentialManifestDescriptor.vendorOriginContext,
                                    verifiedProfile,
                                    credentialManifestDescriptor.deepLink
                                ),
                                remoteCryptoServicesToken,
                                completionBlock
                            )
                        } catch (ex: Exception) {
                            this.onError(VCLError(ex), completionBlock)
                        }
                    },
                    { error ->
                        onError(error, completionBlock)
                    }
                )
            }
        }
    }

    private fun onGetCredentialManifestSuccess(
        credentialManifest: VCLCredentialManifest,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        credentialManifest.deepLink?.let { deepLink ->
            credentialManifestByDeepLinkVerifier.verifyCredentialManifest(credentialManifest, deepLink) {
                it.handleResult(
                    { isVerified ->
                        VCLLog.d(TAG, "Credential manifest deep link verification result: $isVerified")
                        onCredentialManifestDidVerificationSuccess(
                            credentialManifest,
                            remoteCryptoServicesToken,
                            completionBlock
                        )
                    },
                    { error ->
                        onError(error, completionBlock)
                    }
                )
            }
        } ?: run {
            VCLLog.d(TAG, "Deep link was not provided => nothing to verify")
            onCredentialManifestDidVerificationSuccess(
                credentialManifest,
                remoteCryptoServicesToken,
                completionBlock
            )
        }
    }

    private fun onCredentialManifestDidVerificationSuccess(
        credentialManifest: VCLCredentialManifest,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        credentialManifest.jwt.kid?.replace("#", "#".encode())?.let { kid ->
            resolveKidRepository.getPublicKey(kid) { publicKeyResult ->
                publicKeyResult.handleResult(
                    { publicKey ->
                        onResolvePublicKeySuccess(
                            publicKey,
                            credentialManifest,
                            remoteCryptoServicesToken,
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
        publicJwk: VCLPublicJwk,
        credentialManifest: VCLCredentialManifest,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        jwtServiceRepository.verifyJwt(
            credentialManifest.jwt,
            publicJwk,
            remoteCryptoServicesToken
        )
        { verificationResult ->
            verificationResult.handleResult(
                { isVerified ->
                    onVerificationSuccess(
                        isVerified,
                        credentialManifest,
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
        credentialManifest: VCLCredentialManifest,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        if (isVerified) {
            executor.runOnMain {
                completionBlock(VCLResult.Success(credentialManifest))
            }
        } else {
            onError(
                VCLError("Failed to verify credentialManifest jwt:\n${credentialManifest.jwt}"),
                completionBlock
            )
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