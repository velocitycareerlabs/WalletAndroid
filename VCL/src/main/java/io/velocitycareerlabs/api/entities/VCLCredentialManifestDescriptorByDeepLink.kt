/**
 * Created by Michael Avoyan on 8/05/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

class VCLCredentialManifestDescriptorByDeepLink(
    deepLink: VCLDeepLink,
    issuingType: VCLIssuingType = VCLIssuingType.Career,
): VCLCredentialManifestDescriptor(
    uri = deepLink.requestUri,
    issuingType = issuingType,
    vendorOriginContext = deepLink.vendorOriginContext,
    deepLink = deepLink
)