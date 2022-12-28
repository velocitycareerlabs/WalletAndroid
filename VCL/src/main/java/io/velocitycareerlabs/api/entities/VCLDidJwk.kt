/**
 * Created by Michael Avoyan on 26/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

data class VCLDidJwk(
    val value: String
) {
    companion object {
        const val DidJwkPrefix = "did:jwk:"
    }
}
