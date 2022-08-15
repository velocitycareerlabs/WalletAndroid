package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLSubmission
import io.velocitycareerlabs.api.entities.VCLSubmissionResult

internal interface SubmissionUseCase {
    fun submit(submission: VCLSubmission,
               completionBlock: (VCLResult<VCLSubmissionResult>) -> Unit)
}