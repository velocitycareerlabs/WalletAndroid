package io.velocitycareerlabs.impl.domain.repositories

import io.velocitycareerlabs.api.entities.VCLPublicKey
import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 4/20/21.
 */
internal interface ResolveKidRepository {
    fun getPublicKey(keyID: String, completionBlock: (VCLResult<VCLPublicKey>) -> Unit)
}