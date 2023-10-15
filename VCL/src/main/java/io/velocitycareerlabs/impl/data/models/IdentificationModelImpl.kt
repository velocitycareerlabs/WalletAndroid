/**
 * Created by Michael Avoyan on 18/07/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.models

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.domain.models.IdentificationModel
import io.velocitycareerlabs.impl.domain.usecases.IdentificationSubmissionUseCase

internal class IdentificationModelImpl(
    private val identificationSubmissionUseCase: IdentificationSubmissionUseCase
): IdentificationModel {
    override var data: VCLToken? = null

    override fun submit(
        identificationSubmission: VCLIdentificationSubmission,
        didJwk: VCLDidJwk,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLSubmissionResult>) -> Unit
    ) {
        identificationSubmissionUseCase.submit(
            submission = identificationSubmission,
            didJwk = didJwk,
            remoteCryptoServicesToken = remoteCryptoServicesToken
        ) { result ->
            result.handleResult({ data = result.data?.exchangeToken }, { })
            completionBlock(result)
        }
    }
}