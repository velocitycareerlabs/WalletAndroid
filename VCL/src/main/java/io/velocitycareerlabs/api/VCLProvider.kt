package io.velocitycareerlabs.api

import io.velocitycareerlabs.impl.VCLImpl

/**
 * Created by Michael Avoyan on 3/20/21.
 */
class VCLProvider {
    companion object {
        fun vclInstance(): VCL {
            return VCLImpl()
        }
    }
}