package io.velocitycareerlabs.api.entities

/**
 * Created by Michael Avoyan on 8/05/21.
 */
class VCLCredentialManifestDescriptorByDeepLink(
    deepLink: VCLDeepLink
): VCLCredentialManifestDescriptor(
    uri = deepLink.requestUri
)