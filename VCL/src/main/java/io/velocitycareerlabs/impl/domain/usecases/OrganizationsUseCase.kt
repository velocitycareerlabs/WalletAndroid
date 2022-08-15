package io.velocitycareerlabs.impl.domain.usecases

import io.velocitycareerlabs.api.entities.VCLOrganizations
import io.velocitycareerlabs.api.entities.VCLOrganizationsSearchDescriptor
import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 4/20/21.
 */
internal interface OrganizationsUseCase {
    fun searchForOrganizations(organizationsSearchDescriptor: VCLOrganizationsSearchDescriptor,
                               completionBlock: (VCLResult<VCLOrganizations>) -> Unit)
}