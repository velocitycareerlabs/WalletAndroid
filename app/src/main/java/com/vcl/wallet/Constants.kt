/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vcl.wallet

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.VCLServiceType
import io.velocitycareerlabs.api.entities.VCLServiceTypes
import org.json.JSONObject

/**
 * Created by Michael Avoyan on 5/2/21.
 */
object Constants {
    const val OpenidInitiateIssuanceStrDev =
        "openid-initiate-issuance://?issuer=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Avelocity%3A0xc257274276a4e539741ca11b590b9447b26a8051%2Foidc%26credential_type%3DPastEmploymentPosition%26pre-authorized_code%3D8L1UArquTYvE-ylC2BV_2"

    const val PresentationRequestDeepLinkStrDev =
        "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f16305692%2Finspect%2Fget-presentation-request%3Fid%3D61efe084b2658481a3d9248c&inspectorDid=did%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f16305692&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22,%22KeyCode%22%3A%2254514480%22%7D,%22Token%22%3A%22832077a4%22%7D"

    const val PresentationRequestDeepLinkStrStaging =
        "velocity-network-testnet://inspect?request_uri=https%3A%2F%2Fstagingagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA%2Finspect%2Fget-presentation-request%3Fid%3D62e0e80c5ebfe73230b0becc&inspectorDid=did%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22,%22KeyCode%22%3A%2254514480%22%7D,%22Token%22%3A%22832077a4%22%7D"

    const val CredentialManifestDeepLinkStrDev =
        "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiAbP9xvCYnUOiLwqgbkV4auH_26Pv7BT2pYYT3masvvhw%2Fissue%2Fget-credential-manifest%3Fid%3D621c9beec8fa34b8e72d5fc7%26credential_types%3DCourseRegistrationV1.1&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22,%22KeyCode%22%3A%2254514480%22%7D,%22Token%22%3A%22832077a4%22%7D"

    const val CredentialManifestDeepLinkStrStaging =
        "velocity-network-testnet://issue?request_uri=https%3A%2F%2Fstagingagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA%2Fissue%2Fget-credential-manifest%3Fcredential_types%3DEmploymentCurrentV1.1&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22,%22KeyCode%22%3A%2254514480%22%7D,%22Token%22%3A%22832077a4%22%7D"

    const val AdamSmithEmailJwt =
        "eyJ0eXAiOiJKV1QiLCJqd2siOnsiY3J2Ijoic2VjcDI1NmsxIiwieCI6IkZOV0NZcmVDZlkyYk1VbDRIbW9oQWxSdnFJb0hPOFlnT1hpOE5IR2V4RjgiLCJ5IjoiNzBTRTU1RElMd3lWSko0YWRKUXVYVE1fdlJVY3RweWxQMnJsS3dLYnZxTSIsImt0eSI6IkVDIiwia2lkIjoiSzRtYTVBRVhYT0lMSmc0MjhNLXdfbmRwN2ltRExoaVFxdTBCUDAzUnRQWSIsImFsZyI6IkVTMjU2SyIsInVzZSI6InNpZyJ9LCJhbGciOiJFUzI1NksifQ.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIkVtYWlsVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImVtYWlsIjoiYWRhbS5zbWl0aEBleGFtcGxlLmNvbSJ9fSwiaXNzIjoiZGlkOnZlbG9jaXR5OjB4YmE3ZDg3ZjlkNWU0NzNkN2QzYTgyZDE1MjkyM2FkYjUzZGU4ZmMwZSIsImp0aSI6ImRpZDp2ZWxvY2l0eToweGE1M2RlNmJkYTM4YjVlNjVmZWVmNTk3ODZiMTRlNTRhNmY5YzRhNzMiLCJpYXQiOjE2MzQ1MTA4OTMsIm5iZiI6MTYzNDUxMDg5M30.SJwSm86k1rQc5x-JS3cup_8WqA6p06EZo1HP6smNGep8XWSxfcnUHGBYKwGDAOILVoBtQQLCK488BaQ8NSNsbw"

