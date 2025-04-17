/**
 * Created by Michael Avoyan on 10/04/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.VCLAuthToken
import io.velocitycareerlabs.api.entities.VCLAuthTokenDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.AuthTokenRepository
import io.velocitycareerlabs.impl.domain.usecases.AuthTokenUseCase

internal class AuthTokenUseCaseImpl(
    private val authTokenRepository: AuthTokenRepository,
    private val executor: Executor
): AuthTokenUseCase {
    override fun getAuthToken(
        authTokenDescriptor: VCLAuthTokenDescriptor,
        completionBlock: (VCLResult<VCLAuthToken>) -> Unit
    ) {
        executor.runOnBackground {
            authTokenRepository.getAuthToken(authTokenDescriptor) {
                executor.runOnMain {
                    completionBlock(it)
                }
            }
        }
    }
}