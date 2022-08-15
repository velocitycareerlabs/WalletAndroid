package io.velocitycareerlabs.impl.domain.models

import io.velocitycareerlabs.api.entities.*

/**
 * Created by Michael Avoyan on 18/07/2021.
 */
internal interface IdentificationModel: Model<VCLToken> {
    fun submit(identificationSubmission: VCLIdentificationSubmission,
               completionBlock: (VCLResult<VCLIdentificationSubmissionResult>) -> Unit)
}