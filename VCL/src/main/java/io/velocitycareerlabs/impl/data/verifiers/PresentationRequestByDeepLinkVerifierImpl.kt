/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.verifiers

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.domain.verifiers.PresentationRequestByDeepLinkVerifier

class PresentationRequestByDeepLinkVerifierImpl: PresentationRequestByDeepLinkVerifier {
    override fun verifyPresentationRequest(
        presentationRequest: VCLPresentationRequest,
        deepLink: VCLDeepLink,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        if (presentationRequest.iss == deepLink.did) {
            completionBlock(VCLResult.Success(true))
        } else {
            completionBlock(VCLResult.Failure(
                VCLError(errorCode = VCLErrorCode.MismatchedPresentationRequestInspectorDid.value)
            ))
        }
    }
}