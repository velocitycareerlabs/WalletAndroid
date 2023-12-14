/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.verifiers

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.domain.verifiers.OffersByDeepLinkVerifier

class OffersByDeepLinkVerifierImpl: OffersByDeepLinkVerifier {
    override fun verifyOffers(
        offers: VCLOffers,
        deepLink: VCLDeepLink,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        val errorOffer = offers.all.find { it.issuerId != deepLink.did }
        errorOffer?.let {
            completionBlock(VCLResult.Failure(VCLError(errorCode = VCLErrorCode.MismatchedOfferIssuerDid.value)))
        } ?: run {
            completionBlock(VCLResult.Success(true))
        }
    }
}