package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.*

/**
 * Created by Michael Avoyan on 4/11/21.
 */
internal interface SubmissionRepository {
    fun submit(submission: VCLSubmission,
               jwt: VCLJWT,
               completionBlock: (VCLResult<VCLSubmissionResult>) -> Unit)
}