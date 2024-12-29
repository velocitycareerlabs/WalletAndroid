/**
 * Created by Michael Avoyan on 3/12/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.utils

import android.util.Log
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.GlobalConfig.LogTagPrefix

internal object VCLLog {
    /**
     * Send a [.VERBOSE] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun v(tag: String?, msg: String): Int {
        return if (GlobalConfig.IsLoggerOn) Log.v(LogTagPrefix+tag, msg) else -1
    }

    /**
     * Send a [.VERBOSE] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    fun v(tag: String?, msg: String?, tr: Throwable?): Int {
        return if (GlobalConfig.IsLoggerOn) Log.v(LogTagPrefix+tag, msg, tr) else -1
    }

    /**
     * Send a [.DEBUG] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun d(tag: String?, msg: String): Int {
        return if (GlobalConfig.IsLoggerOn) Log.d(LogTagPrefix+tag, msg) else -1
    }

    /**
     * Send a [.DEBUG] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    fun d(tag: String?, msg: String?, tr: Throwable?): Int {
        return if (GlobalConfig.IsLoggerOn) Log.d(LogTagPrefix+tag, msg, tr) else -1
    }

    /**
     * Send an [.INFO] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun i(tag: String?, msg: String): Int {
        return if (GlobalConfig.IsLoggerOn) Log.i(LogTagPrefix+tag, msg) else -1
    }

    /**
     * Send a [.INFO] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    fun i(tag: String?, msg: String?, tr: Throwable?): Int {
        return if (GlobalConfig.IsLoggerOn) Log.i(LogTagPrefix+tag, msg, tr) else -1
    }

    /**
     * Send a [.WARN] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun w(tag: String?, msg: String): Int {
        return if (GlobalConfig.IsLoggerOn) Log.w(LogTagPrefix+tag, msg) else -1
    }

    /**
     * Send a [.WARN] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    fun w(tag: String?, msg: String?, tr: Throwable?): Int {
        return if (GlobalConfig.IsLoggerOn) Log.w(LogTagPrefix+tag, msg, tr) else -1
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param tr An exception to log
     */
    fun w(tag: String?, tr: Throwable?): Int {
        return if (GlobalConfig.IsLoggerOn) Log.w(LogTagPrefix+tag, tr) else -1
    }

    /**
     * Send an [.ERROR] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun e(tag: String?, msg: String): Int {
        return if (GlobalConfig.IsLoggerOn) Log.e(LogTagPrefix+tag, msg) else -1
    }

    /**
     * Send a [.ERROR] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    fun e(tag: String?, msg: String?, tr: Throwable?): Int {
        // always log errors
        return Log.e(LogTagPrefix+tag, msg, tr)
    }
}