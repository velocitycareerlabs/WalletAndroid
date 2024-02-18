/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities.initialization

import io.velocitycareerlabs.api.VCLCryptoServiceType
import io.velocitycareerlabs.api.VCLSignatureAlgorithm

data class VCLCryptoServicesDescriptor(
    val cryptoServiceType: VCLCryptoServiceType = VCLCryptoServiceType.Local,
    val signatureAlgorithm: VCLSignatureAlgorithm = VCLSignatureAlgorithm.SECP256k1,
    val injectedCryptoServicesDescriptor: VCLInjectedCryptoServicesDescriptor? = null,
    val remoteCryptoServicesUrlsDescriptor: VCLRemoteCryptoServicesUrlsDescriptor? = null
)