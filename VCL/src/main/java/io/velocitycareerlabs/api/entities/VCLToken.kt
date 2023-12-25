/**
 * Created by Michael Avoyan on 18/07/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.api.entities

data class VCLToken(
    /**
     * token value represented as jwt string
     */
    val value: String
) {
    /**
     * token value represented as VCLJwt object
     */
    val jwtValue = VCLJwt(value)

    constructor(jwtValue: VCLJwt) : this(jwtValue.encodedJwt ?: "")

    /**
     * token expiration period in milliseconds
     */
    val expiresIn: Long?
        get() = jwtValue.signedJwt?.payload?.toJSONObject()?.get(CodingKeys.KeyExp) as? Long

    companion object CodingKeys {
        const val KeyExp = "exp"
    }
}