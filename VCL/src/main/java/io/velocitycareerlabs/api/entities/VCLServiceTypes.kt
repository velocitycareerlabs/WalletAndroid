/**
 * Created by Michael Avoyan on 14/12/2022.
 */

package io.velocitycareerlabs.api.entities

class VCLServiceTypes(val all: List<VCLServiceType>) {

    fun contains(serviceType: VCLServiceType): Boolean =
        all.any { it == serviceType && serviceType != VCLServiceType.Undefined }
}