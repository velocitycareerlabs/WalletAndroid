/**
 * Created by Michael Avoyan on 18/06/2024.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.impl.extensions.toJsonArray
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.resources.valid.OffersMocks
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

class VCLOffersTest {
    private val subject1 = VCLOffers.fromPayload(
        OffersMocks.OffersJsonArrayStr,
        123,
        VCLToken("some token")
    )
    private val subject2 = VCLOffers.fromPayload(
        OffersMocks.OffersJsonObjectStr,
        123,
        VCLToken("some token")
    )
    private val subject3 = VCLOffers.fromPayload(
        OffersMocks.offersJsonEmptyArrayStr,
        123,
        VCLToken("some token")
    )
    private val subject4 = VCLOffers.fromPayload(
        OffersMocks.offersJsonEmptyObjectStr,
        123,
        VCLToken("some token")
    )

    @Test
    fun testOffersFromJsonArray() {
        JSONAssert.assertEquals(
            subject1.payload.optJSONArray(VCLOffers.Companion.CodingKeys.KeyOffers),
            OffersMocks.OffersJsonArrayStr.toJsonArray(),
            JSONCompareMode.LENIENT
        )
        assertNull(subject1.challenge)
        testExpectations(subject1)
        JSONAssert.assertEquals(
            subject1.all.map { it.payload }.toJsonArray(),
            OffersMocks.OffersJsonArrayStr.toJsonArray()!!,
            JSONCompareMode.LENIENT
        )
    }

    @Test
    fun testOffersFromJsonObject() {
        JSONAssert.assertEquals(
            subject2.payload,
            OffersMocks.OffersJsonObjectStr.toJsonObject(),
            JSONCompareMode.LENIENT
        )
        assertEquals(subject2.challenge, OffersMocks.challenge)
        testExpectations(subject2)
        JSONAssert.assertEquals(
            subject2.all.map { it.payload }.toJsonArray(),
            OffersMocks.OffersJsonArrayStr.toJsonArray()!!,
            JSONCompareMode.LENIENT
        )
    }

    @Test
    fun testOffersFromEmptyJsonArray() {
        JSONAssert.assertEquals(
            subject3.payload.optJSONArray(VCLOffers.Companion.CodingKeys.KeyOffers),
            OffersMocks.offersJsonEmptyArrayStr.toJsonArray(),
            JSONCompareMode.LENIENT
        )
        assertNull(subject3.challenge)
        JSONAssert.assertEquals(
            subject3.all.map { it.payload }.toJsonArray(),
            OffersMocks.offersJsonEmptyArrayStr.toJsonArray()!!,
            JSONCompareMode.LENIENT
        )
    }

    @Test
    fun testOffersFromEmptyJsonObject() {
        JSONAssert.assertEquals(
            subject4.payload,
            OffersMocks.offersJsonEmptyObjectStr.toJsonObject(),
            JSONCompareMode.LENIENT
        )
        assertEquals(subject4.challenge, OffersMocks.challenge)
        JSONAssert.assertEquals(
            subject4.all.map { it.payload }.toJsonArray(),
            OffersMocks.offersJsonEmptyArrayStr.toJsonArray()!!,
            JSONCompareMode.LENIENT
        )
    }

    private fun testExpectations(subject: VCLOffers) {
        assertEquals(subject.responseCode, 123)
        assertEquals(subject.sessionToken.value, "some token")
        assertEquals(subject.all.size, 11)
    }
}