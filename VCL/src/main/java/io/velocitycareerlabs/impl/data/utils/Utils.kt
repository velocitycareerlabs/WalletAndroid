/**
 * Created by Michael Avoyan on 12/07/2023.
 */

package io.velocitycareerlabs.impl.data.utils

import io.velocitycareerlabs.api.entities.VCLJwt

internal class Utils {
    companion object {
        internal fun getCredentialType(jwtCredential: VCLJwt): String? =
            ((jwtCredential.payload?.toJSONObject()?.get(CredentialIssuerVerifierImpl.KeyVC) as? Map<*, *>)?.get(
                CredentialIssuerVerifierImpl.KeyType
            ) as? List<*>)?.first() as? String

        internal fun getCredentialSubject(jwtCredential: VCLJwt): Map<*, *>? =
            (jwtCredential.payload?.toJSONObject()?.get(CredentialIssuerVerifierImpl.KeyVC) as? Map<*, *>)?.get(
                CredentialIssuerVerifierImpl.KeyCredentialSubject
            ) as? Map<*, *>
    }
}