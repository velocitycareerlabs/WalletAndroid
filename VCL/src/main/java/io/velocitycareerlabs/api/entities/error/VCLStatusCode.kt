/**
 * Created by Michael Avoyan on 14/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities.error

enum class VCLStatusCode(val value: Int) {
    NetworkError(1),
    VerificationError(403),
    Undefined(-1)
}