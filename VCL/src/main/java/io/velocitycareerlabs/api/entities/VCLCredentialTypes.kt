package io.velocitycareerlabs.api.entities

/**
 * Created by Michael Avoyan on 3/16/21.
 */
data class VCLCredentialTypes(val all: List<VCLCredentialType>?) {
    val recommendedTypes:List<VCLCredentialType>? get() = all?.filter { it.recommended }
}