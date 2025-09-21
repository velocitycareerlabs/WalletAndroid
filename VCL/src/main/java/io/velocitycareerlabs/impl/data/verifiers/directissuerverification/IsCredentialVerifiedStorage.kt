/**
 * Created by Michael Avoyan on 18/09/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.verifiers.directissuerverification

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class IsCredentialVerifiedStorage {
    private var isVerified: Boolean = false
    private val lock = ReentrantReadWriteLock()

    fun update(value: Boolean) {
        lock.write {
            isVerified = value
        }
    }

    fun get(): Boolean {
        return lock.read {
            isVerified
        }
    }
}