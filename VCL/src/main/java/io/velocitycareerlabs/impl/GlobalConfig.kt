/**
 * Created by Michael Avoyan on 3/12/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl

import io.velocitycareerlabs.BuildConfig
import io.velocitycareerlabs.api.VCLEnvironment
import io.velocitycareerlabs.api.VCLXVnfProtocolVersion

internal object GlobalConfig {
    const val VclPackage = BuildConfig.LIBRARY_PACKAGE_NAME

    var CurrentEnvironment = VCLEnvironment.PROD
    var XVnfProtocolVersion = VCLXVnfProtocolVersion.XVnfProtocolVersion1

    val IsDebug = BuildConfig.DEBUG

    val VersionName = BuildConfig.VERSION_NAME
    val VersionCode = BuildConfig.VERSION_CODE

    const val LogTagPrefix = "VCL "
    // TODO: Will be remotely configurable
    val IsLoggerOn get() = CurrentEnvironment != VCLEnvironment.PROD

    const val TypeJwt = "JWT"
}