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
import io.velocitycareerlabs.impl.utils.VCLLog

class OffersByDeepLinkVerifierImpl: OffersByDeepLinkVerifier {
    private val TAG = OffersByDeepLinkVerifierImpl::class.simpleName

    override fun verifyOffers(
        offers: VCLOffers,
        deepLink: VCLDeepLink,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        offers.all.find { it.issuerId != deepLink.did }?.let { mismatchedOffer ->
            VCLLog.e(TAG, "mismatched offer: ${mismatchedOffer.payload} \ndeepLink: ${deepLink.value}")
            completionBlock(VCLResult.Failure(VCLError(errorCode = VCLErrorCode.MismatchedOfferIssuerDid.value)))
        } ?: run {
            completionBlock(VCLResult.Success(true))
        }
    }
}