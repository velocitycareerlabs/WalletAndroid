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

    override fun runOnMain(block: () -> Unit) {
        mainThread.post {
            block()
        }
    }

    override fun runOnBackground(block: () -> Unit) {
        backgroundThreadPool.submit {
            block()
        }
    }

    override fun shutdown() {
        backgroundThreadPool.shutdown()
        backgroundThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)
    }
}