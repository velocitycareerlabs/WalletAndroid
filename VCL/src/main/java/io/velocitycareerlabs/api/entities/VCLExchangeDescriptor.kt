/**
 * Created by Michael Avoyan on 30/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

data class VCLExchangeDescriptor(
    val presentationSubmission: VCLPresentationSubmission,
    val submissionResult: VCLSubmissionResult
    ) {

    val processUri: String get() = presentationSubmission.progressUri
    val exchangeId: String get() = submissionResult.exchange.id
    val sessionToken: VCLToken get() = submissionResult.sessionToken

    companion object CodingKeys {
        const val KeyExchangeId = "exchange_id"
    }
}
