/**
 * Created by Michael Avoyan on 08/02/2024.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs

import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import org.junit.Assert.assertEquals
import org.junit.Test

class VCLSignatureAlgorithmTest {
    @Test
    fun fromStringAcceptsCurveNames() {
        assertEquals(VCLSignatureAlgorithm.ES256, VCLSignatureAlgorithm.fromString(value = "P-256"))
        assertEquals(VCLSignatureAlgorithm.SECP256k1, VCLSignatureAlgorithm.fromString(value = "secp256k1"))
    }

    @Test
    fun fromStringAcceptsJwsAlgorithmNames() {
        assertEquals(VCLSignatureAlgorithm.ES256, VCLSignatureAlgorithm.fromString(value = "ES256"))
        assertEquals(VCLSignatureAlgorithm.SECP256k1, VCLSignatureAlgorithm.fromString(value = "ES256K"))
    }

    @Test
    fun fromStringDefaultsEmptyAndUnknownValuesToES256() {
        assertEquals(VCLSignatureAlgorithm.ES256, VCLSignatureAlgorithm.fromString(value = ""))
        assertEquals(VCLSignatureAlgorithm.ES256, VCLSignatureAlgorithm.fromString(value = "wrong alg"))
    }
}