/**
 * Created by Michael Avoyan on 18/07/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vcl.wallet

import io.velocitycareerlabs.api.entities.VCLOffers
import io.velocitycareerlabs.api.entities.VCLToken

object Utils {
    val TAG = Utils::class.simpleName

    fun getApprovedRejectedOfferIdsMock(offers: VCLOffers): Pair<List<String>, List<String>> {
        val approvedOfferIds = mutableListOf<String>()
        val rejectedOfferIds = mutableListOf<String>()
        var offer1: String? = null
        var offer2: String? = null
        if (offers.all.isNotEmpty()) {
            offer1 = offers.all[0].id
        }
        if (offers.all.size > 1) {
            offer2 = offers.all[1].id
        }
        offer1?.let { approvedOfferIds.add(it) }
        offer2?.let { rejectedOfferIds.add(it) }
        return Pair(approvedOfferIds, rejectedOfferIds)
    }

    fun isTokenValid(token: VCLToken?) = (token?.expiresIn ?: 0) > (System.currentTimeMillis() / 1000)
}
