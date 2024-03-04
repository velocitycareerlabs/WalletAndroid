/**
 * Created by Michael Avoyan on 04/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.verifiers

import io.velocitycareerlabs.api.entities.VCLCredentialType
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLServiceType
import io.velocitycareerlabs.api.entities.VCLServiceTypes
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.repositories.HeaderKeys
import io.velocitycareerlabs.impl.data.repositories.HeaderValues
import io.velocitycareerlabs.impl.data.utils.Utils
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.models.CredentialTypesModel
import io.velocitycareerlabs.impl.domain.verifiers.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toMap
import io.velocitycareerlabs.impl.utils.VCLLog
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

internal class CredentialIssuerVerifierImpl(
    private val credentialTypesModel: CredentialTypesModel,
    private val networkService: NetworkService
): CredentialIssuerVerifier {
    val TAG = CredentialIssuerVerifierImpl::class.java.simpleName

    override fun verifyCredentials(
        jwtCredentials: List<VCLJwt>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        if (jwtCredentials.isEmpty()) /* nothing to verify */ {
            completionBlock(VCLResult.Success(true))
        } else if (finalizeOffersDescriptor.serviceTypes.all.isEmpty()) {
            completionBlock(VCLResult.Failure(VCLError(errorCode = VCLErrorCode.CredentialTypeNotRegistered.value)))
        } else {
            var globalError: VCLError? = null
            val completableFutures = jwtCredentials.map { jwtCredential ->
                CompletableFuture.supplyAsync {
                    Utils.getCredentialType(jwtCredential)?.let { credentialTypeName ->
                        credentialTypesModel.credentialTypeByTypeName(credentialTypeName)
                            ?.let { credentialType ->
                                verifyCredential(
                                    jwtCredential,
                                    credentialType,
                                    finalizeOffersDescriptor.serviceTypes
                                ) {
                                    it.handleResult({ isVerified ->
                                        VCLLog.d(
                                            TAG,
                                            "Credential verification result = $isVerified"
                                        )
                                    }, { error ->
                                        globalError = error
                                    })
                                }
                            } ?: run {
                            globalError =
                                VCLError(errorCode = VCLErrorCode.CredentialTypeNotRegistered.value)
                        }
                    } ?: run {
                        globalError =
                            VCLError(errorCode = VCLErrorCode.CredentialTypeNotRegistered.value)
                    }
                }
            }
            val allFutures = CompletableFuture.allOf(*completableFutures.toTypedArray())
            allFutures.join()
            globalError?.let { // if at least one credential verification failed => the whole process fails
                completionBlock(VCLResult.Failure(it))
            } ?: run {
                completionBlock(VCLResult.Success(true))
            }
        }
    }

    private fun verifyCredential(
        jwtCredential: VCLJwt,
        credentialType: VCLCredentialType,
        serviceTypes: VCLServiceTypes,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        if (
            serviceTypes.contains(VCLServiceType.IdentityIssuer) ||
            serviceTypes.contains(VCLServiceType.IdDocumentIssuer) ||
            serviceTypes.contains(VCLServiceType.NotaryIdDocumentIssuer) ||
            serviceTypes.contains(VCLServiceType.NotaryContactIssuer) ||
            serviceTypes.contains(VCLServiceType.ContactIssuer)
        ) {
            verifyIdentityIssuer(
                credentialType,
                completionBlock
            )
        } else {
            verifyRegularIssuer(
                jwtCredential,
                serviceTypes,
                completionBlock
            )
        }
    }

    private fun verifyIdentityIssuer(
        credentialType: VCLCredentialType,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        if (
            credentialType.issuerCategory == VCLServiceType.IdentityIssuer.value ||
            credentialType.issuerCategory == VCLServiceType.IdDocumentIssuer.value ||
            credentialType.issuerCategory == VCLServiceType.NotaryIdDocumentIssuer.value ||
            credentialType.issuerCategory == VCLServiceType.NotaryContactIssuer.value ||
            credentialType.issuerCategory == VCLServiceType.ContactIssuer.value
        ) {
            completionBlock(VCLResult.Success(true))
        } else {
            onError(
                VCLError(errorCode = VCLErrorCode.IssuerRequiresIdentityPermission.value),
                completionBlock
            )
        }
    }

    private fun verifyRegularIssuer(
        jwtCredential: VCLJwt,
        permittedServiceCategory: VCLServiceTypes,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        if (permittedServiceCategory.contains(VCLServiceType.NotaryIssuer)) {
            completionBlock(VCLResult.Success(true))
        } else if (permittedServiceCategory.contains(VCLServiceType.Issuer)) {
            Utils.getCredentialSubject(jwtCredential)?.let { credentialSubject ->
                retrieveContextFromCredentialSubject(credentialSubject)?.let { credentialSubjectContexts ->
                    resolveCredentialSubjectContexts(credentialSubjectContexts) { credentialSubjectContextsResult ->
                        credentialSubjectContextsResult.handleResult({ completeContexts ->
                            onResolveCredentialSubjectContexts(
                                credentialSubject,
                                jwtCredential,
                                completeContexts,
                                completionBlock
                            )
                        }, { error ->
                            onError(
                                VCLError(
                                    payload = error.payload,
                                    errorCode = VCLErrorCode.InvalidCredentialSubjectContext.value
                                ),
                                completionBlock
                            )
                        })
                    }
                } ?: run {
                    onError(
                        VCLError(errorCode = VCLErrorCode.InvalidCredentialSubjectContext.value),
                        completionBlock
                    )
                }
            } ?: run {
                onError(
                    VCLError(errorCode = VCLErrorCode.InvalidCredentialSubjectContext.value),
                    completionBlock
                )
            }
        } else {
            onError(
                VCLError(errorCode = VCLErrorCode.IssuerUnexpectedPermissionFailure.value),
                completionBlock
            )
        }
    }

    private fun retrieveContextFromCredentialSubject(credentialSubject: Map<*, *>): List<*>? {
        (credentialSubject[KeyContext] as? List<*>)?.let { credentialSubjectContexts ->
            return credentialSubjectContexts
        }
        (credentialSubject[KeyContext] as? String)?.let { credentialSubjectContext ->
            return listOf(credentialSubjectContext)
        }
        return null
    }

    private fun resolveCredentialSubjectContexts(
        credentialSubjectContexts: List<*>,
        completionBlock: (VCLResult<List<Map<*, *>>>) -> Unit
    ) {
        val completeContexts = CopyOnWriteArrayList(mutableListOf<Map<*, *>>())
        val completableFutures = credentialSubjectContexts.map { credentialSubjectContext ->
            CompletableFuture.supplyAsync {
                networkService.sendRequest(
                    endpoint = credentialSubjectContext as? String ?: "",
                    method = Request.HttpMethod.GET,
                    headers = listOf(
                        Pair(HeaderKeys.XVnfProtocolVersion, HeaderValues.XVnfProtocolVersion)
                    ),
                    completionBlock = { result ->
                        result.handleResult({ ldContextResponse ->
                            (ldContextResponse.payload.toJsonObject()?.toMap()
                                ?.get(KeyContext) as? Map<*, *>)?.let {
                                completeContexts.add(it)
                            } ?: run {
                                VCLLog.e(
                                    TAG,
                                    "Unexpected LD-Context payload:\n${ldContextResponse.payload}"
                                )
                            }
                        }, { error ->
                            VCLLog.e(
                                TAG,
                                "Error fetching $credentialSubjectContext:\n${error.toJsonObject()}"
                            )
                        })
                    })
            }
        }
        val allFutures = CompletableFuture.allOf(*completableFutures.toTypedArray())
        allFutures.join()

        if (completeContexts.isEmpty()) {
            onError(
                VCLError(errorCode = VCLErrorCode.InvalidCredentialSubjectContext.value),
                completionBlock = completionBlock
            )
        } else {
            completionBlock(VCLResult.Success(completeContexts))
        }
    }

    private fun onResolveCredentialSubjectContexts(
        credentialSubject: Map<*, *>,
        jwtCredential: VCLJwt,
        completeContexts: List<Map<*, *>>,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        (((credentialSubject[KeyType] as? List<*>)?.get(0) as? String) ?: credentialSubject[KeyType] as? String)?.let { credentialSubjectType ->
            var globalError: VCLError? = null
            var isCredentialVerified = false
            val completableFutures = completeContexts.map { completeContext ->
                CompletableFuture.supplyAsync {
                    val activeContext = (completeContext[credentialSubjectType] as? Map<*, *>)
                        ?.get(KeyContext) as? Map<*, *>
                        ?: completeContext
                    findKeyForPrimaryOrganizationValue(activeContext)?.let { K ->
                        Utils.getIdentifier(K, credentialSubject)?.let { did ->
//                            Comparing issuer.id instead of iss
//                            https://velocitycareerlabs.atlassian.net/browse/VL-6178?focusedCommentId=46933
//                            https://velocitycareerlabs.atlassian.net/browse/VL-6988
//                            if (jwtCredential.iss == did)
                            val credentialIssuerId = Utils.getCredentialIssuerId(jwtCredential)
                            VCLLog.d(
                                TAG,
                                "Comparing credentialIssuerId: ${credentialIssuerId ?: ""} with did: $did"
                            )
                            if (credentialIssuerId == did) {
                                isCredentialVerified = true
                            } else {
                                globalError =
                                    VCLError(errorCode = VCLErrorCode.IssuerRequiresNotaryPermission.value)
                            }
                        } ?: run {
                            globalError =
                                VCLError(errorCode = VCLErrorCode.IssuerRequiresNotaryPermission.value)

                            VCLLog.e(
                                TAG,
                                "DID NOT FOUND for K = $K and credentialSubject = $credentialSubject"
                            )
                        }
                    } ?: run {
//                        When K is null, the credential will pass these checks:
//                        https://velocitycareerlabs.atlassian.net/browse/VL-6181?focusedCommentId=44343
                        isCredentialVerified = true

                        VCLLog.d(
                            TAG,
                            "Key for primary organization NOT FOUND for active context:\n$activeContext"
                        )
                    }
                }
            }
            val allFutures = CompletableFuture.allOf(*completableFutures.toTypedArray())
            allFutures.join()

            if (isCredentialVerified)
                completionBlock(VCLResult.Success(true))
            else
                completionBlock(
                    VCLResult.Failure(
                        globalError
                            ?: VCLError(errorCode = VCLErrorCode.IssuerUnexpectedPermissionFailure.value)
                    )
                )
        } ?: run {
            onError(
                VCLError(errorCode = VCLErrorCode.InvalidCredentialSubjectType.value),
                completionBlock = completionBlock
            )
        }
    }

    private fun findKeyForPrimaryOrganizationValue(
        activeContext: Map<*, *>
    ): String? {
        activeContext.forEach { (key, value) ->
            (value as? Map<*, *>)?.let { valueMap ->
                if (valueMap[KeyId] == ValPrimaryOrganization ||
                    valueMap[KeyId] == ValPrimarySourceProfile
                ) {
                    return key as? String
                }
            }
        }
        return null
    }

    private fun <T> onError(
        error: VCLError,
        completionBlock: (VCLResult<T>) -> Unit
    ) {
        completionBlock(VCLResult.Failure(error))
    }

    companion object CodingKeys {
        const val KeyVC = "vc"
        const val KeyType = "type"

        const val KeyCredentialSubject = "credentialSubject"
        const val KeyContext = "@context"
        const val KeyId = "@id"

        const val ValPrimaryOrganization =
            "https://velocitynetwork.foundation/contexts#primaryOrganization"
        const val ValPrimarySourceProfile =
            "https://velocitynetwork.foundation/contexts#primarySourceProfile"
    }
}