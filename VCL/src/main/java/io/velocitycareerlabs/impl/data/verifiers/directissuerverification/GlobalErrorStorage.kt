/**
 * Created by Michael Avoyan on 18/09/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.verifiers.directissuerverification

import io.velocitycareerlabs.api.entities.error.VCLError
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class GlobalErrorStorage {
    private var error: VCLError? = null
    private val lock = ReentrantReadWriteLock()

    fun update(error: VCLError) {
        lock.write {
            this.error = error
        }
    }

    fun get(): VCLError? {
        return lock.read {
            error
        }
    }

    fun clear() {
        lock.write {
            error = null
        }
    }

    fun hasError(): Boolean {
        return lock.read {
            error != null
        }
    }
}