    const val AdamSmithDriverLicenseJwt =
        "eyJ0eXAiOiJKV1QiLCJqd2siOnsiY3J2Ijoic2VjcDI1NmsxIiwieCI6ImxkU2gybmdRempJSnhwSktMVkpvbVpEdGQtX1JRMGpzVlgzMFB5ZmdMUEUiLCJ5IjoicEJleVJuMnFYZFNELVhTaG5WVGJCa08tb0VFRmgyV2hneU1VYTRCMHV6RSIsImt0eSI6IkVDIiwia2lkIjoiUXNiZUMzTjVHN2M0NEFQS2dKdWc2X3lhS3ZrR1lQd3B5RjRHVS1sWVpVSSIsImFsZyI6IkVTMjU2SyIsInVzZSI6InNpZyJ9LCJhbGciOiJFUzI1NksifQ.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIkRyaXZlcnNMaWNlbnNlVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImZpcnN0TmFtZSI6eyJsb2NhbGl6ZWQiOnsiZW4iOiJBZGFtIn19LCJsYXN0TmFtZSI6eyJsb2NhbGl6ZWQiOnsiZW4iOiJTbWl0aCJ9fSwia2luZCI6IkRyaXZlcnNMaWNlbnNlIiwiYXV0aG9yaXR5Ijp7ImxvY2FsaXplZCI6eyJlbiI6IkNhbGlmb3JuaWEgRE1WIn19LCJsb2NhdGlvbiI6eyJjb3VudHJ5Q29kZSI6IlVTIiwicmVnaW9uQ29kZSI6IkNBIn0sImRvYiI6eyJkYXkiOjIwLCJtb250aCI6NiwieWVhciI6MTk2Nn0sImlkZW50aXR5TnVtYmVyIjoiMTIzMTAzMTIzMTIifX0sImlzcyI6ImRpZDp2ZWxvY2l0eToweGJhN2Q4N2Y5ZDVlNDczZDdkM2E4MmQxNTI5MjNhZGI1M2RlOGZjMGUiLCJqdGkiOiJkaWQ6dmVsb2NpdHk6MHgyOWRhZTM3ZGIzYzYwYmIyZmY5ZThiZmU4ZTM4YzkxZmUzYjlhZDIyIiwiaWF0IjoxNjM0NTEwODk2LCJuYmYiOjE2MzQ1MTA4OTZ9._nWDDYVf11-KeCKXLNaQ_t2giZB2chFIGS2IVjCHs3dXoUWNG5WC1e-dRTBGngrtOTXFUGHgqrkKHqwi9wDaCQ"

    const val AdamSmithPhoneJwt =
        "eyJ0eXAiOiJKV1QiLCJqd2siOnsiY3J2Ijoic2VjcDI1NmsxIiwieCI6IjFtNi1ZSWtHZTA3MmxYcUNqd1RCTExhMnN6bTZ1cGtMTTNjZnY4eVF6ZEEiLCJ5IjoiNDVBWkJlU2xVOUlSSUR5MHA5RF9kaFR4MkZ4dGQtMlBGdkVma3dsZnRGZyIsImt0eSI6IkVDIiwia2lkIjoiZnV0c2VQQUNRdFVJWnRNVlRMR1RYZzFXMGlUZG1odXJBVHZpcmxES3BwZyIsImFsZyI6IkVTMjU2SyIsInVzZSI6InNpZyJ9LCJhbGciOiJFUzI1NksifQ.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIlBob25lVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7InBob25lIjoiKzE1NTU2MTkyMTkxIn19LCJpc3MiOiJkaWQ6dmVsb2NpdHk6MHhiYTdkODdmOWQ1ZTQ3M2Q3ZDNhODJkMTUyOTIzYWRiNTNkZThmYzBlIiwianRpIjoiZGlkOnZlbG9jaXR5OjB4OGNlMzk4Y2VmNGY3ZWQ4ZWI1MGEyOGQyNWM4NjNlZWY5NjhiYjBlZSIsImlhdCI6MTYzNDUxMDg5NCwibmJmIjoxNjM0NTEwODk0fQ.g3YivH_Quiw95TywvTmiv2CBWsp5JrrCcbpOcTtYpMAQNQJD7Q3kmMYTBs1Zg3tKFRPSJ_XozFIXug5nsn2SGg"

//    Credential id is taken from jti field
    const val CredentialId1 = "did:velocity:v2:0x2bef092530ccc122f5fe439b78eddf6010685e88:248532930732481:1963"
    const val CredentialId2 = "did:velocity:v2:0x2bef092530ccc122f5fe439b78eddf6010685e88:248532930732481:1963"
    val CredentialIds = listOf(CredentialId1, CredentialId2)

    const val IssuingServiceEndPoint =
        "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3/issue/get-credential-manifest"

    const val IssuingServiceJsonStr =
        "{\"id\":\"did:velocity:0x571cf9ef33b111b7060942eb43133c0b347c7ca3#credential-agent-issuer-1\",\"type\":\"VelocityCredentialAgentIssuer_v1.0\",\"credentialTypes\":[\"Course\",\"EducationDegree\",\"Badge\"],\"serviceEndpoint\":\"$IssuingServiceEndPoint\"}"

    val PresentationSelectionsList = listOf(
            VCLVerifiableCredential("PhoneV1.0", AdamSmithPhoneJwt),
            VCLVerifiableCredential("DriversLicenseV1.0", AdamSmithDriverLicenseJwt)
    )

    val IdentificationList = listOf(VCLVerifiableCredential("EmailV1.0", AdamSmithEmailJwt))

