/**
 * Created by Michael Avoyan on 04/07/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

internal enum class VCLErrorCode(val value: String) {
    // Initialization
    RemoteServicesUrlsNotFount("remote_services_urls_not_found"),
    InjectedServicesNotFount("injected_services_not_found"),
    // Credential issuer verification error codes:
    CredentialTypeNotRegistered("credential_type_not_registered"),
    IssuerRequiresIdentityPermission("issuer_requires_identity_permission"),
    IssuerRequiresNotaryPermission("issuer_requires_notary_permission"),
    InvalidCredentialSubjectType("invalid_credential_subject_type"),
    InvalidCredentialSubjectContext("invalid_credential_subject_context"),
    IssuerUnexpectedPermissionFailure("issuer_unexpected_permission_failure")
}