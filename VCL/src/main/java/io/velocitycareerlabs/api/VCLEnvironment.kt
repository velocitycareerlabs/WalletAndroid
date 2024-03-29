/**
 * Created by Michael Avoyan on 9/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api

enum class VCLEnvironment(val value: String) {
    Prod("prod"),
    Staging("staging"),
    Qa("qa"),
    Dev("dev");

    companion object {
        fun fromString(value: String) =
            when (value) {
                Prod.value -> Prod
                Staging.value -> Staging
                Qa.value -> Qa
                Dev.value -> Dev
                else -> Prod
            }
    }
}