    val OrganizationsSearchDescriptor = VCLOrganizationsSearchDescriptor(
        filter = VCLFilter(
//            did = DID,
            serviceTypes = VCLServiceTypes(VCLServiceType.Issuer),
            credentialTypes = listOf("EducationDegree")
        ),
        page = VCLPage("1", "1"),
        sort = listOf(listOf("createdAt","DESC"), listOf("pdatedAt", "ASC")),
        query = "Bank"
    )

    const val DidDev = "did:velocity:0x2bef092530ccc122f5fe439b78eddf6010685e88"
    const val DidStaging = "did:ion:EiDaeg3OofbDCdaQi5-zOLGfhZ9-boS0-w5URDfVwrI7BQ"

    val OrganizationsSearchDescriptorByDidDev = VCLOrganizationsSearchDescriptor(
        filter = VCLFilter(
            did = DidDev
        )
    )
    val OrganizationsSearchDescriptorByDidStaging = VCLOrganizationsSearchDescriptor(
        filter = VCLFilter(
            did = DidStaging
        ),
    )

    val OrganizationJson = "   {\n" +
            "      \"name\":\"Association of Chartered Certified Accountants\",\n" +
            "      \"location\":{\n" +
            "         \"countryCode\":\"UK\",\n" +
            "         \"regionCode\":\"ENG\"\n" +
            "      },\n" +
            "      \"logo\":\"https:\\/\\/docs.velocitycareerlabs.io\\/Logos\\/ACCA.png\",\n" +
            "      \"website\":\"https:\\/\\/example.com\",\n" +
            "      \"founded\":\"1904\",\n" +
            "      \"permittedVelocityServiceCategories\":[\n" +
            "         \"\",\n" +
            "         \"Issuer\"\n" +
            "      ],\n" +
            "      \"did\":\"did:velocity:0x5b4a5d2fdfdbd34e73904a0c8022ed4c22136add\"\n" +
            "   }"

    val CredentialTypes = listOf("PastEmploymentPosition", "CurrentEmploymentPosition")

    val ResidentPermitV10 = "ResidentPermitV1.0"

    val approvedOfferIds = listOf(
        "60f411aa34ab34000a2f60ff",
        "60f411aa34ab34000a2f60fe"
    )
    val rejectedOfferIds = listOf(
        "60f411aa34ab34000a2f6101"
    )

    val VerifiedProfileDescriptor = VCLVerifiedProfileDescriptor(
        did = DidDev
    )

