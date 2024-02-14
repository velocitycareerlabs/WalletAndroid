/**
 * Created by Michael Avoyan on 08/02/2024.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.Curve

enum class VCLSignatureAlgorithm(val value: String) {
    ES256("P-256"),
    SECP256k1("secp256k1");

    val curve: Curve get() = when(this) {
        ES256 -> Curve.P_256
        SECP256k1 -> Curve.SECP256K1
    }

    val jwsAlgorithm: JWSAlgorithm get() = when(this) {
        ES256 -> JWSAlgorithm.ES256
        SECP256k1 -> JWSAlgorithm.ES256K
    }

    companion object {
        fun fromString(value: String) =
            when (value) {
                ES256.value -> ES256
                SECP256k1.value -> SECP256k1
                else -> SECP256k1
            }
    }
}