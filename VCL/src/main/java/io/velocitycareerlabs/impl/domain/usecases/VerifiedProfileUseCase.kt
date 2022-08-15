package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.api.entities.VCLVerifiedProfileDescriptor

/**
 * Created by Michael Avoyan on 10/28/21.
 */
internal interface VerifiedProfileUseCase {
    fun getVerifiedProfile(
        verifiedProfileDescriptor: VCLVerifiedProfileDescriptor,
        completionBlock: (VCLResult<VCLVerifiedProfile>) -> Unit
    )
}