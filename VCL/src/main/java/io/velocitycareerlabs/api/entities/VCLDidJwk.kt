/**
 * Created by Michael Avoyan on 26/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import com.nimbusds.jose.jwk.ECKey
import io.velocitycareerlabs.impl.extensions.decodeBase64
import io.velocitycareerlabs.impl.extensions.encodeToBase64URL

data class VCLDidJwk(
    /**
     * The did in jwk format encoded to Base64 format - the holder did
     */
    val did: String,
    /**
     * The public JWK
     */
    val publicJwk: VCLPublicJwk,
    /**
     * The kid of jwt - did:jwk suffixed with #0
     */
    val kid: String,
    /**
     * The id of private key save in the key store
     */
    val keyId: String
) {
    val curve: String get() = publicJwk.curve

    companion object Utils {
        const val DidJwkPrefix = "did:jwk:"
        const val DidJwkSuffix = "#0"

        fun generateDidJwk(ecKey: ECKey) =
            "${DidJwkPrefix}${ecKey.toPublicJWK().toString().encodeToBase64URL()}"

        fun generateKidFromDidJwk(ecKey: ECKey) =
            "${generateDidJwk(ecKey = ecKey)}${DidJwkSuffix}"

        const val KeyDid = "did"
        const val KeyKid = "kid"
        const val KeyKeyId = "keyId"
    }
}
