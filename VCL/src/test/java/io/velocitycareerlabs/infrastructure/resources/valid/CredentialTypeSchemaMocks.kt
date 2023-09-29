/**
 * Created by Michael Avoyan on 4/29/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLCredentialType
import io.velocitycareerlabs.api.entities.VCLCredentialTypes
import org.json.JSONArray
import org.json.JSONObject

class CredentialTypeSchemaMocks {
    companion object {
        const val CredentialTypeSchemaJson = "{\n" +
                "  \"title\": \"vaccination-certificate\",\n" +
                "  \"\$id\": \"https://velocitynetwork.foundation/schemas/vaccination-certificate\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"disease\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"description\": \"Disease or agent that the vaccination provides protection against\"\n" +
                "    },\n" +
                "    \"vaccineDescription\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"description\": \"Generic description of the vaccine/prophylaxis\"\n" +
                "    },\n" +
                "    \"vaccineType\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"description\": \"Generic description of the vaccine/prophylaxis or its component(s) [J07BX03 covid-19 vaccines]\"\n" +
                "    },\n" +
                "    \"certifiedBy\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"description\": \"Entity that has issued the certificate (allowing to check the certificate)\"\n" +
                "    },\n" +
                "    \"certificateNumber\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"description\": \"Unique identifier of the certificate (UVCI), to be printed (human readable) into the certificate; the unique identifier can be included in the IIS\"\n" +
                "    },\n" +
                "    \"certificateValidFrom\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"format\": \"date-time\",\n" +
                "      \"description\": \"Certificate valid from (required if known)\"\n" +
                "    },\n" +
                "    \"certificateValidTo\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"format\": \"date-time\",\n" +
                "      \"description\": \"Certificate valid until (validity can differ from the expected immunisation period)\"\n" +
                "    },\n" +
                "    \"formVersion\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"pattern\": \"^1.0.0\$\",\n" +
                "      \"description\": \"Version of this minimum dataset definition\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"required\": [\n" +
                "    \"disease\",\n" +
                "    \"vaccineDescription\",\n" +
                "    \"vaccineType\",\n" +
                "    \"certifiedBy\",\n" +
                "    \"certificateNumber\",\n" +
                "    \"certificateValidFrom\",\n" +
                "    \"formVersion\"\n" +
                "  ]}\n"

        val CredentialType = VCLCredentialType(
            payload = JSONObject(CredentialTypesMocks.CredentialType2),
            id = "5fe4a315d8b45dd2e80bd73a",
            schema = "",
            createdAt = "2022-03-17T09:24:38.448Z",
            schemaName = "current-employment-position",
            credentialType = "CurrentEmploymentPosition",
            recommended = true,
            jsonldContext = JSONArray(),
            issuerCategory = "RegularIssuer"
            )

        val CredentialTypes = VCLCredentialTypes(
            all = listOf(CredentialType)
        )
    }
}