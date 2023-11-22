/**
 * Created by Michael Avoyan on 12/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.utils

import io.velocitycareerlabs.api.entities.VCLJwt

internal class Utils {
    companion object {
        internal fun getCredentialType(jwtCredential: VCLJwt): String? =
            ((jwtCredential.payload?.toJSONObject()
                ?.get(CredentialIssuerVerifierImpl.KeyVC) as? Map<*, *>)
                ?.get(CredentialIssuerVerifierImpl.KeyType) as? List<*>)
                ?.first() as? String

        internal fun getCredentialSubject(jwtCredential: VCLJwt): Map<*, *>? =
            (jwtCredential.payload?.toJSONObject()
                ?.get(CredentialIssuerVerifierImpl.KeyVC) as? Map<*, *>)
                ?.get(CredentialIssuerVerifierImpl.KeyCredentialSubject) as? Map<*, *>

        internal fun getIdentifier(
            primaryOrgProp: String?,
            jsonObject: Map<*, *>
        ): String? {
            if(primaryOrgProp == null) {
                return null
            }
            var identifier: String? = null
            val stack = mutableListOf<Map<*, *>>()
            stack.add(jsonObject)

            while (stack.isNotEmpty()) {
                val obj = stack.removeAt(stack.size - 1)

                identifier = getPrimaryIdentifier(obj[primaryOrgProp])
                if (identifier != null) {
                    break
                }

                obj.forEach { (_, value) ->
                    (value as? Map<*, *>)?.let {
                        stack.add(value)
                    }
                }
            }
            return identifier
        }

//        The recursive version:
//        internal fun getIdentifier(
//            primaryOrgProp: String?,
//            jsonObject: Map<*, *>
//        ): String? {
//            val identifier = getPrimaryIdentifier(jsonObject[primaryOrgProp])
//            if (identifier != null) {
//                return identifier
//            }
//
//            jsonObject.forEach { (_, value) ->
//                (value as? Map<*, *>)?.let {
//                    val nestedIdentifier = getIdentifier(primaryOrgProp, it)
//                    if (nestedIdentifier != null) {
//                        return nestedIdentifier
//                    }
//                }
//            }
//            return null
//        }

        internal fun getPrimaryIdentifier(
            credentialSubject: Any?
        ): String? {
            if ((credentialSubject as? String)?.isNotEmpty() == true)
                return credentialSubject
            return (credentialSubject as? Map<*, *>)?.get("identifier") as? String
                ?: (credentialSubject as? Map<*, *>)?.get("id") as? String
        }
    }
}