    val SomeJwt = VCLJwt(
        encodedJwt =
        "eyJ0eXAiOiJKV1QiLCJraWQiOiJkaWQ6dmVsb2NpdHk6MHhkNGRmMjk3MjZkNTAwZjliODViYzZjN2YxYjNjMDIxZjE2MzA1NjkyI2tleS0xIiwiYWxnIjoiRVMyNTZLIn0.eyJleGNoYW5nZV9pZCI6IjYxMmYzM2Q1OTRhN2IyMDAwYTExZDM3YiIsIm1ldGFkYXRhIjp7ImNsaWVudF9uYW1lIjoiTWljcm9zb2Z0IENvcnBvcmF0aW9uIiwibG9nb191cmkiOiJodHRwczovL2Fnc29sLmNvbS93cC1jb250ZW50L3VwbG9hZHMvMjAxOC8wOS9uZXctbWljcm9zb2Z0LWxvZ28tU0laRUQtU1FVQVJFLmpwZyIsInRvc191cmkiOiJodHRwczovL3d3dy52ZWxvY2l0eWV4cGVyaWVuY2VjZW50ZXIuY29tL3Rlcm1zLWFuZC1jb25kaXRpb25zLXZuZiIsIm1heF9yZXRlbnRpb25fcGVyaW9kIjoiMm0iLCJwcm9ncmVzc191cmkiOiJodHRwczovL2RldmFnZW50LnZlbG9jaXR5Y2FyZWVybGFicy5pby9hcGkvaG9sZGVyL3YwLjYvb3JnL2RpZDp2ZWxvY2l0eToweGQ0ZGYyOTcyNmQ1MDBmOWI4NWJjNmM3ZjFiM2MwMjFmMTYzMDU2OTIvZ2V0LWV4Y2hhbmdlLXByb2dyZXNzIiwic3VibWl0X3ByZXNlbnRhdGlvbl91cmkiOiJodHRwczovL2RldmFnZW50LnZlbG9jaXR5Y2FyZWVybGFicy5pby9hcGkvaG9sZGVyL3YwLjYvb3JnL2RpZDp2ZWxvY2l0eToweGQ0ZGYyOTcyNmQ1MDBmOWI4NWJjNmM3ZjFiM2MwMjFmMTYzMDU2OTIvaXNzdWUvc3VibWl0LWlkZW50aWZpY2F0aW9uIiwiY2hlY2tfb2ZmZXJzX3VyaSI6Imh0dHBzOi8vZGV2YWdlbnQudmVsb2NpdHljYXJlZXJsYWJzLmlvL2FwaS9ob2xkZXIvdjAuNi9vcmcvZGlkOnZlbG9jaXR5OjB4ZDRkZjI5NzI2ZDUwMGY5Yjg1YmM2YzdmMWIzYzAyMWYxNjMwNTY5Mi9pc3N1ZS9jcmVkZW50aWFsLW9mZmVycyIsImZpbmFsaXplX29mZmVyc191cmkiOiJodHRwczovL2RldmFnZW50LnZlbG9jaXR5Y2FyZWVybGFicy5pby9hcGkvaG9sZGVyL3YwLjYvb3JnL2RpZDp2ZWxvY2l0eToweGQ0ZGYyOTcyNmQ1MDBmOWI4NWJjNmM3ZjFiM2MwMjFmMTYzMDU2OTIvaXNzdWUvZmluYWxpemUtb2ZmZXJzIn0sInByZXNlbnRhdGlvbl9kZWZpbml0aW9uIjp7ImlkIjoiNjEyZjMzZDU5NGE3YjIwMDBhMTFkMzdiLjYwZTgwZGY5MGY5YjhlMDAxYzY4ZmMzMyIsInB1cnBvc2UiOiJDcmVkZW50aWFsIElzc3VhbmNlIiwiZm9ybWF0Ijp7Imp3dF92cCI6eyJhbGciOlsic2VjcDI1NmsxIl19fSwiaW5wdXRfZGVzY3JpcHRvcnMiOlt7ImlkIjoiUGhvbmUiLCJzY2hlbWEiOlt7InVyaSI6Imh0dHBzOi8vZGV2c2VydmljZXMudmVsb2NpdHljYXJlZXJsYWJzLmlvL2FwaS92MC42L3NjaGVtYXMvcGhvbmUuc2NoZW1hLmpzb24ifV19LHsiaWQiOiJFbWFpbCIsInNjaGVtYSI6W3sidXJpIjoiaHR0cHM6Ly9kZXZzZXJ2aWNlcy52ZWxvY2l0eWNhcmVlcmxhYnMuaW8vYXBpL3YwLjYvc2NoZW1hcy9lbWFpbC5zY2hlbWEuanNvbiJ9XX0seyJpZCI6IklkRG9jdW1lbnQiLCJzY2hlbWEiOlt7InVyaSI6Imh0dHBzOi8vZGV2c2VydmljZXMudmVsb2NpdHljYXJlZXJsYWJzLmlvL2FwaS92MC42L3NjaGVtYXMvaWQtZG9jdW1lbnQudjEuc2NoZW1hLmpzb24ifV19XX0sIm91dHB1dF9kZXNjcmlwdG9ycyI6W3siaWQiOiJQYXN0RW1wbG95bWVudFBvc2l0aW9uIiwic2NoZW1hIjpbeyJ1cmkiOiJodHRwczovL2RldnNlcnZpY2VzLnZlbG9jaXR5Y2FyZWVybGFicy5pby9hcGkvdjAuNi9zY2hlbWFzL3Bhc3QtZW1wbG95bWVudC1wb3NpdGlvbi5zY2hlbWEuanNvbiJ9XX1dLCJpc3N1ZXIiOnsiaWQiOiJkaWQ6dmVsb2NpdHk6MHhkNGRmMjk3MjZkNTAwZjliODViYzZjN2YxYjNjMDIxZjE2MzA1NjkyIn0sImlzcyI6ImRpZDp2ZWxvY2l0eToweGQ0ZGYyOTcyNmQ1MDBmOWI4NWJjNmM3ZjFiM2MwMjFmMTYzMDU2OTIiLCJpYXQiOjE2MzA0ODM0MTMsImV4cCI6MTYzMTA4ODIxMywibmJmIjoxNjMwNDgzNDEzfQ.9py7xxe60rFa_kpbG7OptU8ekKgLJGSiMfQctNvHctclPiqvnuZ-Bh1jjN0rh7V5yqFpvz8wqPTD-4Rs_1zIGg"
    )
    val SomeJwkPublic = VCLJwkPublic(
        valueStr =
        "{\"alg\":\"ES256K\",\"use\":\"sig\",\"kid\":\"uemn6l5ro6hLNrgiPRl1Dy51V9whez4tu4hlwsNOTVk\",\"crv\":\"secp256k1\",\"x\":\"oLYCa-AlnVpW8Rq9iST_1eY_XoyvGRry7y1xS4vU4qo\",\"y\":\"PUMAsawZ24WaSnRIdDb_wNbShAvfsGF71ke1DcJGxlM\",\"kty\":\"EC\"}"
    )
    val SomeJson = JSONObject("{\"p1\":\"v1\", \"p2\":\"v12\"}")
}