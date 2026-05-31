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
import io.velocitycareerlabs.impl.domain.verifiers.PresentationRequestByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.ErrorTaxonomy
import io.velocitycareerlabs.impl.utils.VCLLog

internal class PresentationRequestByDeepLinkVerifierImpl: PresentationRequestByDeepLinkVerifier {
    private val TAG = PresentationRequestByDeepLinkVerifierImpl::class.simpleName

    override fun verifyPresentationRequest(
        presentationRequest: VCLPresentationRequest,
        deepLink: VCLDeepLink,
        didDocument: VCLDidDocument,
        completionBlock: (VCLResult<Unit>) -> Unit
    ) {
        val deepLinkDid = deepLink.did!!
        if (
            isDidBoundToDidDocument(presentationRequest.iss, didDocument) &&
            isDidBoundToDidDocument(deepLinkDid, didDocument)
        ) {
            completionBlock(VCLResult.Success(Unit))
        } else {
            onError(
                errorCode = VCLErrorCode.MismatchedPresentationRequestInspectorDid,
                errorMessage = "presentation request: ${presentationRequest.jwt.encodedJwt} \ndid document: $didDocument",
                requestUri = deepLink.requestUri,
                completionBlock = completionBlock
            )
        }
    }

    private fun isDidBoundToDidDocument(requestDid: String, didDocument: VCLDidDocument): Boolean =
        didDocument.id == requestDid || didDocument.alsoKnownAs.contains(requestDid)

    private fun onError(
        errorCode: VCLErrorCode = VCLErrorCode.SdkError,
        errorMessage: String,
        requestUri: String?,
        completionBlock: (VCLResult<Unit>) -> Unit

    ) {
        VCLLog.e(TAG, errorMessage)
        val error = VCLError(errorCode = errorCode.value, message = errorMessage, requestUri = requestUri)
        completionBlock(
            (VCLResult.Failure(
                ErrorTaxonomy.toRequestValidationError(
                    error,
                    requestKind = ErrorTaxonomy.RequestKindPresentation,
                    requestDid = null,
                )
            ))
        )
    }
}
