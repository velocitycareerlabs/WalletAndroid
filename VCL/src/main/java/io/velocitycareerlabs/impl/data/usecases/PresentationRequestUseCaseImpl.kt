/**
 * Created by Michael Avoyan on 4/12/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.*
import io.velocitycareerlabs.impl.domain.usecases.PresentationRequestUseCase
import io.velocitycareerlabs.impl.domain.verifiers.PresentationRequestByDeepLinkVerifier
import io.velocitycareerlabs.impl.extensions.encode
import io.velocitycareerlabs.impl.utils.VCLLog

internal class PresentationRequestUseCaseImpl(
    private val presentationRequestRepository: PresentationRequestRepository,
    private val resolveKidRepository: ResolveKidRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val presentationRequestByDeepLinkVerifier: PresentationRequestByDeepLinkVerifier,
    private val executor: Executor
): PresentationRequestUseCase {

    private val TAG = PresentationRequestUseCaseImpl::class.simpleName

    override fun getPresentationRequest(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        verifiedProfile: VCLVerifiedProfile,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        executor.runOnBackground {
            presentationRequestRepository.getPresentationRequest(
                presentationRequestDescriptor
            ) { encodedJwtStrResult ->
                encodedJwtStrResult.handleResult(
                    { encodedJwtStr ->
                        onGetPresentationRequestSuccess(
                            VCLPresentationRequest(
                                jwt = VCLJwt(encodedJwtStr),
                                verifiedProfile = verifiedProfile,
                                deepLink = presentationRequestDescriptor.deepLink,
                                pushDelegate = presentationRequestDescriptor.pushDelegate,
                                didJwk = presentationRequestDescriptor.didJwk,
                                remoteCryptoServicesToken = presentationRequestDescriptor.remoteCryptoServicesToken
                            ),
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

    private fun onGetPresentationRequestSuccess(
        presentationRequest: VCLPresentationRequest,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        presentationRequest.jwt.kid?.replace("#", "#".encode())?.let { keyID ->
            resolveKidRepository.getPublicKey(keyID) { publicKeyResult ->
                publicKeyResult.handleResult({ publicKey ->
                    onResolvePublicKeySuccess(
                        publicKey,
                        presentationRequest,
                        completionBlock
                    )
                }, { error ->
                    onError(error, completionBlock)
                })
            }
        } ?: run {
            onError(VCLError("Empty KeyID"), completionBlock)
        }
    }

    private fun onResolvePublicKeySuccess(
        publicJwk: VCLPublicJwk,
        presentationRequest: VCLPresentationRequest,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        jwtServiceRepository.verifyJwt(
            presentationRequest.jwt,
            publicJwk,
            presentationRequest.remoteCryptoServicesToken
        ) { jwtVerificationRes ->
            jwtVerificationRes.handleResult({
                presentationRequestByDeepLinkVerifier.verifyPresentationRequest(
                    presentationRequest,
                    presentationRequest.deepLink
                ) { byDeepLinkVerificationRes ->
                    byDeepLinkVerificationRes.handleResult({ isVerified ->
                        VCLLog.d(TAG, "Presentation request by deep link verification result: $isVerified")
                        onVerificationSuccess(
                            isVerified,
                            presentationRequest,
                            completionBlock
                        )
                    }, { error ->
                        onError(error, completionBlock)
                    })
                }
            }, { error ->
                onError(error, completionBlock)
            })
        }
    }

    private fun onVerificationSuccess(
        isVerified: Boolean,
        presentationRequest: VCLPresentationRequest,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        if (isVerified)
            executor.runOnMain {
                completionBlock(VCLResult.Success(presentationRequest))
            }
        else
            onError(
                VCLError("Failed  to verify: ${presentationRequest.jwt.payload}"),
                completionBlock
            )
    }

    private fun onError(
        error: VCLError,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        executor.runOnMain {
            completionBlock(VCLResult.Failure(error))
        }
    }
}