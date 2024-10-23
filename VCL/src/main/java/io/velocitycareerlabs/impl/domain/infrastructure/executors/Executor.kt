/**
 * Created by Michael Avoyan on 5/2/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.infrastructure.executors

internal interface Executor {
    fun runOnMain(block: () -> Unit)
    fun runOnBackground(block: () -> Unit)
}