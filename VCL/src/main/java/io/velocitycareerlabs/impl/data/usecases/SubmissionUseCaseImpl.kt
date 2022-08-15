package io.velocitycareerlabs.impl.data.usecases

import android.os.Looper
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import io.velocitycareerlabs.impl.domain.repositories.JwtServiceRepository
import io.velocitycareerlabs.impl.domain.repositories.SubmissionRepository
import io.velocitycareerlabs.impl.domain.usecases.SubmissionUseCase

/**
 * Created by Michael Avoyan on 8/04/21.
 */
internal class SubmissionUseCaseImpl(
    private val submissionRepository: SubmissionRepository,
    private val jwtServiceRepository: JwtServiceRepository,
    private val executor: Executor
):  SubmissionUseCase {
    override fun submit(submission: VCLSubmission,
                        completionBlock: (VCLResult<VCLSubmissionResult>) -> Unit) {
        val callingLooper = Looper.myLooper()
        executor.runOnBackgroundThread {
            jwtServiceRepository.generateSignedJwt(
                submission.payload,
                submission.iss
            ) { signedJwtResult ->
                signedJwtResult.handleResult(
                    { jwt ->
                        submissionRepository.submit(
                            submission,
                            jwt
                        ) { submissionResult ->
                            executor.runOn(callingLooper) { completionBlock(submissionResult) }
                        }
                    },
                    { error ->
                        executor.runOn(callingLooper) { completionBlock(VCLResult.Failure(error)) }
                    }
                )
            }
        }
    }
}