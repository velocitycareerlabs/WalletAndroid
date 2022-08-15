package io.velocitycareerlabs.infrastructure

import android.os.Looper
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor

/**
 * Created by Michael Avoyan on 5/2/21.
 */
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