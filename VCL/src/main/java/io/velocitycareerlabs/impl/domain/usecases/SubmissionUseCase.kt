/**
 * Created by Michael Avoyan on 4/12/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLSubmission
import io.velocitycareerlabs.api.entities.VCLSubmissionResult
import io.velocitycareerlabs.api.entities.VCLToken

internal interface SubmissionUseCase {
    fun submit(
        submission: VCLSubmission,
        didJwk: VCLDidJwk? = null,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLSubmissionResult>) -> Unit
    )
}