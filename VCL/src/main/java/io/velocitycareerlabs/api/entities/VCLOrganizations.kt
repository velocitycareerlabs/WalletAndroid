package io.velocitycareerlabs.api.entities

/**
 * Created by Michael Avoyan on 4/20/21.
 */
data class VCLOrganizations(val all: List<VCLOrganization>) {
    companion object CodingKeys {
        const val KeyResult = "result"
    }
}