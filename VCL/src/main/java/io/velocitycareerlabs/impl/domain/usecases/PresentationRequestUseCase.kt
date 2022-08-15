package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 4/12/21.
 */
internal interface PresentationRequestUseCase {
    fun getPresentationRequest(deepLink: VCLDeepLink, completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit)
}