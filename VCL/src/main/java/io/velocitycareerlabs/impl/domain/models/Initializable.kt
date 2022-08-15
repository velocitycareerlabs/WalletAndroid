package io.velocitycareerlabs.impl.domain.models

import io.velocitycareerlabs.api.entities.VCLResult

/**
 * Created by Michael Avoyan on 18/07/2021.
 */
internal interface Initializable<T> {
    fun initialize(completionBlock: (VCLResult<T>) -> Unit)
}