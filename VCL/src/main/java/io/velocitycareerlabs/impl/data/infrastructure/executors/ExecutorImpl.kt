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
import io.velocitycareerlabs.impl.utils.VCLLog
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class ExecutorImpl private constructor() : Executor {

    private val TAG = ExecutorImpl::class.simpleName

    private val mainThread: Handler = Handler(Looper.getMainLooper())

    private var numberOfCores = Runtime.getRuntime().availableProcessors()
    private var optimalThreadCount = minOf(numberOfCores * 2, 8) // Limit max to 8 threads

    private val executorService: ExecutorService =
        Executors.newFixedThreadPool(optimalThreadCount, Executors.defaultThreadFactory())

    override fun runOnMain(block: () -> Unit) {
        mainThread.post {
            block()
        }
    }

    override fun runOnBackground(block: () -> Unit) {
        executorService.submit {
            try {
                block()
            } catch (e: Exception) {
                VCLLog.e(TAG, "", e)
            }
        }
    }

    companion object {
        val instance: ExecutorImpl by lazy { ExecutorImpl() }
    }
}
