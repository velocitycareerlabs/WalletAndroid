/**
 * Created by Michael Avoyan on 8/04/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.repositories.SubmissionRepository
import io.velocitycareerlabs.impl.domain.usecases.SubmissionUseCase

internal class SubmissionUseCaseImpl(
    private val submissionRepository: SubmissionRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val executor: Executor
):  SubmissionUseCase {
    override fun submit(
        submission: VCLSubmission,
        didJwk: VCLDidJwk?,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLSubmissionResult>) -> Unit
    ) {
        executor.runOnBackground {
            jwtServiceRepository.generateSignedJwt(
                kid = didJwk?.kid,
                jwtDescriptor = VCLJwtDescriptor(
                    keyId = didJwk?.keyId,
                    payload = submission.payload,
                    jti = submission.jti,
                    iss = submission.iss
                ),
                remoteCryptoServicesToken = remoteCryptoServicesToken,
                completionBlock = { signedJwtResult ->
                    signedJwtResult.handleResult(
                        { jwt ->
                            submissionRepository.submit(
                                submission = submission,
                                jwt = jwt
                            ) { submissionResult ->
                                executor.runOnMain { completionBlock(submissionResult) }
                            }
                        },
                        { error ->
                            executor.runOnMain { completionBlock(VCLResult.Failure(error)) }
                        }
                    )
                }
            )
        }
    }
}