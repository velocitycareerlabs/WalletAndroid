/**
 * Created by Michael Avoyan on 3/20/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.utils

import io.velocitycareerlabs.api.entities.error.VCLError
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

internal class InitializationWatcher(private val initAmount: Int) {
    private val initCount = AtomicInteger(0)

    private val errors = Collections.synchronizedList(mutableListOf<VCLError>())

    fun onInitializedModel(error: VCLError?, enforceFailure: Boolean = false): Boolean {
        initCount.incrementAndGet()
        error?.let { errors.add(it) }
        return isInitializationComplete(enforceFailure)
    }

    fun firstError(): VCLError? {
        return errors.firstOrNull()
    }

    private fun isInitializationComplete(enforceFailure: Boolean): Boolean {
        return initCount.get() == initAmount || enforceFailure
    }
}