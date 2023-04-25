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
    val didJwk: VCLDidJwk? = null,
    /**
     * The kid of the owner, UUID by default
     */
    val kid: String = UUID.randomUUID().toString(),
    /**
     * Json formatted payload
     */
    val payload: JSONObject,
    /**
     * JWT ID
     */
     val jti:String,
    /**
     * The did of the wallet owner
     */
    val iss: String,
    /**
     * The issuer DID
     */
    val aud: String? = null,
)
