/**
 * Created by Michael Avoyan on 07/11/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.utils

import io.velocitycareerlabs.impl.data.verifiers.VerificationUtils
import org.junit.Test

class VerificationUtilsTest {
    @Test
    fun testGetIdentifier() {
        val jsonObject = mapOf(
            "a" to "ValueA",
            "b" to mapOf("identifier" to "ValueB"),
            "c" to mapOf("id1" to "ValueC"), // what about this case?
            "d" to mapOf("x" to "ValueX"),
            "e" to mapOf("f" to mapOf("id" to "ValueD"))
        )

        val primaryOrgProp = "id"
        val result = VerificationUtils.getIdentifier(primaryOrgProp, jsonObject)

//        assert("ValueC" == result) // what about this case?
        assert("ValueD" == result) // because of stack
    }

    @Test
    fun testGetIdentifier_NoMatch() {
        val jsonObject = mapOf(
            "a" to "ValueA",
            "b" to mapOf("identifier" to "ValueB"),
            "c" to mapOf("x" to "ValueX")
        )

        val primaryOrgProp = "id"
        val result = VerificationUtils.getIdentifier(primaryOrgProp, jsonObject)

        assert(result == null)
    }

    @Test
    fun testGetPrimaryIdentifier() {
        val value = "ValueX"
        val result = VerificationUtils.getPrimaryIdentifier(value)

        assert("ValueX" == result)
    }

    @Test
    fun testGetPrimaryIdentifier_MapWithId() {
        val value = mapOf("id" to "ValueY")
        val result = VerificationUtils.getPrimaryIdentifier(value)

        assert("ValueY" == result)
    }

    @Test
    fun testGetPrimaryIdentifier_MapWithIdentifier() {
        val value = mapOf("identifier" to "ValueZ")
        val result = VerificationUtils.getPrimaryIdentifier(value)

        assert("ValueZ" == result)
    }

    @Test
    fun testGetPrimaryIdentifier_Null() {
        val value: Map<*, *>? = null
        val result = VerificationUtils.getPrimaryIdentifier(value)

        assert(result == null)
    }
}