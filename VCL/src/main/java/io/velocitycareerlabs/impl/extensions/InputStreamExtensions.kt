package io.velocitycareerlabs.impl.extensions

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by Michael Avoyan on 3/18/21.
 */
internal fun InputStream.convertToString(encoding: String): String {
    val sb = StringBuilder()
    try {
        val reader = BufferedReader(InputStreamReader(this, encoding))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line + "\n")
        }
    } catch (ex: Exception) {
        throw ex
    } finally {
        try {
            this.close()
        } catch (ex: Exception) {
            throw ex
        }
    }
    return sb.toString()
}