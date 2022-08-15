package io.velocitycareerlabs.api.entities

/**
 * Created by Michael Avoyan on 09/05/2021.
 */
open class VCLCredentialManifestDescriptor(
    val uri: String,
    val credentialTypes: List<String>? = null,
    val pushDelegate: VCLPushDelegate? = null
) {
    companion object CodingKeys {
        const val KeyId = "id"
        const val KeyDid = "did"
        const val KeyCredentialTypes = "credential_types"
        const val KeyPushDelegatePushUrl = "push_delegate.push_url"
        const val KeyPushDelegatePushToken = "push_delegate.push_token"

        const val KeyCredentialId = "credentialId"
        const val KeyRefresh = "refresh"
    }

    open val endpoint get() =  uri
}