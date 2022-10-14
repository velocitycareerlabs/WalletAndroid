/**
 * Created by Michael Avoyan on 10/28/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.api.entities.VCLVerifiedProfileDescriptor
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.VerifiedProfileRepository
import io.velocitycareerlabs.impl.domain.usecases.VerifiedProfileUseCase

internal class VerifiedProfileUseCaseImpl(
    private val verifiedProfileRepository: VerifiedProfileRepository,
    private val executor: Executor
): VerifiedProfileUseCase {
    override fun getVerifiedProfile(
        verifiedProfileDescriptor: VCLVerifiedProfileDescriptor,
        completionBlock: (VCLResult<VCLVerifiedProfile>) -> Unit
    ) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread(){
            verifiedProfileRepository.getVerifiedProfile(verifiedProfileDescriptor) {
                executor.runOn(callingLooper) { completionBlock(it) }
            }
        }
    }
}