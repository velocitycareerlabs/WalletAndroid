/**
 * Created by Michael Avoyan on 04/06/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
internal fun Long.toDateStr() = SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(this)
