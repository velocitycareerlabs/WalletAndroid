/**
 * Created by Michael Avoyan on 4/5/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLPresentationRequestDescriptor

internal interface PresentationRequestRepository {
    fun getPresentationRequest(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        completionBlock: (VCLResult<String>) -> Unit
    )
}