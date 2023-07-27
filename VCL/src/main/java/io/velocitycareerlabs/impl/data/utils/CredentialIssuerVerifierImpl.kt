/**
 * Created by Michael Avoyan on 04/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.utils

import io.velocitycareerlabs.api.entities.VCLCredentialType
import io.velocitycareerlabs.api.entities.VCLError
import io.velocitycareerlabs.api.entities.VCLErrorCode
import io.velocitycareerlabs.api.entities.VCLFinalizeOffersDescriptor
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLServiceType
import io.velocitycareerlabs.api.entities.VCLServiceTypes
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.repositories.HeaderKeys
import io.velocitycareerlabs.impl.data.repositories.HeaderValues
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.domain.models.CredentialTypesModel
import io.velocitycareerlabs.impl.domain.utils.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toMap
import io.velocitycareerlabs.impl.utils.VCLLog
import java.util.concurrent.CompletableFuture

internal class CredentialIssuerVerifierImpl(
    private val credentialTypesModel: CredentialTypesModel,
    private val networkService: NetworkService
): CredentialIssuerVerifier {
    val TAG = CredentialIssuerVerifierImpl::class.java.simpleName

    override fun verifyCredentials(
        jwtEncodedCredentials: List<String>,
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        if (jwtEncodedCredentials.isEmpty()) /* nothing to verify */ {
            completionBlock(VCLResult.Success(true))
        } else if (finalizeOffersDescriptor.serviceTypes.all.isEmpty()) {
            completionBlock(VCLResult.Failure(VCLError(errorCode = VCLErrorCode.CredentialTypeNotRegistered.value)))
        } else {
            var globalError: VCLError? = null
            val completableFutures = jwtEncodedCredentials.map { encodedJwtCredential ->
                CompletableFuture.supplyAsync {
                    val jwtCredential = VCLJwt(encodedJwt = encodedJwtCredential)
                    Utils.getCredentialType(jwtCredential)?.let { credentialTypeName ->
                        credentialTypesModel.credentialTypeByTypeName(credentialTypeName)
                            ?.let { credentialType ->
                                verifyCredential(
                                    jwtCredential,
                                    credentialType,
                                    finalizeOffersDescriptor.serviceTypes
                                ) {
                                    it.handleResult({ verified ->
//                                        do nothing
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
            globalError?.let {
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
        if (serviceTypes.contains(VCLServiceType.IdentityIssuer)) {
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
        if (credentialType.issuerCategory == VCLServiceType.IdentityIssuer.value) {
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
                (credentialSubject[KeyContext] as? List<String>)?.let { credentialSubjectContexts ->
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

    private fun resolveCredentialSubjectContexts(
        credentialSubjectContexts: List<String>,
        completionBlock: (VCLResult<List<Map<String, Any>>>) -> Unit
    ) {
        val completeContexts = mutableListOf<Map<String, Any>>()
        val completableFutures = credentialSubjectContexts.map { credentialSubjectContext ->
            CompletableFuture.supplyAsync {
                networkService.sendRequest(
                    endpoint = credentialSubjectContext,
                    method = Request.HttpMethod.GET,
                    headers = listOf(
                        Pair(
                            HeaderKeys.XVnfProtocolVersion,
                            HeaderValues.XVnfProtocolVersion
                        )
                    ),
                    completionBlock = { result ->
                        result.handleResult({ ldContextResponse ->
                            ldContextResponse.payload.toJsonObject()?.toMap()?.let {
                                completeContexts.add(it)
                            }
                        }, {
                            VCLLog.e(
                                TAG,
                                "Error fetching $credentialSubjectContext:\n${it?.toJsonObject()}"
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
        completeContexts: List<Map<String, Any>>,
        completionBlock: (VCLResult<Boolean>) -> Unit
    ) {
        (credentialSubject[KeyType] as? String)?.let { credentialSubjectType ->
            var globalError: VCLError? = null
            val completableFutures = completeContexts.map { completeContext ->
                CompletableFuture.supplyAsync {
                    (((completeContext[KeyContext] as? Map<*, *>)
                        ?.get(credentialSubjectType) as? Map<*, *>)
                        ?.get(KeyContext) as? Map<*, *>)
                        ?.let { context ->
                            findKeyForPrimaryOrganizationValue(context)?.let { K ->
                                ((credentialSubject[K] as? Map<*, *>)?.get(KeyIdentifier) as? String)?.let { did ->
                                    if (jwtCredential.iss == did) {
//                                        do nothing
                                    } else {
                                        globalError =
                                            VCLError(errorCode = VCLErrorCode.IssuerRequiresNotaryPermission.value)
                                    }
                                } ?: run {
                                    globalError =
                                        VCLError(errorCode = VCLErrorCode.IssuerRequiresNotaryPermission.value)
                                }
                            } ?: run {
                                globalError =
                                    VCLError(errorCode = VCLErrorCode.InvalidCredentialSubjectType.value)
                            }
                        } ?: run {
                        globalError =
                            VCLError(errorCode = VCLErrorCode.InvalidCredentialSubjectContext.value)
                    }
                }
            }
            val allFutures = CompletableFuture.allOf(*completableFutures.toTypedArray())
            allFutures.join()
            globalError?.let {
                completionBlock(VCLResult.Failure(it))
            } ?: run {
                completionBlock(VCLResult.Success(true))
            }
        } ?: run {
            onError(
                VCLError(errorCode = VCLErrorCode.InvalidCredentialSubjectType.value),
                completionBlock = completionBlock
            )
        }
    }

    private fun findKeyForPrimaryOrganizationValue(context: Map<*, *>): String? {
        var retVal: String? = null
        context.forEach { (key, value) ->
            if ((value as? Map<*, *>)?.get(CodingKeys.KeyId) as? String == CodingKeys.ValPrimaryOrganization) {
                retVal = key as? String
            }
        }
        return retVal
    }

    private fun <T>onError(
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
        const val KeyIdentifier = "identifier"

        const val ValPrimaryOrganization =
            "https://velocitynetwork.foundation/contexts#primaryOrganization"
    }
}