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

internal class CompleteContextsStorage {
    private val storage = mutableListOf<Map<*, *>>()
    private val lock = ReentrantReadWriteLock()

    fun append(completeContext: Map<*, *>) {
        lock.write {
            storage.add(completeContext)
        }
    }

    fun isEmpty(): Boolean {
        return lock.read {
            storage.isEmpty()
        }
    }

    fun get(): List<Map<*, *>> {
        return lock.read {
            storage.toList() // defensive copy to avoid external mutation
        }
    }
}