/**
 * Created by Michael Avoyan on 4/12/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.*
import io.velocitycareerlabs.impl.domain.usecases.PresentationRequestUseCase
import io.velocitycareerlabs.impl.extensions.encode
import java.lang.Exception

internal class PresentationRequestUseCaseImpl(
    private val presentationRequestRepository: PresentationRequestRepository,
    private val resolveKidRepository: ResolveKidRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val executor: Executor
): PresentationRequestUseCase {

    override fun getPresentationRequest(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread {
            presentationRequestRepository.getPresentationRequest(
                presentationRequestDescriptor
            ) { encodedJwtStrResult ->
                encodedJwtStrResult.handleResult(
                    { encodedJwtStr ->
                        onGetJwtSuccess(
                            encodedJwtStr,
                            presentationRequestDescriptor,
                            callingLooper,
                            completionBlock
                        )
                    },
                    { error ->
                        onError(error, callingLooper, completionBlock)
                    }
                )
            }
        }
    }

    private fun onGetJwtSuccess(
        encodedJwtStr: String,
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        try {
            jwtServiceRepository.decode(encodedJwtStr) { jwtResult ->
                jwtResult.handleResult({ jwt ->
                    onDecodeJwtSuccess(
                        jwt,
                        presentationRequestDescriptor,
                        callingLooper,
                        completionBlock
                    )
                }, { error ->
                    onError(error, callingLooper, completionBlock)
                })
            }
        } catch (ex: Exception) {
            onError(VCLError(ex.message), callingLooper, completionBlock)
        }
    }

    private fun onDecodeJwtSuccess(
        jwt: VCLJwt,
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        jwt.header.keyID?.replace("#", "#".encode())?.let { keyID ->
            resolveKidRepository.getPublicKey(keyID) { publicKeyResult ->
                publicKeyResult.handleResult({ publicKey ->
                    onResolvePublicKeySuccess(
                        publicKey,
                        jwt,
                        presentationRequestDescriptor,
                        callingLooper,
                        completionBlock
                    )
                }, { error ->
                    onError(error, callingLooper, completionBlock)
                })
            }
        } ?: run {
            onError(VCLError("Empty KeyID"), callingLooper, completionBlock)
        }
    }

    private fun onResolvePublicKeySuccess(
        jwkPublic: VCLJwkPublic,
        jwt: VCLJwt,
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        val presentationRequest = VCLPresentationRequest(
            jwt = jwt,
            jwkPublic = jwkPublic,
            deepLink = presentationRequestDescriptor.deepLink,
            pushDelegate = presentationRequestDescriptor.pushDelegate
        )
        jwtServiceRepository.verifyJwt(
            presentationRequest.jwt,
            presentationRequest.jwkPublic
        ) { isVerifiedResult ->
            isVerifiedResult.handleResult({ isVerified ->
                onVerificationSuccess(
                    isVerified,
                    presentationRequest,
                    callingLooper,
                    completionBlock
                )
            },
                { error ->
                    onError(error, callingLooper, completionBlock)
                })
        }
    }

    private fun onVerificationSuccess(
        isVerified: Boolean,
        presentationRequest: VCLPresentationRequest,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        if (isVerified)
            executor.runOn(callingLooper) {
                completionBlock(VCLResult.Success(presentationRequest))
            }
        else
            onError(
                VCLError("Failed  to verify: ${presentationRequest.jwt.payload}"),
                callingLooper,
                completionBlock
            )
    }

    private fun onError(
        error: VCLError,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    ) {
        executor.runOn(callingLooper) {
            completionBlock(VCLResult.Failure(error))
        }
    }
}