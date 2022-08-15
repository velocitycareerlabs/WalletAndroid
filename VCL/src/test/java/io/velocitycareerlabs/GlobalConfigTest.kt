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
        GlobalConfig.CurrentEnvironment = VCLEnvironment.DEV

        assert(GlobalConfig.IsLoggerOn)
    }

    @Test
    fun testStagingEnvironment() {
        GlobalConfig.CurrentEnvironment = VCLEnvironment.STAGING

        assert(GlobalConfig.IsLoggerOn)
    }

    @Test
    fun testProdEnvironment() {
        GlobalConfig.CurrentEnvironment = VCLEnvironment.PROD

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