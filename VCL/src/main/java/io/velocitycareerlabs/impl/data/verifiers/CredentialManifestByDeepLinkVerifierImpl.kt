/**
 * Created by Michael Avoyan on 10/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.verifiers

import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.impl.domain.verifiers.CredentialManifestByDeepLinkVerifier
import io.velocitycareerlabs.impl.utils.ErrorTaxonomy
import io.velocitycareerlabs.impl.utils.VCLLog

internal class CredentialManifestByDeepLinkVerifierImpl: CredentialManifestByDeepLinkVerifier {
    private val TAG = CredentialManifestByDeepLinkVerifierImpl::class.simpleName

    override fun verifyCredentialManifest(
        credentialManifest: VCLCredentialManifest,
        deepLink: VCLDeepLink?,
        didDocument: VCLDidDocument,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        deepLink?.did?.let { deepLinkDid ->
            if (didDocument.id == credentialManifest.issuerId && didDocument.id == deepLinkDid ||
                didDocument.alsoKnownAs.contains(credentialManifest.issuerId) && didDocument.alsoKnownAs.contains(deepLinkDid)
            ) {
                completionBlock(VCLResult.Success(true))
            } else {
                onError(
                    errorCode = VCLErrorCode.MismatchedRequestIssuerDid,
                    errorMessage = "credential manifest: ${credentialManifest.jwt.encodedJwt} \ndidDocument: $didDocument",
                    requestUri = deepLink.requestUri,
                    completionBlock = completionBlock
                )
            }
        }  ?: run {
            onError(
                errorMessage = "DID not found in deep link: ${deepLink?.value}",
                requestUri = deepLink?.requestUri,
                completionBlock = completionBlock
            )
        }
    }

    private fun onError(
        errorCode: VCLErrorCode = VCLErrorCode.SdkError,
        errorMessage: String,
        requestUri: String?,
        completionBlock: (VCLResult<Boolean>) -> Unit

    ) {
        VCLLog.e(TAG, errorMessage)
        val error = VCLError(errorCode = errorCode.value, message = errorMessage, requestUri = requestUri)
        completionBlock(
            (VCLResult.Failure(
                ErrorTaxonomy.toRequestValidationError(
                    error,
                    requestKind = ErrorTaxonomy.RequestKindIssuing,
                    requestDid = null,
                )
            ))
        )
    }
}
