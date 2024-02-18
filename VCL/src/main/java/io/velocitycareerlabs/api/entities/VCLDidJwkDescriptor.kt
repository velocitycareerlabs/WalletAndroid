/**
 * Created by Michael Avoyan on 15/02/24.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import io.velocitycareerlabs.api.VCLSignatureAlgorithm

data class VCLDidJwkDescriptor(
    val signatureAlgorithm: VCLSignatureAlgorithm = VCLSignatureAlgorithm.ES256,
    val remoteCryptoServicesToken: VCLToken? = null
)