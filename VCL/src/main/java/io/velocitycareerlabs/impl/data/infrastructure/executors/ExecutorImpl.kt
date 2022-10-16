/**
 * Created by Michael Avoyan on 4/29/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.infrastructure.executors

import android.os.Handler
import android.os.Looper
import io.velocitycareerlabs.impl.domain.infrastructure.executors.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class ExecutorImpl: Executor {

    private val mainThread: Handler = Handler(Looper.getMainLooper())
    private val backgroundThreadPool: ExecutorService = Executors.newFixedThreadPool(10)

    override fun runOn(looper: Looper?, runnable: Runnable) {
        looper?.let {
            Handler(it).post {
                runnable.run()
            }
        } ?: run{ runnable.run() }
    }

    override fun runOnMainThread(runnable: Runnable) {
        mainThread.post {
            runnable.run()
        }
    }

    override fun runOnBackgroundThread(runnable: Runnable) {
        backgroundThreadPool.submit {
            runnable.run()
        }
    }

    override fun waitForTermination() {
        backgroundThreadPool.shutdown()
        backgroundThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)
    }
}