/**
 * Created by Michael Avoyan on 3/12/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl

import io.velocitycareerlabs.BuildConfig
import io.velocitycareerlabs.api.VCLEnvironment
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.VCLXVnfProtocolVersion

internal object GlobalConfig {
    const val VclPackage = BuildConfig.LIBRARY_PACKAGE_NAME

    var CurrentEnvironment = VCLEnvironment.Prod
    var XVnfProtocolVersion = VCLXVnfProtocolVersion.XVnfProtocolVersion1
    var SignatureAlgorithm = VCLSignatureAlgorithm.SECP256k1

    var IsDebugOn = false //BuildConfig.DEBUG

    val VersionName = BuildConfig.VERSION_NAME
    val VersionCode = BuildConfig.VERSION_CODE

    const val LogTagPrefix = "VCL "

    val IsLoggerOn get() = (CurrentEnvironment != VCLEnvironment.Staging && CurrentEnvironment != VCLEnvironment.Prod) || IsDebugOn

    const val TypeJwt = "JWT"
}