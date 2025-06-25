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
import io.velocitycareerlabs.impl.utils.VCLLog

internal class PresentationRequestByDeepLinkVerifierImpl: PresentationRequestByDeepLinkVerifier {
    private val TAG = PresentationRequestByDeepLinkVerifierImpl::class.simpleName

    override fun verifyPresentationRequest(
        presentationRequest: VCLPresentationRequest,
        deepLink: VCLDeepLink,
        didDocument: VCLDidDocument,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        deepLink.did?.let { deepLinkDid ->
            if (didDocument.id == presentationRequest.iss && didDocument.id == deepLinkDid ||
                didDocument.alsoKnownAs.contains(presentationRequest.iss) && didDocument.alsoKnownAs.contains(deepLinkDid)) {
                completionBlock(VCLResult.Success(true))
            } else {
                onError(
                    errorCode = VCLErrorCode.MismatchedPresentationRequestInspectorDid,
                    errorMessage = "presentation request: ${presentationRequest.jwt.encodedJwt} \ndid document: $didDocument",
                    completionBlock = completionBlock
                )
            }
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