/**
 * Created by Michael Avoyan on 04/06/2023.
 */

package io.velocitycareerlabs.impl.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
internal fun Long.toDateStr() = SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(this)
