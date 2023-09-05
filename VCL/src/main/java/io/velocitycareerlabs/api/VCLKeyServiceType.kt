/**
 * Created by Michael Avoyan on 23/10/22.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api

enum class VCLKeyServiceType(val value: String) {
    Local("local"),
    Remote("remote"),
    Injected("injected")
}