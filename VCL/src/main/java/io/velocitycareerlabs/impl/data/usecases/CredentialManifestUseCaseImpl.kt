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
import io.velocitycareerlabs.impl.utils.ErrorTaxonomy
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
                            val credentialManifest = VCLCredentialManifest(
                                jwt = VCLJwt(jwtStr),
                                vendorOriginContext = credentialManifestDescriptor.vendorOriginContext,
                                verifiedProfile = verifiedProfile,
                                deepLink = credentialManifestDescriptor.deepLink,
                                didJwk = credentialManifestDescriptor.didJwk,
                                remoteCryptoServicesToken = credentialManifestDescriptor.remoteCryptoServicesToken
                            )
                            resolveDidDocumentRepository.resolveDidDocument(
                                credentialManifest.iss
                            ) { didDocumentResult ->
                                didDocumentResult.handleResult({ didDocument ->
                                    verifyCredentialManifestJwt(
                                        credentialManifest,
                                        didDocument,
                                        completionBlock
                                    )
                                }, { error ->
                                    onError(
                                        ErrorTaxonomy.classifyDidResolution(
                                            error,
                                            requestKind = ErrorTaxonomy.RequestKindIssuing,
                                            requestDid = credentialManifest.iss,
                                        ),
                                        completionBlock
                                    );
                                })
                            }
                        } catch (ex: Exception) {
                            this.onError(
                                ErrorTaxonomy.classifyClientRequestFetch(
                                    VCLError(ex),
                                    requestUri = credentialManifestDescriptor.endpoint,
                                    requestKind = ErrorTaxonomy.RequestKindIssuing,
                                ),
                                completionBlock
                            )
                        }
                    },
                    { error ->
                        onError(error, completionBlock)
                    }
                )
            }
        }
    }

    private fun verifyCredentialManifestJwt(
        credentialManifest: VCLCredentialManifest,
        didDocument: VCLDidDocument,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        val kid = credentialManifest.jwt.kid
            ?: return onError(
                missingJwtKidError(requestDid = credentialManifest.iss),
                completionBlock
            )
        val publicJwk = didDocument.getPublicJwk(kid)
            ?: return onError(
                unresolvedDidDocumentKeyError(kid, requestDid = credentialManifest.iss),
                completionBlock
            )
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
                    onError(
                        ErrorTaxonomy.classifyRequestValidation(
                            error,
                            requestKind = ErrorTaxonomy.RequestKindIssuing,
                            requestDid = credentialManifest.iss,
                        ),
                        completionBlock
                    )
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
                        onError(
                            ErrorTaxonomy.classifyRequestValidation(
                                error,
                                requestKind = ErrorTaxonomy.RequestKindIssuing,
                                requestDid = credentialManifest.iss,
                            ),
                            completionBlock
                        )
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

    private fun missingJwtKidError(requestDid: String?): VCLError =
        ErrorTaxonomy.classifyRequestValidation(
            VCLError(message = "JWT kid is missing"),
            requestKind = ErrorTaxonomy.RequestKindIssuing,
            requestDid = requestDid,
        )

    private fun unresolvedDidDocumentKeyError(kid: String, requestDid: String?): VCLError =
        ErrorTaxonomy.classifyDidResolution(
            VCLError(message = "public jwk not found for kid: $kid"),
            requestKind = ErrorTaxonomy.RequestKindIssuing,
            requestDid = requestDid,
        )

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
                ErrorTaxonomy.classifyRequestValidation(
                    VCLError(message = "Failed to verify credentialManifest jwt:\n${credentialManifest.jwt}"),
                    requestKind = ErrorTaxonomy.RequestKindIssuing,
                    requestDid = credentialManifest.iss,
                ),
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
