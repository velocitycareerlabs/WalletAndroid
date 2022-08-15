package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.VCLGenerateOffersDescriptor

/**
 * Created by Michael Avoyan on 10/05/2021.
 */
internal interface GenerateOffersRepository {
    fun generateOffers(token: VCLToken,
                       generateOffersDescriptor: VCLGenerateOffersDescriptor,
                       completionBlock: (VCLResult<VCLOffers>) -> Unit)
}