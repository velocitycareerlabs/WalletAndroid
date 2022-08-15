package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLOrganizations
import io.velocitycareerlabs.api.entities.VCLOrganizationsSearchDescriptor
import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 4/11/21.
 */
internal interface OrganizationsRepository {
    fun searchForOrganizations(organizationsSearchDescriptor: VCLOrganizationsSearchDescriptor,
                               completionBlock: (VCLResult<VCLOrganizations>) -> Unit)
}