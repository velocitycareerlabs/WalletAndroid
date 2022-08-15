package io.velocitycareerlabs.impl.extensions

import org.json.JSONArray

/**
 * Created by Michael Avoyan on 18/07/2021.
 */

internal fun <T> List<T>.toJsonArray(): JSONArray {
    val retVal = JSONArray()
    forEach {
        retVal.put(it)
    }
    return retVal
}