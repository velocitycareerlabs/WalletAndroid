package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLExchange
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLExchangeDescriptor

/**
 * Created by Michael Avoyan on 30/05/2021.
 */
internal interface ExchangeProgressRepository {
    fun getExchangeProgress(exchangeDescriptor: VCLExchangeDescriptor,
                            completionBlock: (VCLResult<VCLExchange>) -> Unit)
}