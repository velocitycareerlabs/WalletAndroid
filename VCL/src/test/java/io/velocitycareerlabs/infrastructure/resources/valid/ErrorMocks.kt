/**
 * Created by Michael Avoyan on 08/03/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

class ErrorMocks {

    companion object {
        val Payload = "{\"error\":\"Bad Request\",\"errorCode\": \"proof_jwt_is_required\",\"requestId\": \"some_request_id\",\"message\":\"proof.jwt is missing\",\"statusCode\": 400}"
        val Error = "Bad Request"
        val ErrorCode = "proof_jwt_is_required"
        val RequestId = "some_request_id"
        val Message = "proof.jwt is missing"
        val StatusCode = 400
    }
}