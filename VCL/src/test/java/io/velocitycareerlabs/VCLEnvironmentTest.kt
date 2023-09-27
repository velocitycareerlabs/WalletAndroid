/**
 * Created by Michael Avoyan on 26/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs

import io.velocitycareerlabs.api.VCLEnvironment
import org.junit.Test

class VCLEnvironmentTest {
    @Test
    fun fromStringTest() {
        assert(VCLEnvironment.fromString(value = "prod") == VCLEnvironment.Prod)
        assert(VCLEnvironment.fromString(value = "staging") == VCLEnvironment.Staging)
        assert(VCLEnvironment.fromString(value = "qa") == VCLEnvironment.Qa)
        assert(VCLEnvironment.fromString(value = "dev") == VCLEnvironment.Dev)
        assert(VCLEnvironment.fromString(value = "123") == VCLEnvironment.Prod) //default
    }
}