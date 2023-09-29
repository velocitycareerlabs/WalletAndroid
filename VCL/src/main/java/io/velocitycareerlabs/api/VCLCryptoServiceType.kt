/**
 * Created by Michael Avoyan on 02/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api

enum class VCLCryptoServiceType(val value: String) {
    Local("local"),
    Remote("remote"),
    Injected("injected");

    companion object {
        fun fromString(value: String) =
            when (value) {
                Local.value -> Local
                Remote.value -> Remote
                Injected.value -> Injected
                else -> Local
            }
    }
}