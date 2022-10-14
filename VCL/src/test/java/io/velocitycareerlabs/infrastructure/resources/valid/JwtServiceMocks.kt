/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import io.velocitycareerlabs.api.entities.VCLJWT
import io.velocitycareerlabs.api.entities.VCLPublicKey
import org.json.JSONObject

class JwtServiceMocks {
    companion object {
        const val AdamSmithEmailJwt =
            "eyJ0eXAiOiJKV1QiLCJqd2siOnsiY3J2Ijoic2VjcDI1NmsxIiwieCI6IkFxcl9HRHVoeE5fSHFHZ0E4YmVjeW9NNkZBUzZMMm9rVWFoc21GaTJRSUkiLCJ5IjoiaXI0a2Z1bVRYTHlCdjlPNUlJNVJzVmhVdnNvWUZRVURnWk0yQXJCSkxxYyIsImt0eSI6IkVDIiwia2lkIjoiLTNUNjZ1WHJ4Y1JWZ3E4VnhwcW43NXFTbDBrN0dfcDNYMlRIdW9jSFlycyIsImFsZyI6IkVTMjU2SyJ9LCJhbGciOiJFUzI1NksifQ.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIkVtYWlsIiwiVmVyaWZpYWJsZUNyZWRlbnRpYWwiXSwiY3JlZGVudGlhbFN0YXR1cyI6eyJpZCI6Imh0dHBzOi8vY3JlZGVudGlhbHN0YXR1cy52ZWxvY2l0eWNhcmVlcmxhYnMuaW8iLCJ0eXBlIjoiVmVsb2NpdHlSZXZvY2F0aW9uUmVnaXN0cnkifSwiY3JlZGVudGlhbFN1YmplY3QiOnsiZW1haWwiOiJhZGFtLnNtaXRoQGV4YW1wbGUuY29tIn19LCJpc3MiOiJkaWQ6ZXRoci4weDBiMTU0ZGE0OGQwZjIxM2MyNmM0YjFkMDQwZGM1ZmYxZGJmOTlmZmEiLCJqdGkiOiJkaWQ6ZXRoci4weDQ5YjhlMzQ2NzYyZGYwYzc4NzkyZDAzZWM2Zjg0NGVlYjEyMDM2YzgiLCJpYXQiOjE2MDkwNzgzMTQsIm5iZiI6MTYwOTA3ODMxNH0.Pk3_mf7OTHATkOis4MBamUlerGepcB0ke-DFvhe7potRRCwiuo4v96vkOGPW_Rib0Rk6xV1rPXl8Z-UY2_TbvQ"

        const val AdamSmithIdDocumentJwt =
            "eyJ0eXAiOiJKV1QiLCJqd2siOnsiY3J2Ijoic2VjcDI1NmsxIiwieCI6IkstTzhWQVhGM0tOMjFITnZUV3NsNDkwMU14RG5iVTBHdXRXSXd3bmRYQm8iLCJ5IjoiU2xKdDNuYnBsY1ZtRVJYUkpjeXQ0aG1QTjZnTnFkUVRKWE5ESnNmYUJfcyIsImt0eSI6IkVDIiwia2lkIjoiaDA3NDZPazdNUXhsdGVqa0VDeXV2TTFxalhnb29IbG5udC1MWXA1YmlvNCIsImFsZyI6IkVTMjU2SyIsInVzZSI6InNpZyJ9LCJhbGciOiJFUzI1NksifQ.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIklkRG9jdW1lbnQiLCJWZXJpZmlhYmxlQ3JlZGVudGlhbCJdLCJjcmVkZW50aWFsU3RhdHVzIjp7ImlkIjoiaHR0cHM6Ly9jcmVkZW50aWFsc3RhdHVzLnZlbG9jaXR5Y2FyZWVybGFicy5pbyIsInR5cGUiOiJWZWxvY2l0eVJldm9jYXRpb25SZWdpc3RyeSJ9LCJjcmVkZW50aWFsU3ViamVjdCI6eyJmaXJzdE5hbWUiOnsibG9jYWxpemVkIjp7ImVuIjoiQWRhbSJ9fSwibGFzdE5hbWUiOnsibG9jYWxpemVkIjp7ImVuIjoiU21pdGgifX0sImtpbmQiOiJEcml2ZXJzTGljZW5zZSIsImF1dGhvcml0eSI6eyJsb2NhbGl6ZWQiOnsiZW4iOiJDYWxpZm9ybmlhIERNViJ9fSwibG9jYXRpb24iOnsiY291bnRyeUNvZGUiOiJVUyIsInJlZ2lvbkNvZGUiOiJDQSJ9LCJkb2IiOnsiZGF5IjoyMCwibW9udGgiOjYsInllYXIiOjE5NjZ9LCJpZGVudGl0eU51bWJlciI6IjEyMzEwMzEyMzEyIn19LCJpc3MiOiJkaWQ6dmVsb2NpdHk6MHhiYTdkODdmOWQ1ZTQ3M2Q3ZDNhODJkMTUyOTIzYWRiNTNkZThmYzBlIiwianRpIjoiZGlkOnZlbG9jaXR5OjB4MTEyNDEzMjQ4YzhlNWJiYTZkZTcxZWNmNzQ2MWQ0N2U3YmExZmNlYiIsImlhdCI6MTYyODYxMjkyNywibmJmIjoxNjI4NjEyOTI3fQ.qNVYtZx5FUbNi-yfdBC6L2F-7OsKGHS_gHWW4KuSbbuYMdGJ_3D7tZenaMkxR0j2xFggar1FbskIxy-KO7W3Kw"

        const val AdamSmithPhoneJwt =
            "eyJ0eXAiOiJKV1QiLCJqd2siOnsiY3J2Ijoic2VjcDI1NmsxIiwieCI6IkNVZkI1eUR6WS03ZXFqYi1tamNCZ082YmhZREN3QzVhVDUyR3JXM3ljeGsiLCJ5IjoiS1FvZ1hsWTFETVViSnBqcktPdnMtOUY2NDl2aXBxMHJob3NfVk5mOU5DWSIsImt0eSI6IkVDIiwia2lkIjoiRWNWZHVnaTFKaU5WemFjTmhLSFNJQmdtREJXZ2VTVDVVY3llZ3phNVk4USIsImFsZyI6IkVTMjU2SyJ9LCJhbGciOiJFUzI1NksifQ.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIlBob25lIiwiVmVyaWZpYWJsZUNyZWRlbnRpYWwiXSwiY3JlZGVudGlhbFN0YXR1cyI6eyJpZCI6Imh0dHBzOi8vY3JlZGVudGlhbHN0YXR1cy52ZWxvY2l0eWNhcmVlcmxhYnMuaW8iLCJ0eXBlIjoiVmVsb2NpdHlSZXZvY2F0aW9uUmVnaXN0cnkifSwiY3JlZGVudGlhbFN1YmplY3QiOnsicGhvbmUiOiIrMTU1NTYxOTIxOTEifX0sImlzcyI6ImRpZDpldGhyLjB4MGIxNTRkYTQ4ZDBmMjEzYzI2YzRiMWQwNDBkYzVmZjFkYmY5OWZmYSIsImp0aSI6ImRpZDpldGhyLjB4OWFkZmE3MjA0ZjQ0OWRhOGY4Mzg3YmM0MjM3N2MwZjMyNTJjODUzOCIsImlhdCI6MTYwOTA3ODY1MiwibmJmIjoxNjA5MDc4NjUyfQ.Q2IlJCxXxVdmEOeUDfudPjCLFhEwmVWFMIdfHfpqaLLTNjh6p5YUhviBdy1CbDkM_MZvFLaZ81NHn1D4Knv1hQ"

        const val JsonObjectStr = "{\"authority\":{\"name\":{\"ui.title\":\"Issued by\"},\"identifer\":{\"ui.widget\":\"hidden\"},\"place\":{\"name\":{\"ui.widget\":\"hidden\"},\"addressCountry\":{\"ui.title\":\"Country\",\"ui:enum\":[\"TARGET_COUNTRIES_ENUM\"],\"ui:widget\":\"select\"},\"addressRegion\":{\"ui:widget\":\"hidden\"},\"addressLocality\":{\"ui.widget\":\"hidden\"}},\"ui:order\":[\"name\",\"place\"]},\"identifier\":{\"ui.title\":\"Permit number\"},\"validity\":{\"firstValidFrom\":{\"ui.title\":\"Date issued\",\"ui:widget\":\"date\"},\"validFrom\":{\"ui.title\":\"Date renewed\",\"ui:widget\":\"date\"},\"validUntil\":{\"ui.title\":\"Valid until\",\"ui:widget\":\"date\"},\"validIn\":{\"ui.widget\":\"hidden\"},\"ui:order\":[\"firstValidFrom\",\"validFrom\",\"validUntil\"]},\"person\":{\"givenName\":{\"ui.title\":\"Given Name\"},\"additionalName\":{\"ui.title\":\"Middle name\"},\"familyName\":{\"ui.title\":\"Family name\"},\"namePrefix\":{\"ui.title\":\"Name prefix\"},\"nameSuffix\":{\"ui.title\":\"Name suffix\"},\"birthDate\":{\"ui.title\":\"Birth date\",\"ui:widget\":\"date\"},\"birthPlace\":{\"name\":{\"ui.widget\":\"hidden\"},\"addressCountry\":{\"ui.title\":\"Country of birth\",\"ui:enum\":[\"TARGET_COUNTRIES_ENUM\"],\"ui:widget\":\"select\"},\"addressRegion\":{\"ui.title\":\"Region or state of birth\",\"ui:enum\":[\"TARGET_REGIONS_ENUM\"],\"ui:widget\":\"select\"},\"addressLocality\":{\"ui.title\":\"Place of birth\"},\"ui:order\":[\"addressCountry\",\"addressRegion\",\"addressLocality\"]},\"gender\":{\"ui.title\":\"Gender\"},\"ui:order\":[\"givenName\",\"additionalName\",\"familyName\",\"namePrefix\",\"nameSuffix\",\"birthDate\",\"birthPlace\",\"gender\"]},\"nationality\":{\"ui.title\":\"Nationality\"},\"address\":{\"addressCountry\":{\"ui.title\":\"Address country\",\"ui:enum\":[\"TARGET_COUNTRIES_ENUM\"],\"ui:widget\":\"select\"},\"addressRegion\":{\"ui.title\":\"Address region or state\",\"ui:enum\":[\"TARGET_REGIONS_ENUM\"],\"ui:widget\":\"select\"},\"addressLocality\":{\"ui.title\":\"Address locality\"},\"streetAddress\":{\"ui.title\":\"Street address\"},\"postCode\":{\"ui.title\":\"Post code\"},\"ui:order\":[\"addressCountry\",\"addressRegion\",\"addressLocality\",\"streetAddress\",\"postCode\"]},\"ui:order\":[\"authority\",\"identifier\",\"validity\",\"person\",\"nationality\",\"address\"]}"

        val JsonObject = JSONObject(JsonObjectStr)

        val JWK = "{\"alg\":\"ES256K\",\"use\":\"sig\",\"kid\":\"uemn6l5ro6hLNrgiPRl1Dy51V9whez4tu4hlwsNOTVk\",\"crv\":\"secp256k1\",\"x\":\"oLYCa-AlnVpW8Rq9iST_1eY_XoyvGRry7y1xS4vU4qo\",\"y\":\"PUMAsawZ24WaSnRIdDb_wNbShAvfsGF71ke1DcJGxlM\",\"kty\":\"EC\"}\n"

        val PresentationRequestJwt = "eyJ0eXAiOiJKV1QiLCJraWQiOiJkaWQ6dmVsb2NpdHk6MHhkNjAyMzFhM2QwZGUwZjE5N2YxNzg0ZjZmMzdlYmNmYWEyOTFhYjIzI2tleS0xIiwiYWxnIjoiRVMyNTZLIn0.eyJleGNoYW5nZV9pZCI6IjYwODk2NDViNDY2N2M4NDQ5YzQzM2EwMSIsIm1ldGFkYXRhIjp7ImNsaWVudF9uYW1lIjoiR29vZ2xlIiwibG9nb191cmkiOiJodHRwczovL2V4cHJlc3N3cml0ZXJzLmNvbS93cC1jb250ZW50L3VwbG9hZHMvMjAxNS8wOS9nb29nbGUtbmV3LWxvZ28tMTI4MHg3MjAuanBnIiwidG9zX3VyaSI6Imh0dHBzOi8vcmVxdWlzaXRpb25zLmFjbWUuZXhhbXBsZS5jb20vZGlzY2xvc3VyZS10ZXJtcy5odG1sIiwibWF4X3JldGVudGlvbl9wZXJpb2QiOiIybSJ9LCJwcmVzZW50YXRpb25fZGVmaW5pdGlvbiI6eyJpZCI6IjYwODk2NDViNDY2N2M4NDQ5YzQzM2EwMS41ZjRkN2VjOTQ2MTE3MDAwMDc0OWNmNzUiLCJwdXJwb3NlIjoiSm9iIG9mZmVyIiwiZm9ybWF0Ijp7Imp3dF92cCI6eyJhbGciOlsic2VjcDI1NmsxIl19fSwiaW5wdXRfZGVzY3JpcHRvcnMiOlt7ImlkIjoiSWRlbnRpdHlBbmRDb250YWN0Iiwic2NoZW1hIjpbeyJ1cmkiOiJJZGVudGl0eUFuZENvbnRhY3QifV19LHsiaWQiOiJFZHVjYXRpb25EZWdyZWUiLCJzY2hlbWEiOlt7InVyaSI6Imh0dHBzOi8vZGV2c2VydmljZXMudmVsb2NpdHljYXJlZXJsYWJzLmlvL2FwaS92MC42L3NjaGVtYXMvZWR1Y2F0aW9uLWRlZ3JlZS5zY2hlbWEuanNvbiJ9XX0seyJpZCI6IlBhc3RFbXBsb3ltZW50UG9zaXRpb24iLCJzY2hlbWEiOlt7InVyaSI6Imh0dHBzOi8vZGV2c2VydmljZXMudmVsb2NpdHljYXJlZXJsYWJzLmlvL2FwaS92MC42L3NjaGVtYXMvcGFzdC1lbXBsb3ltZW50LXBvc2l0aW9uLnNjaGVtYS5qc29uIn1dfSx7ImlkIjoiQ3VycmVudEVtcGxveW1lbnRQb3NpdGlvbiIsInNjaGVtYSI6W3sidXJpIjoiaHR0cHM6Ly9kZXZzZXJ2aWNlcy52ZWxvY2l0eWNhcmVlcmxhYnMuaW8vYXBpL3YwLjYvc2NoZW1hcy9jdXJyZW50LWVtcGxveW1lbnQtcG9zaXRpb24uc2NoZW1hLmpzb24ifV19LHsiaWQiOiJDZXJ0aWZpY2F0aW9uIiwic2NoZW1hIjpbeyJ1cmkiOiJodHRwczovL2RldnNlcnZpY2VzLnZlbG9jaXR5Y2FyZWVybGFicy5pby9hcGkvdjAuNi9zY2hlbWFzL2NlcnRpZmljYXRpb24uc2NoZW1hLmpzb24ifV19LHsiaWQiOiJCYWRnZSIsInNjaGVtYSI6W3sidXJpIjoiaHR0cHM6Ly9kZXZzZXJ2aWNlcy52ZWxvY2l0eWNhcmVlcmxhYnMuaW8vYXBpL3YwLjYvc2NoZW1hcy9iYWRnZS5zY2hlbWEuanNvbiJ9XX0seyJpZCI6IkFzc2Vzc21lbnQiLCJzY2hlbWEiOlt7InVyaSI6Imh0dHBzOi8vZGV2c2VydmljZXMudmVsb2NpdHljYXJlZXJsYWJzLmlvL2FwaS92MC42L3NjaGVtYXMvYXNzZXNzbWVudC5zY2hlbWEuanNvbiJ9XX1dfSwiaXNzIjoiZGlkOnZlbG9jaXR5OjB4ZDYwMjMxYTNkMGRlMGYxOTdmMTc4NGY2ZjM3ZWJjZmFhMjkxYWIyMyIsImlhdCI6MTYxOTYxNjg1OSwiZXhwIjoxNjIwMjIxNjU5LCJuYmYiOjE2MTk2MTY4NTl9.r6n0nwB6iTrNEEokE63fSmKIS350t_giHp8LvZLmG66ESFZHodAStTowaiHOObJr-la2Uy8uXqtQLlTBO37SGQ"


        val splittedPresentationRequestJwt = PresentationRequestJwt.split(".")

        val SignedJWT = SignedJWT(Base64URL(splittedPresentationRequestJwt[0]), Base64URL(splittedPresentationRequestJwt[1]), Base64URL(splittedPresentationRequestJwt[2]))

        val PublicKey = VCLPublicKey(JWK)

        val JWT = VCLJWT(SignedJWT)
    }
}