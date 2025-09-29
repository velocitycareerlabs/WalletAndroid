package io.velocitycareerlabs.impl.data.verifiers.directissuerverification

import io.velocitycareerlabs.api.entities.VCLJwt
import kotlin.collections.get

/**
 * Created by Michael Avoyan on 12/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
internal class VerificationUtils {
    companion object {
        /**
         * The implementation relaying on the below reference:
         * https://github.com/velocitycareerlabs/velocitycore/blob/37c8535c2ef839ed72a2706685a398f20f4ae11c/packages/vc-checks/src/extract-credential-type.js#L20
         */
        internal fun getCredentialType(jwtCredential: VCLJwt): String? =
            ((jwtCredential.payload?.toJSONObject()
                ?.get(CredentialIssuerVerifierImpl.KeyVC) as? Map<*, *>)
                ?.get(CredentialIssuerVerifierImpl.KeyType) as? List<*>)
                ?.firstOrNull { it != "VerifiableCredential" } as? String

        internal fun getCredentialSubjectFromCredential(jwtCredential: VCLJwt): Map<*, *>? =
            getCredentialSubjectFromPayload(jwtCredential.payload?.toJSONObject())

        internal fun getContextsFromCredential(jwtCredential: VCLJwt): List<*>? {
            val credentialPayloadJson = jwtCredential.payload?.toJSONObject()

            val rootContextsList =
                getContextsFromPayload(credentialPayloadJson?.get(CredentialIssuerVerifierImpl.KeyVC) as? Map<*, *>)
            val credentialSubjectContextsList =
                getContextsFromPayload(getCredentialSubjectFromPayload(credentialPayloadJson))

            return if (rootContextsList == null && credentialSubjectContextsList == null) {
                null
            } else {
                (rootContextsList ?: emptyList<Map<*, *>>())
                    .union(credentialSubjectContextsList ?: emptyList()).toList()
            }
        }

        private fun getCredentialSubjectFromPayload(credentialPayload: Map<*, *>?): Map<*, *>? =
            (credentialPayload
                ?.get(CredentialIssuerVerifierImpl.KeyVC) as? Map<*, *>)
                ?.get(CredentialIssuerVerifierImpl.KeyCredentialSubject) as? Map<*, *>

        private fun getContextsFromPayload(map: Map<*, *>?): List<*>? {
            (map?.get(CredentialIssuerVerifierImpl.CodingKeys.KeyContext) as? List<*>)?.let { credentialSubjectContexts ->
                return credentialSubjectContexts
            }
            (map?.get(CredentialIssuerVerifierImpl.CodingKeys.KeyContext) as? String)?.let { credentialSubjectContext ->
                return listOf(credentialSubjectContext)
            }
            return null
        }

        internal fun getIdentifier(
            primaryOrgProp: String?,
            jsonObject: Map<*, *>
        ): String? {
            if (primaryOrgProp == null) {
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

        internal fun getCredentialIssuerId(jwtCredential: VCLJwt): String? {
            val vc: Map<*, *>? = jwtCredential.payload?.toJSONObject()?.get("vc") as? Map<*, *>
            return (vc?.get("issuer") as? Map<*, *>)?.get("id") as? String
                ?: vc?.get("issuer") as? String
        }

        internal fun extractCredentialSubjectType(subject: Map<*, *>): String? {
            val value = subject[CredentialIssuerVerifierImpl.KeyType]

            return when (value) {
                is List<*> -> value.firstOrNull() as? String
                is String -> value
                else -> null
            }
        }

        internal fun extractActiveContext(
            completeContext: Map<*, *>,
            credentialSubjectType: String
        ): Map<*, *> {
            val nested = (completeContext[CredentialIssuerVerifierImpl.KeyContext] as? Map<*, *>)
                ?.get(credentialSubjectType) as? Map<*, *>

            @Suppress("UNCHECKED_CAST")
            return (nested?.get(CredentialIssuerVerifierImpl.KeyContext) as? Map<*, *>)
                ?: completeContext
        }

        internal fun findKeyForPrimaryOrganizationValue(
            activeContext: Map<*, *>
        ): String? {
            for ((key, value) in activeContext) {
                val valueMap = value as? Map<*, *>
                val id = valueMap?.get(CredentialIssuerVerifierImpl.KeyId) as? String
                if (id == CredentialIssuerVerifierImpl.ValPrimaryOrganization || id == CredentialIssuerVerifierImpl.ValPrimarySourceProfile) {
                    return key as? String
                }
            }
            return null
        }
    }
}