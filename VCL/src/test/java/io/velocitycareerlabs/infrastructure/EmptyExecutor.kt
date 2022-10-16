/**
 * Created by Michael Avoyan on 5/2/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure

import android.os.Looper
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor

internal class EmptyExecutor: Executor {
    override fun runOnMainThread(runnable: Runnable) {
        runnable.run()
    }

    override fun runOn(looper: Looper?, runnable: Runnable) {
        runnable.run()
    }

    override fun runOnBackgroundThread(runnable: Runnable) {
        runnable.run()
    }

    override fun waitForTermination() {
    }
}