/**
 * Created by Michael Avoyan on 4/5/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.extensions

import android.util.Base64
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLPublicJwk
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
    } catch (e: Exception) {
        VCLLog.e("", "", e)
    }
    return map
}

internal fun String.getUrlSubPath(subPathPrefix: String) =
    URI(this)
    .path.split("/")
    .find { it.startsWith(subPathPrefix) }

//internal fun String.getUrlSubPath(subPathPrefix: String): String? {
//    VCLLog.d("getUrlSubPath", "VCL start search for $subPathPrefix in $this")
//    val splitted = URI(this).path.split("/")
//    VCLLog.d("getUrlSubPath", "VCL splitted URI $splitted")
//    val retVal = splitted.find { it.startsWith(subPathPrefix) }
//    VCLLog.d("getUrlSubPath", "VCL found $retVal")
//    return retVal
//}

/**
 * Decode the Base64-encoded data in input and return the data in a new byte array.
 * The padding '=' characters at the end are considered optional, but if any are present, there must be the correct number of them.
 * Params:
 * str – the input String to decode, which is converted to bytes using the default charset flags – controls certain features of the decoded output. Pass DEFAULT to decode standard Base64.
 * Throws:
 * IllegalArgumentException – if the input contains incorrect padding
 */
internal fun String.decodeBase64(flags: Int = Base64.NO_WRAP): String =
    Base64.decode(this, flags).toString(Charsets.UTF_8)

/**
 * Base64-encode the given data and return a newly allocated String with the result.
 * Params:
 * input – the data to encode flags – controls certain features of the encoded output. Passing DEFAULT results in output that adheres to RFC 2045.
 */
internal fun String.encodeToBase64(flags: Int = Base64.NO_WRAP): String =
    Base64.encodeToString(this.toByteArray(), flags)

internal fun String.encodeToBase64URL(): String =
    com.nimbusds.jose.util.Base64URL.encode(this).toString()

internal fun String.toDate(): Date? {
    val format = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    return try {
        format.parse(this)
    } catch (e: ParseException) {
//        VCLLog.e("", "", e)
        null
    }
}

internal fun String.toJsonObject(): JSONObject? {
    return try {
        JSONObject(this)
    } catch (e: Exception) {
//        VCLLog.e("", "", e)
        null
    }
}

internal fun String.toJsonArray(): JSONArray? {
    return try {
        JSONArray(this)
    } catch (e: Exception) {
//        VCLLog.e("", "", e)
        null
    }
}

internal fun String.toPublicJwk() =
    VCLPublicJwk(this.removePrefix(VCLDidJwk.DidJwkPrefix).decodeBase64())

internal fun randomString(length: Int): String =
    List(length) {
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")

