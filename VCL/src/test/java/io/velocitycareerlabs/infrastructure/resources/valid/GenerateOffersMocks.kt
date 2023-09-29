/**
 * Created by Michael Avoyan on 11/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

class GenerateOffersMocks {
    companion object {
        const val Offers = "[{\"offer1\":\"some offer 1\"},{\"offer2\":\"some offer 2\"}]"
        const val Challenge = "CSASLD10103aa_RW"
        const val GeneratedOffers = "{\"offers\":$Offers,\"challenge\":\"$Challenge\"}"
        const val GeneratedOffersEmptyJsonObj = "{}"
        const val GeneratedOffersEmptyJsonArr = "[]"
    }
}