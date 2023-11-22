/**
 * Created by Michael Avoyan on 4/12/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLPresentationRequestDescriptor
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken

internal interface PresentationRequestUseCase {
    fun getPresentationRequest(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        remoteCryptoServicesToken: VCLToken?,
        completionBlock: (VCLResult<VCLPresentationRequest>) -> Unit
    )
}