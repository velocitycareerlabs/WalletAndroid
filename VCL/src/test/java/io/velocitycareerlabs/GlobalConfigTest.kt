/**
 * Created by Michael Avoyan on 10/28/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs

import io.velocitycareerlabs.api.VCLEnvironment
import io.velocitycareerlabs.impl.GlobalConfig
import org.junit.After
import org.junit.Before
import org.junit.Test

class GlobalConfigTest {
    @Before
    fun setUp() {
    }

    @Test
    fun testDevEnvironment() {
        GlobalConfig.CurrentEnvironment = VCLEnvironment.Dev
        assert(GlobalConfig.IsLoggerOn)

        GlobalConfig.IsDebugOn = true
        assert(GlobalConfig.IsLoggerOn)

        GlobalConfig.IsDebugOn = false
        assert(GlobalConfig.IsLoggerOn)
    }

    @Test
    fun testQaEnvironment() {
        GlobalConfig.CurrentEnvironment = VCLEnvironment.Qa
        assert(GlobalConfig.IsLoggerOn)

        GlobalConfig.IsDebugOn = true
        assert(GlobalConfig.IsLoggerOn)

        GlobalConfig.IsDebugOn = false
        assert(GlobalConfig.IsLoggerOn)
    }

    @Test
    fun testStagingEnvironment() {
        GlobalConfig.CurrentEnvironment = VCLEnvironment.Staging
        assert(!GlobalConfig.IsLoggerOn)

        GlobalConfig.IsDebugOn = true
        assert(GlobalConfig.IsLoggerOn)

        GlobalConfig.IsDebugOn = false
        assert(!GlobalConfig.IsLoggerOn)
    }

    @Test
    fun testProdEnvironment() {
        GlobalConfig.CurrentEnvironment = VCLEnvironment.Prod
        assert(!GlobalConfig.IsLoggerOn)

        GlobalConfig.IsDebugOn = true
        assert(GlobalConfig.IsLoggerOn)

        GlobalConfig.IsDebugOn = false
        assert(!GlobalConfig.IsLoggerOn)
    }

    @Test
    fun testPackageName() {
        assert(GlobalConfig.VclPackage == "io.velocitycareerlabs")
    }

    @Test
    fun testLogTagPrefix() {
        assert(GlobalConfig.LogTagPrefix == "VCL ")
    }

    @After
    fun tearDown() {
    }
}