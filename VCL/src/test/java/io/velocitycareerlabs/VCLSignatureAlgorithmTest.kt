/**
 * Created by Michael Avoyan on 08/02/2024.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs

import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import org.junit.Test

class VCLSignatureAlgorithmTest {
    @Test
    fun fromStringTest() {
        assert(VCLSignatureAlgorithm.fromString(value = "P-256") == VCLSignatureAlgorithm.ES256)
        assert(VCLSignatureAlgorithm.fromString(value = "secp256k1") == VCLSignatureAlgorithm.SECP256k1)
        assert(VCLSignatureAlgorithm.fromString(value = "") == VCLSignatureAlgorithm.SECP256k1)
        assert(VCLSignatureAlgorithm.fromString(value = "wrong alg") == VCLSignatureAlgorithm.SECP256k1)
    }
}