/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.verifiers

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.domain.repositories.ResolveDidDocumentRepository
import io.velocitycareerlabs.impl.domain.verifiers.PresentationRequestByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.VCLLog

internal class PresentationRequestByDeepLinkVerifierImpl(
    private val didDocumentRepository: ResolveDidDocumentRepository
): PresentationRequestByDeepLinkVerifier {
    private val TAG = PresentationRequestByDeepLinkVerifierImpl::class.simpleName

    override fun verifyPresentationRequest(
        presentationRequest: VCLPresentationRequest,
        deepLink: VCLDeepLink,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        deepLink.did?.let { did ->
            didDocumentRepository.resolveDidDocument(did) { didDocumentResult ->
                didDocumentResult.handleResult(
                    successHandler = {
                        verify(presentationRequest, it, completionBlock)
                    },
                    errorHandler = {
                        onError(
                            errorMessage = "Failed to resolve DID Document: $did",
                            completionBlock = completionBlock
                        )
                    })
            }
        } ?: {
            onError(
                errorMessage = "DID not found in deep link: ${deepLink.value}",
                completionBlock = completionBlock
            )
        }
    }

    private fun verify(
        presentationRequest: VCLPresentationRequest,
        didDocument: VCLDidDocument,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        if (didDocument.id == presentationRequest.iss ||
            didDocument.alsoKnownAs.contains(presentationRequest.iss)) {
            completionBlock(VCLResult.Success(true))
        } else {
            onError(
                errorCode = VCLErrorCode.MismatchedPresentationRequestInspectorDid,
                errorMessage = "presentation request: ${presentationRequest.jwt.encodedJwt} \ndid document: $didDocument",
                completionBlock = completionBlock
            )
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