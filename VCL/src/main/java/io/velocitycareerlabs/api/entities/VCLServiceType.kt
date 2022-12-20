/**
 * Created by Michael Avoyan on 06/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

enum class VCLServiceType(val value: String) {
    Issuer("Issuer"),
    Inspector("Inspector"),
    TrustRoot("TrustRoot"),
    NodeOperator("NodeOperator"),
    NotaryIssuer("NotaryIssuer"),
    IdentityIssuer("IdentityIssuer"),
    HolderAppProvider("HolderAppProvider"),
    CredentialAgentOperator("CredentialAgentOperator"),
    Undefined("Undefined");

    companion object {
        fun fromString(value: String): VCLServiceType {
            if(value.contains(VCLServiceType.NotaryIssuer.value)) {
                return VCLServiceType.NotaryIssuer
            }
            if(value.contains(VCLServiceType.IdentityIssuer.value)) {
                return VCLServiceType.IdentityIssuer
            }
            if(value.contains(VCLServiceType.Issuer.value)) {
                return VCLServiceType.Issuer
            }
            if(value.contains(VCLServiceType.Inspector.value)) {
                return VCLServiceType.Inspector
            }
            if(value.contains(VCLServiceType.TrustRoot.value)) {
                return VCLServiceType.TrustRoot
            }
            if(value.contains(VCLServiceType.NodeOperator.value)) {
                return VCLServiceType.NodeOperator
            }
            if(value.contains(VCLServiceType.HolderAppProvider.value)) {
                return VCLServiceType.HolderAppProvider
            }
            if(value.contains(VCLServiceType.CredentialAgentOperator.value)) {
                return VCLServiceType.CredentialAgentOperator
            }
            else {
                return VCLServiceType.Undefined
            }
        }
    }
}