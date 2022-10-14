/**
 * Created by Michael Avoyan on 8/15/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

class OrganizationsMocks {
    companion object {
        const val OrganizationJson =
            "{\"id\":\"did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3\",\"name\":\"Universidad de Sant Cugat\",\"logo\":\"https:\\/\\/docs.velocitycareerlabs.io\\/Logos\\/Universidad de  Sant Cugat.png\",\"location\":{\"countryCode\":\"ES\",\"regionCode\":\"CAT\"},\"founded\":\"1984\",\"website\":\"https:\\/\\/example.com\",\"permittedVelocityServiceCategories\":[\"Issuer\",null],\"service\":[{\"id\":\"did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3#credential-agent-issuer-1\",\"type\":\"VelocityCredentialAgentIssuer_v1.0\",\"credentialTypes\":[\"Course\",\"EducationDegree\",\"Badge\"],\"serviceEndpoint\":\"https:\\/\\/devagent.velocitycareerlabs.io\\/api\\/holder\\/v0.6\\/org\\/did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3\\/issue\\/get-credential-manifest\"}]}"
        const val OrganizationJsonResult = "{\"result\":[$OrganizationJson]}"

        const val ServiceJsonStr =
            "{\"id\":\"did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3#credential-agent-issuer-1\",\"type\":\"VelocityCredentialAgentIssuer_v1.0\",\"credentialTypes\":[\"Course\",\"EducationDegree\",\"Badge\"],\"serviceEndpoint\":\"https:\\/\\/devagent.velocitycareerlabs.io\\/api\\/holder\\/v0.6\\/org\\/did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3\\/issue\\/get-credential-manifest\"}"
        const val ServiceEndpoint =
            "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3/issue/get-credential-manifest"
    }
}