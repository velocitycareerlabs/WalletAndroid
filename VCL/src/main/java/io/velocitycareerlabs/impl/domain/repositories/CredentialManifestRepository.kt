package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptor

/**
 * Created by Michael Avoyan on 09/05/2021.
 */
internal interface CredentialManifestRepository {
    fun getCredentialManifest(credentialManifestDescriptor: VCLCredentialManifestDescriptor,
                              completionBlock:(VCLResult<String>) -> Unit)
}