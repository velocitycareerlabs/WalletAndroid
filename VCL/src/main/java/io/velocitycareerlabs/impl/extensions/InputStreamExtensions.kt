/**
 * Created by Michael Avoyan on 3/18/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.extensions

import java.io.InputStream
import java.nio.charset.Charset

internal fun InputStream.convertToString(charset: Charset = Charsets.UTF_8): String {
    return this.bufferedReader(charset).use { it.readText() }
}