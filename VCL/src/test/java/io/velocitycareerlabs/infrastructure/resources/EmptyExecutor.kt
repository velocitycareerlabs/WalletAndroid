/**
 * Created by Michael Avoyan on 5/2/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources

import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor

internal class EmptyExecutor: Executor {
    override fun runOnMain(block: () -> Unit) {
        block()
    }

    override fun runOnBackground(block: () -> Unit) {
        block()
    }

    override fun shutdown() {
    }
}