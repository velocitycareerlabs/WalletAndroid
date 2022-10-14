/**
 * Created by Michael Avoyan on 18/07/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.models

import io.velocitycareerlabs.api.entities.VCLResult

internal interface Initializable<T> {
    fun initialize(completionBlock: (VCLResult<T>) -> Unit)
}