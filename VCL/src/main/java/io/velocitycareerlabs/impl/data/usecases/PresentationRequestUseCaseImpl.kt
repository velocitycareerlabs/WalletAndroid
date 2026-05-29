/**
 * Created by Michael Avoyan on 4/12/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLPresentationRequestDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.repositories.PresentationRequestRepository
import io.velocitycareerlabs.impl.domain.repositories.ResolveDidDocumentRepository
import io.velocitycareerlabs.impl.domain.usecases.PresentationRequestUseCase
import io.velocitycareerlabs.impl.domain.verifiers.PresentationRequestByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.ErrorTaxonomy
import io.velocitycareerlabs.impl.utils.VCLLog

internal class PresentationRequestUseCaseImpl(
    private val presentationRequestRepository: PresentationRequestRepository,
    private val resolveDidDocumentRepository: ResolveDidDocumentRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val presentationRequestByDeepLinkVerifier: PresentationRequestByDeepLinkVerifier,
    private val executor: Executor
): PresentationRequestUseCase {

    private val TAG = PresentationRequestUseCaseImpl::class.simpleName
    private val phases = PublicRequestUseCasePhases(
        resolveDidDocumentRepository,
        jwtServiceRepository,
        executor,
    )

    override fun getPresentationRequest(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        verifiedProfile: VCLVerifiedProfile,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        val complete = phases.mainThreadCompletion(completionBlock)

        executor.runOnBackground {
            clientRequestFetch(presentationRequestDescriptor)
                .then { encodedJwtStr ->
                    requestValidationDecode(
                        encodedJwtStr,
                        presentationRequestDescriptor,
                        verifiedProfile
                    )
                }
                .then { presentationRequest ->
                    phases.didResolution(
                        presentationRequest.iss,
                        ErrorTaxonomy.RequestKindPresentation,
                    )
                        .map { didDocument ->
                            PresentationRequestVerificationContext(
                                presentationRequest,
                                didDocument
                            )
                        }
                }
                .then { context ->
                    phases.requestValidationVerifyJwt(
                        context.presentationRequest.jwt,
                        context.didDocument,
                        context.presentationRequest.remoteCryptoServicesToken,
                        context.presentationRequest.iss,
                        presentationRequestDescriptor.endpoint,
                        ErrorTaxonomy.RequestKindPresentation,
                    )
                        .map { context }
                }
                .then { context ->
                    requestValidationVerifyDeepLink(context.presentationRequest, context.didDocument)
                }
                .invoke(complete)
        }
    }

    private fun clientRequestFetch(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
    ): VCLAsyncResult<String> =
        vclAsyncResult { completion ->
            presentationRequestRepository.getPresentationRequest(
                presentationRequestDescriptor,
                completion
            )
        }

    private fun requestValidationDecode(
        encodedJwtStr: String,
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        verifiedProfile: VCLVerifiedProfile,
    ): VCLAsyncResult<VCLPresentationRequest> =
        vclAsyncResult { completion ->
            jwtServiceRepository.decode(encodedJwtStr) { jwtResult ->
                completion(
                    when (jwtResult) {
                        is VCLResult.Failure -> VCLResult.Failure(
                            phases.requestValidationError(
                                jwtResult.error,
                                presentationRequestDescriptor.did,
                                presentationRequestDescriptor.endpoint,
                                ErrorTaxonomy.RequestKindPresentation,
                            )
                        )
                        is VCLResult.Success -> VCLResult.Success(
                            presentationRequest(
                                jwtResult.data,
                                presentationRequestDescriptor,
                                verifiedProfile
                            )
                        ).takeUnless { it.data.iss.isBlank() }
                            ?: VCLResult.Failure(
                                phases.requestValidationError(
                                    VCLError(message = "Missing iss"),
                                    presentationRequestDescriptor.did,
                                    presentationRequestDescriptor.endpoint,
                                    ErrorTaxonomy.RequestKindPresentation,
                                )
                            )
                    }
                )
            }
        }

    private fun requestValidationVerifyDeepLink(
        presentationRequest: VCLPresentationRequest,
        didDocument: VCLDidDocument,
    ): VCLAsyncResult<VCLPresentationRequest> =
        vclAsyncResult { completion ->
            presentationRequestByDeepLinkVerifier.verifyPresentationRequest(
                presentationRequest,
                presentationRequest.deepLink,
                didDocument
            ) { byDeepLinkVerificationResult ->
                completion(
                    when (byDeepLinkVerificationResult) {
                        is VCLResult.Failure -> VCLResult.Failure(
                            phases.requestValidationError(
                                byDeepLinkVerificationResult.error,
                                presentationRequest.iss,
                                presentationRequest.deepLink.requestUri,
                                ErrorTaxonomy.RequestKindPresentation,
                            )
                        )
                        is VCLResult.Success -> {
                            VCLLog.d(TAG, "Presentation request by deep link verification succeeded")
                            VCLResult.Success(presentationRequest)
                        }
                    }
                )
            }
        }

    private fun presentationRequest(
        jwt: VCLJwt,
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        verifiedProfile: VCLVerifiedProfile,
    ) = VCLPresentationRequest(
        jwt = jwt,
        verifiedProfile = verifiedProfile,
        deepLink = presentationRequestDescriptor.deepLink,
        pushDelegate = presentationRequestDescriptor.pushDelegate,
        didJwk = presentationRequestDescriptor.didJwk,
        remoteCryptoServicesToken = presentationRequestDescriptor.remoteCryptoServicesToken
    )

    private data class PresentationRequestVerificationContext(
        val presentationRequest: VCLPresentationRequest,
        val didDocument: VCLDidDocument,
    )
}
