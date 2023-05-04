/**
 * Created by Michael Avoyan on 11/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.FinalizeOffersRepository
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.usecases.FinalizeOffersUseCase

internal class FinalizeOffersUseCaseImpl(
    private val finalizeOffersRepository: FinalizeOffersRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val executor: Executor
): FinalizeOffersUseCase {
    override fun finalizeOffers(
        token: VCLToken,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) {
        val callingLooper = Looper.myLooper()
        val passedCredentials = mutableListOf<VCLJwt>()
        val failedCredentials = mutableListOf<VCLJwt>()
        executor.runOnBackgroundThread {
            finalizeOffersRepository.finalizeOffers(token, finalizeOffersDescriptor) { encodedJwtOffersListResult ->
                encodedJwtOffersListResult.handleResult(
                    { encodedJwtOffers ->
                        encodedJwtOffers.forEach { encodedJwtOffer ->
                            jwtServiceRepository.decode(encodedJwtOffer) { jwtResult ->
                                jwtResult.handleResult(
                                    { jwt ->
                                        if (verifyJwtCredential(jwt, finalizeOffersDescriptor)) {
                                            passedCredentials.add(jwt)
                                        } else {
                                            failedCredentials.add(jwt)
                                        }
                                        if(encodedJwtOffers.size == passedCredentials.size + failedCredentials.size) {
                                            executor.runOn(callingLooper) {
                                                completionBlock(VCLResult.Success(VCLJwtVerifiableCredentials(passedCredentials, failedCredentials)))
                                            }
                                        }
                                    },
                                    { error ->
                                        onError(error, callingLooper, completionBlock)
                                    })
                            }
                        }
                        if(encodedJwtOffers.isEmpty()) {
                            executor.runOn(callingLooper) {
                                completionBlock(VCLResult.Success(VCLJwtVerifiableCredentials(passedCredentials, failedCredentials)))
                            }
                        }
                    },
                    { error ->
                        onError(error, callingLooper, completionBlock)
                    }
                )
            }
        }
    }

    private fun verifyJwtCredential(
        jwtCredential: VCLJwt,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor
    ): Boolean {
        return jwtCredential.payload.toJSONObject()?.get("iss") == finalizeOffersDescriptor.did
    }

    private fun onError(
        error: VCLError,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit
    ) {
        executor.runOn(callingLooper) {
            completionBlock(VCLResult.Failure(error))
        }
    }
}