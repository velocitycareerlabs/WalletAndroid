package io.velocitycareerlabs.api.entities

/**
 * Created by Michael Avoyan on 30/05/2021.
 */
data class VCLExchangeDescriptor(
    val presentationSubmission: VCLPresentationSubmission,
    val submissionResult: VCLSubmissionResult
    ) {

    val processUri: String get() = presentationSubmission.progressUri
    val did: String get() = presentationSubmission.iss
    val exchangeId: String get() = submissionResult.exchange.id
    val token: VCLToken get() = submissionResult.token

    companion object CodingKeys {
        const val KeyExchangeId = "exchange_id"
    }
}
