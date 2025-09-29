/**
 * Created by Michael Avoyan on 28/09/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.verifiers.directissuerverification.repositories

import io.velocitycareerlabs.api.entities.VCLResult

interface CredentialSubjectContextRepository {
    fun getCredentialSubjectContext(
        credentialSubjectContextEndpoint: String,
        completionBlock: (VCLResult<Map<*, *>>) -> Unit
    )
}