package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLDeepLink

/**
 * Created by Michael Avoyan on 4/5/21.
 */
internal interface PresentationRequestRepository {
    fun getPresentationRequest(deepLink: VCLDeepLink, completionBlock: (VCLResult<String>) -> Unit)
}