package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.*

/**
 * Created by Michael Avoyan on 10/05/2021.
 */
internal interface GenerateOffersUseCase {
    fun generateOffers(token: VCLToken,
                       generateOffersDescriptor: VCLGenerateOffersDescriptor,
                       completionBlock: (VCLResult<VCLOffers>) -> Unit)
}