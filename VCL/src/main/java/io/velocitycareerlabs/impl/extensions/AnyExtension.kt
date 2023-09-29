/**
 * Created by Michael Avoyan on 07/08/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.extensions

import org.json.JSONObject
//import kotlin.reflect.full.declaredMemberProperties
//import kotlin.reflect.jvm.isAccessible
//import kotlin.reflect.jvm.javaField

/**
 * provides only constructor properties
 */
//internal fun Any.toPropsJsonObject(): JSONObject {
//    val retVal = JSONObject()
//    val properties = this::class.declaredMemberProperties
//    for (property in properties) {
//        property.isAccessible = true
//        val value = property.javaField?.get(this)
//        retVal.put(property.name, value)
//    }
//    return retVal
//}