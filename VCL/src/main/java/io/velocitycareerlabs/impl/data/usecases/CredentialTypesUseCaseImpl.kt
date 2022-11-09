/**
 * Created by Michael Avoyan on 3/11/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
import io.velocitycareerlabs.api.entities.VCLCredentialTypes
import io.velocitycareerlabs.impl.domain.usecases.CredentialTypesUseCase
import io.velocitycareerlabs.impl.domain.repositories.CredentialTypesRepository
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor

internal class CredentialTypesUseCaseImpl(
        private val credentialTypes: CredentialTypesRepository,
        private val executor: Executor
): CredentialTypesUseCase {

    override fun getCredentialTypes(
        cacheSequence: Int,
        completionBlock: (VCLResult<VCLCredentialTypes>) -> Unit
    ) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread {
            credentialTypes.getCredentialTypes(cacheSequence) {
                executor.runOn(callingLooper) { completionBlock(it) }
            }
        }
    }
}