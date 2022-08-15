package io.velocitycareerlabs.impl.data.models

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.models.IdentificationModel
import io.velocitycareerlabs.impl.domain.usecases.IdentificationSubmissionUseCase

/**
 * Created by Michael Avoyan on 18/07/2021.
 */
internal class IdentificationModelImpl(
    private val identificationSubmissionUseCase: IdentificationSubmissionUseCase
): IdentificationModel {
    override var data: VCLToken? = null

    override fun submit(identificationSubmission: VCLIdentificationSubmission,
                        completionBlock: (VCLResult<VCLIdentificationSubmissionResult>) -> Unit){
        identificationSubmissionUseCase.submit(
            identificationSubmission,
        ) { result ->
            result.handleResult({ data = result.data?.token }, { })
            completionBlock(result)
        }
    }
}