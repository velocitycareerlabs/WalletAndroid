/**
 * Created by Michael Avoyan on 4/5/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.extensions

import android.util.Base64
import io.velocitycareerlabs.impl.utils.VCLLog
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

internal fun String.isUrlEquivalentTo(url: String): Boolean {
    var retVal = true
    val thisQueryParams = this.getUrlQueryParams()!!
    val urlQueryParams = url.getUrlQueryParams()!!

    thisQueryParams.forEach {
        retVal = retVal && urlQueryParams[it.key] == it.value
    }
    retVal = retVal && URI(this).host == URI(url).host
    retVal = retVal && URI(this).path == URI(url).path
    return retVal
}

internal fun String.appendQueryParams(queryParams: String) =
    this + ( URI(this).query?.let { "&" } ?: "?" ) + queryParams

internal fun String.decode(): String = URLDecoder.decode(this, "UTF-8")

internal fun String.encode(): String = URLEncoder.encode(this, "UTF-8")

internal fun String.getUrlQueryParams(): Map<String, String>? {
    var map: MutableMap<String, String>? = null
    try {
        val params = this.split("[${Pattern.quote("?")}&]".toRegex()).toTypedArray()
        map = HashMap()
        for (param in params) {
            val pair = param.split("=".toRegex()).toTypedArray()
            if (pair.size == 2)
                map[pair[0]] = pair[1]
        }
    } catch (ex: Exception)
    {}
    return map
}

//internal fun String.getUrlSubPath(subPathPrefix: String) =
//    URI(this)
//    .path.split("/")
//    .find { it.startsWith(subPathPrefix) }

internal fun String.getUrlSubPath(subPathPrefix: String): String? {
    VCLLog.d("getUrlSubPath", "VCL start search for $subPathPrefix in $this")
    val splitted = URI(this).path.split("/")
    VCLLog.d("getUrlSubPath", "VCL splitted URI $splitted")
    val retVal = splitted.find { it.startsWith(subPathPrefix) }
    VCLLog.d("getUrlSubPath", "VCL found $retVal")
    return retVal
}

internal fun String.decodeBase64() = Base64.decode(this, Base64.DEFAULT).toString(Charsets.UTF_8)
internal fun String.encodeToBase64() = Base64.encodeToString(this.toByteArray(), Base64.DEFAULT)

internal fun String.toDate(): Date? {
    val format = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    try {
        return format.parse(this)
    } catch (e: ParseException)
    { }
    return null
}

internal fun String.toJsonObject(): JSONObject? {
    return try {
        JSONObject(this)
    } catch (e: Exception) {
        null
    }
}

internal fun String.toJsonArray(): JSONArray? {
    return try {
        JSONArray(this)
    } catch (e: Exception) {
        null
    }
}

internal fun randomString(length: Int): String =
    List(length) {
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")

