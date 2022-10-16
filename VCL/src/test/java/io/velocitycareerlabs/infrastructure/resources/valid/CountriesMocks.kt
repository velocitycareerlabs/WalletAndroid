/**
 * Created by Michael Avoyan on 9/12/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

class CountriesMocks {
    companion object {

        const val AfghanistanRegion1Name = "Balkh Province"
        const val AfghanistanRegion1Code = "BAL"
        const val AfghanistanRegion2Name = "Bamyan Province"
        const val AfghanistanRegion2Code = "BAM"
        const val AfghanistanRegion3Name = "Badghis Province"
        const val AfghanistanRegion3Code = "BDG"

        const val AfghanistanRegion1 = "{\"name\":\"Balkh Province\",\"code\":\"BAL\"}"
        const val AfghanistanRegion2 = "{\"name\":\"Bamyan Province\",\"code\":\"BAM\"}"
        const val AfghanistanRegion3 = "{\"name\":\"Badghis Province\",\"code\":\"BDG\"}"
        const val AfghanistanRegions =
            "[$AfghanistanRegion1,$AfghanistanRegion2,$AfghanistanRegion3,{\"name\":\"Badakhshan Province\",\"code\":\"BDS\"},{\"name\":\"Baghlan Province\",\"code\":\"BGL\"},{\"name\":\"Daykundi Province\",\"code\":\"DAY\"},{\"name\":\"Farah Province\",\"code\":\"FRA\"},{\"name\":\"Faryab Province\",\"code\":\"FYB\"},{\"name\":\"Ghazni Province\",\"code\":\"GHA\"},{\"name\":\"Ghōr Province\",\"code\":\"GHO\"},{\"name\":\"Helmand Province\",\"code\":\"HEL\"},{\"name\":\"Herat Province\",\"code\":\"HER\"},{\"name\":\"Jowzjan Province\",\"code\":\"JOW\"},{\"name\":\"Kabul Province\",\"code\":\"KAB\"},{\"name\":\"Kandahar Province\",\"code\":\"KAN\"},{\"name\":\"Kapisa Province\",\"code\":\"KAP\"},{\"name\":\"Kunduz Province\",\"code\":\"KDZ\"},{\"name\":\"Khost Province\",\"code\":\"KHO\"},{\"name\":\"Kunar Province\",\"code\":\"KNR\"},{\"name\":\"Laghman Province\",\"code\":\"LAG\"},{\"name\":\"Logar Province\",\"code\":\"LOG\"},{\"name\":\"Nangarhar Province\",\"code\":\"NAN\"},{\"name\":\"Nimruz Province\",\"code\":\"NIM\"},{\"name\":\"Nuristan Province\",\"code\":\"NUR\"},{\"name\":\"Oruzgan\",\"code\":\"ORU\"},{\"name\":\"Panjshir Province\",\"code\":\"PAN\"},{\"name\":\"Parwan Province\",\"code\":\"PAR\"},{\"name\":\"Paktia Province\",\"code\":\"PIA\"},{\"name\":\"Paktika Province\",\"code\":\"PKA\"},{\"name\":\"Samangan Province\",\"code\":\"SAM\"},{\"name\":\"Sar-e Pol Province\",\"code\":\"SAR\"},{\"name\":\"Takhar Province\",\"code\":\"TAK\"},{\"name\":\"Urozgan Province\",\"code\":\"URU\"},{\"name\":\"Maidan Wardak Province\",\"code\":\"WAR\"},{\"name\":\"Zabul Province\",\"code\":\"ZAB\"}]"
        const val AfghanistanName = "Afghanistan"
        const val AfghanistanCode = "AF"
        const val AfghanistanCountry =
            "{\"name\":$AfghanistanName,\"code\":$AfghanistanCode,\"regions\":$AfghanistanRegions}"

        const val CountriesJson =
            "[$AfghanistanCountry,{\"name\":\"South Sudan\",\"code\":\"SS\",\"regions\":[{\"name\":\"Ruweng\",\"code\":\"\"},{\"name\":\"Maiwut\",\"code\":\"\"},{\"name\":\"Akobo\",\"code\":\"\"},{\"name\":\"Aweil\",\"code\":\"\"},{\"name\":\"Eastern Lakes\",\"code\":\"\"},{\"name\":\"Gogrial\",\"code\":\"\"},{\"name\":\"Lol\",\"code\":\"\"},{\"name\":\"Amadi State\",\"code\":\"\"},{\"name\":\"Yei River\",\"code\":\"\"},{\"name\":\"Fashoda\",\"code\":\"\"},{\"name\":\"Gok\",\"code\":\"\"},{\"name\":\"Tonj\",\"code\":\"\"},{\"name\":\"Twic\",\"code\":\"\"},{\"name\":\"Wau\",\"code\":\"\"},{\"name\":\"Gbudwe\",\"code\":\"\"},{\"name\":\"Imatong\",\"code\":\"\"},{\"name\":\"Jubek\",\"code\":\"\"},{\"name\":\"Maridi\",\"code\":\"\"},{\"name\":\"Terekeka\",\"code\":\"\"},{\"name\":\"Boma\",\"code\":\"\"},{\"name\":\"Bieh\",\"code\":\"\"},{\"name\":\"Central Upper Nile\",\"code\":\"\"},{\"name\":\"Latjoor\",\"code\":\"\"},{\"name\":\"Northern Liech\",\"code\":\"\"},{\"name\":\"Southern Liech\",\"code\":\"\"},{\"name\":\"Fangak\",\"code\":\"\"},{\"name\":\"Western Lakes\",\"code\":\"\"},{\"name\":\"Aweil East\",\"code\":\"\"},{\"name\":\"Northern Upper Nile\",\"code\":\"\"},{\"name\":\"Tambura\",\"code\":\"\"},{\"name\":\"Kapoeta\",\"code\":\"\"},{\"name\":\"Jonglei\",\"code\":\"JG\"}]},{\"name\":\"Kosovo\",\"code\":\"XK\",\"regions\":[]}]"
    }
}