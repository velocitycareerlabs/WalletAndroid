/**
 * Created by Michael Avoyan on 15/02/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.api.entities

enum class VCLServiceType(val value: String) {
    Inspector("Inspector"),
    Issuer("Issuer"),
    NotaryIssuer("NotaryIssuer"),
    CareerIssuer("CareerIssuer"),
//    Identity issuer types:
    IdentityIssuer("IdentityIssuer"),
    IdDocumentIssuer("IdDocumentIssuer"),
    NotaryIdDocumentIssuer("NotaryIdDocumentIssuer"),
    ContactIssuer("ContactIssuer"),
    NotaryContactIssuer("NotaryContactIssuer"),
    WorkPermitIssuer("WorkPermitIssuer"),
    NotaryWorkPermitIssuer("NotaryWorkPermitIssuer"),

    Undefined("Undefined");

    companion object {
        fun fromString(value: String): VCLServiceType {
            if(value.contains(Inspector.value)) {
                return Inspector
            }
            if(value.contains(NotaryIssuer.value)) {
                return NotaryIssuer
            }
            if(value.contains(IdentityIssuer.value)) {
                return IdentityIssuer
            }
            if(value.contains(NotaryIdDocumentIssuer.value)) {
                return NotaryIdDocumentIssuer
            }
            if(value.contains(IdDocumentIssuer.value)) {
                return IdDocumentIssuer
            }
            if(value.contains(NotaryContactIssuer.value)) {
                return NotaryContactIssuer
            }
            if(value.contains(ContactIssuer.value)) {
                return ContactIssuer
            }
            if(value.contains(CareerIssuer.value)) {
                return CareerIssuer
            }
            if(value.contains(NotaryWorkPermitIssuer.value)) {
                return NotaryWorkPermitIssuer
            }
            if(value.contains(WorkPermitIssuer.value)) {
                return WorkPermitIssuer
            }
            if(value.contains(Issuer.value)) {
                return Issuer
            }
            return Undefined
        }
    }
}