/**
 * Created by Michael Avoyan on 09/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptor
import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
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
    private val phases = PublicRequestUseCasePhases(
        resolveDidDocumentRepository,
        jwtServiceRepository,
        executor,
    )

    override fun getCredentialManifest(
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        verifiedProfile: VCLVerifiedProfile,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        val complete = phases.mainThreadCompletion(completionBlock)

        executor.runOnBackground {
            clientRequestFetch(credentialManifestDescriptor)
                .then { jwtStr ->
                    requestValidationDecode(
                        jwtStr,
                        credentialManifestDescriptor,
                        verifiedProfile
                    )
                }
                .then { credentialManifest ->
                    phases.didResolution(
                        credentialManifest.iss,
                        ErrorTaxonomy.RequestKindIssuing,
                    )
                        .map { didDocument ->
                            CredentialManifestVerificationContext(
                                credentialManifest,
                                didDocument
                            )
                        }
                }
                .then { context ->
                    phases.requestValidationVerifyJwt(
                        context.credentialManifest.jwt,
                        context.didDocument,
                        context.credentialManifest.remoteCryptoServicesToken,
                        context.credentialManifest.iss,
                        credentialManifestDescriptor.endpoint,
                        ErrorTaxonomy.RequestKindIssuing,
                    )
                        .map { context }
                }
                .then { context ->
                    requestValidationVerifyDeepLink(context.credentialManifest, context.didDocument)
                }
                .invoke(complete)
        }
    }

    private fun clientRequestFetch(
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
    ): VCLAsyncResult<String> =
        vclAsyncResult { completion ->
            credentialManifestRepository.getCredentialManifest(
                credentialManifestDescriptor,
                completion
            )
        }

    private fun requestValidationDecode(
        jwtStr: String,
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        verifiedProfile: VCLVerifiedProfile,
    ): VCLAsyncResult<VCLCredentialManifest> =
        vclAsyncResult { completion ->
            jwtServiceRepository.decode(jwtStr) { jwtResult ->
                completion(
                    when (jwtResult) {
                        is VCLResult.Failure -> VCLResult.Failure(
                            phases.requestValidationError(
                                jwtResult.error,
                                credentialManifestDescriptor.did,
                                credentialManifestDescriptor.endpoint,
                                ErrorTaxonomy.RequestKindIssuing,
                            )
                        )
                        is VCLResult.Success -> VCLResult.Success(
                            credentialManifest(
                                jwtResult.data,
                                credentialManifestDescriptor,
                                verifiedProfile
                            )
                        )
                    }
                )
            }
        }

    private fun requestValidationVerifyDeepLink(
        credentialManifest: VCLCredentialManifest,
        didDocument: VCLDidDocument,
    ): VCLAsyncResult<VCLCredentialManifest> =
        vclAsyncResult { completion ->
            val deepLink = credentialManifest.deepLink
            if (deepLink == null) {
                VCLLog.d(TAG, "Deep link was not provided => nothing to verify")
                completion(VCLResult.Success(credentialManifest))
                return@vclAsyncResult
            }

            credentialManifestByDeepLinkVerifier.verifyCredentialManifest(
                credentialManifest,
                deepLink,
                didDocument
            ) { verificationResult ->
                completion(
                    when (verificationResult) {
                        is VCLResult.Failure -> VCLResult.Failure(
                            phases.requestValidationError(
                                verificationResult.error,
                                credentialManifest.iss,
                                credentialManifest.deepLink?.requestUri,
                                ErrorTaxonomy.RequestKindIssuing,
                            )
                        )
                        is VCLResult.Success -> {
                            val isVerified = verificationResult.data
                            VCLLog.d(TAG, "Credential manifest deep link verification result: $isVerified")
                            if (isVerified) {
                                VCLResult.Success(credentialManifest)
                            } else {
                                VCLResult.Failure(
                                    phases.requestValidationError(
                                        VCLError(message = "Failed to verify credentialManifest jwt:\n${credentialManifest.jwt}"),
                                        credentialManifest.iss,
                                        credentialManifest.deepLink?.requestUri,
                                        ErrorTaxonomy.RequestKindIssuing,
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }

    private fun credentialManifest(
        jwt: VCLJwt,
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        verifiedProfile: VCLVerifiedProfile,
    ) = VCLCredentialManifest(
        jwt = jwt,
        vendorOriginContext = credentialManifestDescriptor.vendorOriginContext,
        verifiedProfile = verifiedProfile,
        deepLink = credentialManifestDescriptor.deepLink,
        didJwk = credentialManifestDescriptor.didJwk,
        remoteCryptoServicesToken = credentialManifestDescriptor.remoteCryptoServicesToken
    )

    private data class CredentialManifestVerificationContext(
        val credentialManifest: VCLCredentialManifest,
        val didDocument: VCLDidDocument,
    )
}
