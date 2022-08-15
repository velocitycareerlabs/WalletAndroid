package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLJwtVerifiableCredentials
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor

/**
 * Created by Michael Avoyan on 11/05/2021.
 */
internal interface FinalizeOffersUseCase {
    fun finalizeOffers(token: VCLToken,
                       finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
                       completionBlock: (VCLResult<VCLJwtVerifiableCredentials>) -> Unit)
}