/**
 * Created by Michael Avoyan on 04/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.impl.data.verifiers.directissuerverification

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
import io.velocitycareerlabs.impl.data.verifiers.directissuerverification.VerificationUtils
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.models.CredentialTypesModel
import io.velocitycareerlabs.impl.domain.verifiers.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toMap
import io.velocitycareerlabs.impl.utils.VCLLog
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.get

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
            return
        }
        if (finalizeOffersDescriptor.serviceTypes.all.isEmpty()) {
            completionBlock(VCLResult.Failure(VCLError(errorCode = VCLErrorCode.CredentialTypeNotRegistered.value)))
            return
        }

        val globalErrorStorage = GlobalErrorStorage()

        val completableFutures = jwtCredentials.map { jwtCredential ->
            CompletableFuture.supplyAsync {
                val credentialTypeName =
                    VerificationUtils.Companion.getCredentialType(jwtCredential)
                val credentialType =
                    credentialTypesModel.credentialTypeByTypeName(credentialTypeName ?: "")
                if (credentialTypeName == null || credentialType == null) {
                    globalErrorStorage.update(
                        VCLError(errorCode = VCLErrorCode.CredentialTypeNotRegistered.value)
                    )
                    return@supplyAsync
                }

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
                        globalErrorStorage.update(error)
                    })
                }
            }
        }

        val allFutures = CompletableFuture.allOf(*completableFutures.toTypedArray())
        allFutures.join()
        globalErrorStorage.get()?.let {
            // if at least one credential verification failed => the whole process fails
            completionBlock(VCLResult.Failure(it))
        } ?: run {
            completionBlock(VCLResult.Success(true))
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
            VerificationUtils.getCredentialSubjectFromCredential(jwtCredential)?.let { credentialSubject ->
                VerificationUtils.getContextsFromCredential(jwtCredential)?.let { credentialContexts ->
                    resolveCredentialSubjectContexts(credentialContexts) { credentialSubjectContextsResult ->
                        credentialSubjectContextsResult.handleResult({ completeContexts ->
                            onResolveCredentialContexts(
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

    private fun resolveCredentialSubjectContexts(
        credentialSubjectContexts: List<*>,
        completionBlock: (VCLResult<List<Map<*, *>>>) -> Unit
    ) {
        val completeContextsStorage = CompleteContextsStorage()

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
                            ldContextResponse.payload.toJsonObject()?.toMap()?.let {
                                completeContextsStorage.append(it)
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

        if (completeContextsStorage.isEmpty()) {
            onError(
                VCLError(errorCode = VCLErrorCode.InvalidCredentialSubjectContext.value),
                completionBlock = completionBlock
            )
        } else {
            completionBlock(VCLResult.Success(completeContextsStorage.get()))
        }
    }

    private fun onResolveCredentialContexts(
        credentialSubject: Map<*, *>,
        jwtCredential: VCLJwt,
        completeContexts: List<Map<*, *>>,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        val credentialSubjectType = VerificationUtils.extractCredentialSubjectType(credentialSubject)
        if (credentialSubjectType == null) {
            onError(
                VCLError(errorCode = VCLErrorCode.InvalidCredentialSubjectType.value),
                completionBlock
            )
            return
        }

        val globalErrorStorage = GlobalErrorStorage()
        val isCredentialVerifiedStorage = IsCredentialVerifiedStorage()

        val completableFutures = completeContexts.map { completeContext ->
            CompletableFuture.supplyAsync {

                val activeContext = VerificationUtils.extractActiveContext(completeContext, credentialSubjectType)

                val K = VerificationUtils.findKeyForPrimaryOrganizationValue(activeContext)
                if (K == null) {
                    // Case: no primary organization key found → accept
                    // When K is null, the credential will pass these checks:
//                        https://velocitycareerlabs.atlassian.net/browse/VL-6181?focusedCommentId=44343
                    isCredentialVerifiedStorage.update(true)
                    VCLLog.d(TAG, "No primary organization key in context: $activeContext")
                    return@supplyAsync
                }

                val did = VerificationUtils.getIdentifier(K, credentialSubject)
                if (did == null) {
                    globalErrorStorage.update(
                        VCLError(errorCode = VCLErrorCode.IssuerRequiresNotaryPermission.value)
                    )
                    VCLLog.e(TAG, "DID not found for K = $K and subject = $credentialSubject")
                    return@supplyAsync
                }

                val issuerId = VerificationUtils.getCredentialIssuerId(jwtCredential)
                VCLLog.d(TAG, "Comparing credentialIssuerId: ${issuerId ?: ""} with did: $did")

//                    Comparing issuer.id instead of iss
//                    https://velocitycareerlabs.atlassian.net/browse/VL-6178?focusedCommentId=46933
//                    https://velocitycareerlabs.atlassian.net/browse/VL-6988
//                    if (jwtCredential.iss == did)
                if (issuerId == did) {
                    isCredentialVerifiedStorage.update(true)
                } else {
                    globalErrorStorage.update(
                        VCLError(errorCode = VCLErrorCode.IssuerRequiresNotaryPermission.value)
                    )
                }
            }
        }
        val allFutures = CompletableFuture.allOf(*completableFutures.toTypedArray())
        allFutures.join()

        val error = globalErrorStorage.get()
        if (error != null) {
            completionBlock(VCLResult.Failure(error))
        } else if (isCredentialVerifiedStorage.get()) {
            completionBlock(VCLResult.Success(true))
        } else {
            completionBlock(
                VCLResult.Failure(
                    VCLError(errorCode = VCLErrorCode.IssuerUnexpectedPermissionFailure.value)
                )
            )
        }
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