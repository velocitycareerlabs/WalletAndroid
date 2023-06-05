/**
 * Created by Michael Avoyan on 18/07/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vcl.wallet

import io.velocitycareerlabs.api.entities.VCLOffers

object Utils {
    val TAG = Utils::class.simpleName

    fun getApprovedRejectedOfferIdsMock(offers: VCLOffers): Pair<List<String>, List<String>> {

        val approvedOfferIds: List<String> = listOfNotNull(
            offers.all.optJSONObject(0)?.optString("id")
        )
        val rejectedOfferIds: List<String> = listOfNotNull(
            offers.all.optJSONObject(1)?.optString("id")
        )
        return Pair(approvedOfferIds, rejectedOfferIds)
    }
}