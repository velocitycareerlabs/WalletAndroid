/**
 * Created by Michael Avoyan on 10/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.GenerateOffersRepository
import io.velocitycareerlabs.impl.domain.usecases.GenerateOffersUseCase
import io.velocitycareerlabs.impl.domain.verifiers.OffersByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.VCLLog

internal class GenerateOffersUseCaseImpl(
    private val generateOffersRepository: GenerateOffersRepository,
    private val offersByDeepLinkVerifier: OffersByDeepLinkVerifier,
    private val executor: Executor
): GenerateOffersUseCase {
    private val TAG = GenerateOffersUseCaseImpl::class.simpleName

    override fun generateOffers(
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        sessionToken: VCLToken,
        completionBlock: (VCLResult<VCLOffers>) -> Unit
    ) {
        executor.runOnBackground {
            generateOffersRepository.generateOffers(
                generateOffersDescriptor,
                sessionToken
            ) {
                it.handleResult({ offers ->
                    verifyOffersByDeepLink(offers,generateOffersDescriptor, completionBlock)
                },{ error ->
                    onError(error, completionBlock)
                })
            }
        }
    }

    private fun verifyOffersByDeepLink(
        offers: VCLOffers,
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        completionBlock: (VCLResult<VCLOffers>) -> Unit
    ) {
        generateOffersDescriptor.credentialManifest.deepLink?.let { deepLink ->
            offersByDeepLinkVerifier.verifyOffers(offers, deepLink) {
                it.handleResult({ isVerified ->
                    VCLLog.d(TAG, "Offers deep link verification result: $isVerified")
                    executor.runOnMain {
                        completionBlock(VCLResult.Success(offers))
                    }
                }, { error ->
                    onError(error, completionBlock)
                })
            }
        } ?: run {
            VCLLog.d(TAG, "Deep link was not provided => nothing to verify")
            executor.runOnMain {
                completionBlock(VCLResult.Success(offers))
            }
        }
    }

    private fun <T> onError(
        error: VCLError,
        completionBlock: (VCLResult<T>) -> Unit
    ) {
        completionBlock(VCLResult.Failure(error))
    }
}