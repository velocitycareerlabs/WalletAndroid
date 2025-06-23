/**
 * Created by Michael Avoyan on 03/06/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLDidDocument
import io.velocitycareerlabs.api.entities.VCLResult

internal interface ResolveDidDocumentRepository {
    fun resolveDidDocument(
        did: String,
        completionBlock: (VCLResult<VCLDidDocument>) -> Unit
    )
}