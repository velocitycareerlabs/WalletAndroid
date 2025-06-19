/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.verifiers

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.domain.repositories.ResolveDidDocumentRepository
import io.velocitycareerlabs.impl.domain.verifiers.OffersByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.VCLLog

internal class OffersByDeepLinkVerifierImpl(
    private val didDocumentRepository: ResolveDidDocumentRepository
): OffersByDeepLinkVerifier {
    private val TAG = OffersByDeepLinkVerifierImpl::class.simpleName

    override fun verifyOffers(
        offers: VCLOffers,
        deepLink: VCLDeepLink,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        deepLink.did?.let { did ->
            didDocumentRepository.resolveDidDocument(did) { didDocumentResult ->
                didDocumentResult.handleResult(
                    successHandler = {
                        verify(offers, it, completionBlock)
                    },
                    errorHandler = {
                        onError(
                            errorMessage = "Failed to resolve DID Document: $did",
                            completionBlock = completionBlock
                        )
                    })
            }
        } ?: run {
            onError(
                errorMessage = "DID not found in deep link: ${deepLink.value}",
                completionBlock = completionBlock
            )
        }
    }

    private fun verify(
        offers: VCLOffers,
        didDocument: VCLDidDocument,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        offers.all.find  {
            didDocument.id != it.issuerId && !didDocument.alsoKnownAs.contains(it.issuerId)
        }?.let { mismatchedOffer ->
            onError(
                errorCode = VCLErrorCode.MismatchedOfferIssuerDid,
                errorMessage = "mismatched offer: ${mismatchedOffer.payload} \ndid document: $didDocument",
                completionBlock = completionBlock
            )
        } ?: run {
            completionBlock(VCLResult.Success(true))
        }
    }

    private fun onError(
        errorCode: VCLErrorCode = VCLErrorCode.SdkError,
        errorMessage: String,
        completionBlock: (VCLResult<Boolean>) -> Unit

    ) {
        VCLLog.e(TAG, errorMessage)
        completionBlock(
            (VCLResult.Failure(VCLError(errorCode = errorCode.value, message = errorMessage)))
        )
    }
}