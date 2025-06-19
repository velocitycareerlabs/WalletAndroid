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
import io.velocitycareerlabs.impl.domain.repositories.ResolveDidDocumentRepository
import io.velocitycareerlabs.impl.domain.usecases.CredentialManifestUseCase
import io.velocitycareerlabs.impl.domain.verifiers.CredentialManifestByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.VCLLog

internal class CredentialManifestUseCaseImpl(
    private val credentialManifestRepository: CredentialManifestRepository,
    private val resolveDidDocumentRepository: ResolveDidDocumentRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val credentialManifestByDeepLinkVerifier: CredentialManifestByDeepLinkVerifier,
    private val executor: Executor
): CredentialManifestUseCase {

    private val TAG = CredentialManifestUseCaseImpl::class.simpleName

    override fun getCredentialManifest(
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        verifiedProfile: VCLVerifiedProfile,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        executor.runOnBackground {
            credentialManifestRepository.getCredentialManifest(
                credentialManifestDescriptor
            ) { jwtStrResult ->
                jwtStrResult.handleResult(
                    { jwtStr ->
                        try {
                            resolveDidDocument(
                                VCLCredentialManifest(
                                    jwt = VCLJwt(jwtStr),
                                    vendorOriginContext = credentialManifestDescriptor.vendorOriginContext,
                                    verifiedProfile = verifiedProfile,
                                    deepLink = credentialManifestDescriptor.deepLink,
                                    didJwk = credentialManifestDescriptor.didJwk,
                                    remoteCryptoServicesToken = credentialManifestDescriptor.remoteCryptoServicesToken
                                ),
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

    private fun resolveDidDocument(
        credentialManifest: VCLCredentialManifest,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        credentialManifest.jwt.kid?.let { kid ->
            resolveDidDocumentRepository.resolveDidDocument(credentialManifest.iss) { didDocumentResult ->
                didDocumentResult.handleResult({ didDocument ->
                    didDocument.getPublicJwk(kid)?.let { publicJwk ->
                        verifyCredentialManifestJwt(
                            publicJwk,
                            credentialManifest,
                            didDocument,
                            completionBlock
                        )
                    } ?: onError(
                        VCLError("public jwk not found for kid: $kid"),
                        completionBlock
                    )
                }, { error ->
                    onError(error, completionBlock)
                })
            }
        } ?: run {
            onError(
                VCLError("Empty credentialManifest.jwt.kid in jwt: ${credentialManifest.jwt}"),
                completionBlock
            )
        }
    }

    private fun verifyCredentialManifestJwt(
        publicJwk: VCLPublicJwk,
        credentialManifest: VCLCredentialManifest,
        didDocument: VCLDidDocument,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        jwtServiceRepository.verifyJwt(
            credentialManifest.jwt,
            publicJwk,
            credentialManifest.remoteCryptoServicesToken
        ) { verificationResult ->
            verificationResult.handleResult(
                { isVerified ->
                    verifyCredentialManifestByDeepLink(
                        credentialManifest,
                        didDocument,
                        completionBlock
                    )
                },
                { error ->
                    onError(error, completionBlock)
                }
            )
        }
    }

    private fun verifyCredentialManifestByDeepLink(
        credentialManifest: VCLCredentialManifest,
        didDocument: VCLDidDocument,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        credentialManifest.deepLink?.let { deepLink ->
            credentialManifestByDeepLinkVerifier.verifyCredentialManifest(
                credentialManifest,
                deepLink,
                didDocument
            ) {
                it.handleResult(
                    { isVerified ->
                        VCLLog.d(
                            TAG,
                            "Credential manifest deep link verification result: $isVerified"
                        )
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
        } ?: run {
            VCLLog.d(TAG, "Deep link was not provided => nothing to verify")
            executor.runOnMain {
                completionBlock(VCLResult.Success(credentialManifest))
            }
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