package io.velocitycareerlabs.impl.domain.infrastructure.executors

import android.os.Looper

/**
 * Created by Michael Avoyan on 5/2/21.
 */
internal interface Executor {
    fun runOn(looper: Looper?, runnable: Runnable)
    fun runOnMainThread(runnable: Runnable)
    fun runOnBackgroundThread(runnable: Runnable)
    fun waitForTermination()
}