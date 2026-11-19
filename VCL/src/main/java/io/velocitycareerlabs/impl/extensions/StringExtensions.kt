/**
 * Created by Michael Avoyan on 4/5/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.extensions

import android.util.Base64
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.utils.VCLLog
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern

//internal fun String.isUrlEquivalentTo(url: String): Boolean {
//    var retVal = true
//    val thisQueryParams = this.decode().getUrlQueryParams()!!
//    val urlQueryParams = url.decode().getUrlQueryParams()!!
//
//    thisQueryParams.forEach {
//        retVal = retVal && urlQueryParams[it.key] == it.value
//    }
//    retVal = retVal && URI(this.encode()).host == URI(url.encode()).host
//    retVal = retVal && URI(this.encode()).path == URI(url.encode()).path
//    return retVal
//}
internal fun String.isUrlEquivalentTo(url: String): Boolean {
    return this.toCharArray().sorted().joinToString("") ==
            url.toCharArray().sorted().joinToString("")
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

// The query's items as they're written
internal fun String.getUrlQuerySegments(): List<String> =
    substringAfter('?', "").substringBefore('#').split('&').filter { it.isNotEmpty() }

// The query's items in order, each name and value decoded once, as a browser reads a query: a value keeps any
// &, = or ? it encodes, and a repeated name keeps every value. A malformed escape throws, as decode() does.
internal fun String.getUrlQueryItems(): List<Pair<String, String>> =
    getUrlQuerySegments().map { it.substringBefore('=').decode() to it.substringAfter('=', "").decode() }

internal fun String.decodeOrSelf(): String =
    try {
        decode()
    } catch (e: IllegalArgumentException) {
        this
    }

// Escapes the characters a URL query can't hold as they are, such as a space or a quote, and leaves escapes and
// delimiters as they are
internal fun String.encodeIllegalQueryCharacters(): String =
    IllegalQueryCharacters.replace(this) { URLEncoder.encode(it.value, "UTF-8") }

private val IllegalQueryCharacters = Regex("[^A-Za-z0-9\\-._~!$&'()*+,;=:@/?%]+")

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

internal fun String.toJwtList(): List<VCLJwt>? {
    this.toJsonArray()?.toList()?.let { encodedCredentialList ->
        val jwtCredentials = mutableListOf<VCLJwt>()
        val completableFutures = encodedCredentialList.map { jwtEncodedCredential ->
            CompletableFuture.supplyAsync {
                jwtCredentials.add(VCLJwt(jwtEncodedCredential as? String ?: ""))
            }
        }
        val allFutures = CompletableFuture.allOf(*completableFutures.toTypedArray())
        allFutures.join()

        return jwtCredentials
    }
    return null
}

internal fun String.toPublicJwk() =
    VCLPublicJwk(
        this.removePrefix(VCLDidJwk.DidJwkPrefix)
            .removeSuffix(VCLDidJwk.DidJwkSuffix)
            .decodeBase64()
    )

internal fun randomString(length: Int): String =
    List(length) {
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")

