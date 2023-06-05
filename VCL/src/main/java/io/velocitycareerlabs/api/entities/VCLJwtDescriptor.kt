/**
 * Created by Michael Avoyan on 26/12/2022.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

import org.json.JSONObject
import java.util.UUID

data class VCLJwtDescriptor(
    /**
     * The Id of the existing private key to sign with
     */
    val keyId: String? = null,
    /**
     * Json formatted payload
     */
    val payload: JSONObject? = null,
    /**
     * JWT ID
     */
    val jti: String = UUID.randomUUID().toString(),
    /**
     * The did of the wallet owner
     */
    val iss: String,
    /**
     * The issuer DID
     */
    val aud: String? = null
)
