package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLCredentialManifest
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLCredentialManifestDescriptor

/**
 * Created by Michael Avoyan on 09/05/2021.
 *
 * Retrieves the credential manifest for the issuing exchange.
 * Inside the manifest are issuer details, credential schemas and a presentation definition
 */
internal interface CredentialManifestUseCase {
    fun getCredentialManifest(credentialManifestDescriptor: VCLCredentialManifestDescriptor,
                              completionBlock:(VCLResult<VCLCredentialManifest>) -> Unit)
}