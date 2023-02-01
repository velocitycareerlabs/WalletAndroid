/**
 * Created by Michael Avoyan on 09/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.CredentialManifestRepository
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.repositories.ResolveKidRepository
import io.velocitycareerlabs.impl.domain.usecases.CredentialManifestUseCase
import io.velocitycareerlabs.impl.extensions.encode

internal class CredentialManifestUseCaseImpl(
    private val credentialManifestRepository: CredentialManifestRepository,
    private val resolveKidRepository: ResolveKidRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val executor: Executor
): CredentialManifestUseCase {

    override fun getCredentialManifest(
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread {
            credentialManifestRepository.getCredentialManifest(
                credentialManifestDescriptor
            ) { jwtStrResult ->
                jwtStrResult.handleResult(
                    { jwtStr ->
                        onGetJwtSuccess(
                            jwtStr,
                            callingLooper,
                            completionBlock
                        )
                    },
                    { error ->
                        onError(error, callingLooper, completionBlock)
                    }
                )
            }
        }
    }

    private fun onGetJwtSuccess(
        jwtStr: String,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        jwtServiceRepository.decode(jwtStr) { jwtResult ->
            jwtResult.handleResult(
                { jwt ->
                    onDecodeJwtSuccess(jwt, callingLooper, completionBlock)
                },
                { error ->
                    onError(error, callingLooper, completionBlock)
                }
            )
        }
    }

    private fun onDecodeJwtSuccess(
        jwt: VCLJwt,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        jwt.header.keyID?.replace("#", "#".encode())?.let { keyID ->
            resolveKidRepository.getPublicKey(keyID) { publicKeyResult ->
                publicKeyResult.handleResult(
                    { publicKey ->
                        onResolvePublicKeySuccess(publicKey, jwt, callingLooper, completionBlock)
                    },
                    { error ->
                        onError(error, callingLooper, completionBlock)
                    }
                )
            }
        } ?: run {
            onError(VCLError("Empty KeyID"), callingLooper, completionBlock)
        }
    }

    private fun onResolvePublicKeySuccess(
        jwkPublic: VCLJwkPublic,
        jwt: VCLJwt,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        jwtServiceRepository.verifyJwt(jwt, jwkPublic)
        { verificationResult ->
            verificationResult.handleResult(
                { isVerified ->
                    onVerificationSuccess(isVerified, jwt, callingLooper, completionBlock)
                },
                { error ->
                    onError(error, callingLooper, completionBlock)
                }
            )
        }
    }

    private fun onVerificationSuccess(
        isVerified: Boolean,
        jwt: VCLJwt,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        if (isVerified) {
            executor.runOn(callingLooper) {
                completionBlock(VCLResult.Success(VCLCredentialManifest(jwt)))
            }
        } else {
            onError(VCLError("Failed to verify: $jwt"), callingLooper, completionBlock)
        }
    }

    private fun onError(
        error: VCLError,
        callingLooper: Looper?,
        completionBlock: (VCLResult<VCLCredentialManifest>) -> Unit
    ) {
        executor.runOn(callingLooper) {
            completionBlock(VCLResult.Failure(error))
        }
    }
}