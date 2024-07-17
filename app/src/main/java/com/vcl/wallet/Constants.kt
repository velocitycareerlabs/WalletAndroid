/**
 * Created by Michael Avoyan on 18/07/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vcl.wallet

import io.velocitycareerlabs.api.VCLEnvironment
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.VCLServiceType
import io.velocitycareerlabs.api.entities.VCLServiceTypes
import org.json.JSONObject

object Constants {
    const val PresentationRequestDeepLinkStrDev =
        "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiAbP9xvCYnUOiLwqgbkV4auH_26Pv7BT2pYYT3masvvhw%2Finspect%2Fget-presentation-request%3Fid%3D62d8f05788de05e27930b037&inspectorDid=did%3Aion%3AEiAbP9xvCYnUOiLwqgbkV4auH_26Pv7BT2pYYT3masvvhw"

    const val PresentationRequestDeepLinkStrStaging =
//        "velocity-network-testnet://inspect?request_uri=https%3A%2F%2Fstagingagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA%2Finspect%2Fget-presentation-request%3Fid%3D62e0e80c5ebfe73230b0becc&inspectorDid=did%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22,%22KeyCode%22%3A%2254514480%22%7D,%22Token%22%3A%22832077a4%22%7D"
        "velocity-network-testnet://inspect?request_uri=https%3A%2F%2Fstagingagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiC8GZpBYJXt5UhqxZJbixJyMjrGw0yw8yFN6HjaM1ogSw%2Finspect%2Fget-presentation-request%3Fid%3D64dcc64915b0660266ae356a&inspectorDid=did%3Aion%3AEiC8GZpBYJXt5UhqxZJbixJyMjrGw0yw8yFN6HjaM1ogSw"

    const val CredentialManifestDeepLinkStrDev =
//        "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Fissue%2Fget-credential-manifest%3Fid%3D6384a3ad148b1991687f67c9%26credential_types%3DEmploymentPastV1.1"
        "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Fissue%2Fget-credential-manifest%3Fid%3D6384a3ad148b1991687f67c9%26credential_types%3DEmploymentPastV1.1%26issuerDid%3Ddid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA"

//    Open badge deep link
//        "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiBMsw27IKRYIdwUOfDeBd0LnWVeG2fPxxJi9L1fvjM20g%2Fissue%2Fget-credential-manifest%3Fid%3D65520714d778c03b43828792%26credential_types%3DOpenBadgeCredential"
//        "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiBMsw27IKRYIdwUOfDeBd0LnWVeG2fPxxJi9L1fvjM20g%2Fissue%2Fget-credential-manifest%3Fid%3D65520714d778c03b43828792%26credential_types%3DOpenBadgeCredential%26issuerDid%3Ddid%3Aion%3AEiBMsw27IKRYIdwUOfDeBd0LnWVeG2fPxxJi9L1fvjM20g"

    const val CredentialManifestDeepLinkStrStaging =
//        "velocity-network-testnet://issue?request_uri=https%3A%2F%2Fstagingagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA%2Fissue%2Fget-credential-manifest%3Fid%3D624d65daf18484b8525288c3%26credential_types%3DEmploymentPastV1.1"
        "velocity-network-testnet://issue?request_uri=https%3A%2F%2Fstagingagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA%2Fissue%2Fget-credential-manifest%3Fid%3D624d65daf18484b8525288c3&issuerDid=did%3Aion%3AEiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA"

    const val AdamSmithPhoneJwtDev =
        "eyJ0eXAiOiJKV1QiLCJqd2siOnsiY3J2Ijoic2VjcDI1NmsxIiwieCI6IjFtNi1ZSWtHZTA3MmxYcUNqd1RCTExhMnN6bTZ1cGtMTTNjZnY4eVF6ZEEiLCJ5IjoiNDVBWkJlU2xVOUlSSUR5MHA5RF9kaFR4MkZ4dGQtMlBGdkVma3dsZnRGZyIsImt0eSI6IkVDIiwia2lkIjoiZnV0c2VQQUNRdFVJWnRNVlRMR1RYZzFXMGlUZG1odXJBVHZpcmxES3BwZyIsImFsZyI6IkVTMjU2SyIsInVzZSI6InNpZyJ9LCJhbGciOiJFUzI1NksifQ.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIlBob25lVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7InBob25lIjoiKzE1NTU2MTkyMTkxIn19LCJpc3MiOiJkaWQ6dmVsb2NpdHk6MHhiYTdkODdmOWQ1ZTQ3M2Q3ZDNhODJkMTUyOTIzYWRiNTNkZThmYzBlIiwianRpIjoiZGlkOnZlbG9jaXR5OjB4OGNlMzk4Y2VmNGY3ZWQ4ZWI1MGEyOGQyNWM4NjNlZWY5NjhiYjBlZSIsImlhdCI6MTYzNDUxMDg5NCwibmJmIjoxNjM0NTEwODk0fQ.g3YivH_Quiw95TywvTmiv2CBWsp5JrrCcbpOcTtYpMAQNQJD7Q3kmMYTBs1Zg3tKFRPSJ_XozFIXug5nsn2SGg"
    const val AdamSmithEmailJwtDev =
        "eyJ0eXAiOiJKV1QiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHg2MjU2YjE4OTIxZWFiZDM5MzUxZWMyM2YxYzk0Zjg4MDYwNGU3MGU3OjIxMTQ4ODcxODM1NTAwODo0MTY2I2tleS0xIiwiYWxnIjoiRVMyNTZLIn0.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIkVtYWlsVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdGF0dXMiOnsidHlwZSI6IlZlbG9jaXR5UmV2b2NhdGlvbkxpc3RKYW4yMDIxIiwiaWQiOiJldGhlcmV1bToweEQ4OTBGMkQ2MEI0MjlmOWUyNTdGQzBCYzU4RWYyMjM3Nzc2REQ5MUIvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4MDMwMThFM2EzODk3MzRhRTEyZjE0RTQ0NTQwZkFlYTM1NzkxZkVDNyZsaXN0SWQ9MTYzNTc4ODY2Mjk2NjUzJmluZGV4PTg2OTgiLCJzdGF0dXNMaXN0SW5kZXgiOjg2OTgsInN0YXR1c0xpc3RDcmVkZW50aWFsIjoiZXRoZXJldW06MHhEODkwRjJENjBCNDI5ZjllMjU3RkMwQmM1OEVmMjIzNzc3NkREOTFCL2dldFJldm9rZWRTdGF0dXM_YWRkcmVzcz0weDAzMDE4RTNhMzg5NzM0YUUxMmYxNEU0NDU0MGZBZWEzNTc5MWZFQzcmbGlzdElkPTE2MzU3ODg2NjI5NjY1MyIsImxpbmtDb2RlQ29tbWl0IjoiRWlBb3FJWWYycmgxdzEvdURXTnNwYTRyOHRrV2dwRGRUUjBtNHlIRTVMZUtQZz09In0sImNvbnRlbnRIYXNoIjp7InR5cGUiOiJWZWxvY2l0eUNvbnRlbnRIYXNoMjAyMCIsInZhbHVlIjoiODlkNGRjYzg2ZDU0MGM2ZWVhMzlkMTc4ZWVkYzMwMjEzZTc4MmYyNTFlMDNiNzZmNDI3MzEwNjgwOGRkMGQ0ZiJ9LCJjcmVkZW50aWFsU2NoZW1hIjp7ImlkIjoiaHR0cHM6Ly9kZXZyZWdpc3RyYXIudmVsb2NpdHluZXR3b3JrLmZvdW5kYXRpb24vc2NoZW1hcy9lbWFpbC12MS4wLnNjaGVtYS5qc29uIiwidHlwZSI6Ikpzb25TY2hlbWFWYWxpZGF0b3IyMDE4In0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImVtYWlsIjoiYWRhbS5zbWl0aEBleGFtcGxlLmNvbSJ9fSwiaXNzIjoiZGlkOmlvbjpFaUFlaFdtcFg1bUhCdWM5M1NJaFBYRjhic0V4NjhHNm1QY2RJYUxOR2JvelBBIiwianRpIjoiZGlkOnZlbG9jaXR5OnYyOjB4NjI1NmIxODkyMWVhYmQzOTM1MWVjMjNmMWM5NGY4ODA2MDRlNzBlNzoyMTE0ODg3MTgzNTUwMDg6NDE2NiIsImlhdCI6MTY1Mjg5Njg2OSwibmJmIjoxNjUyODk2ODY5fQ.fi0qJFzHiDEWTGUu0ME1aG36-j2jm7xxA2DWPs_Ra7ftl-ALMu0FY3A38klbkJQYCaXWHFH0hBbcQ5Z3uZCeew"

    const val AdamSmithPhoneJwtStaging =
        "eyJ0eXAiOiJKV1QiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHgzNGZmY2M5Y2NiMGI4MGVjZjljMzQxYmEyNzI2Y2UxNzlmM2M3YTg5OjE4NTA3MDUzODAwODI2ODoyMzc3I2tleS0xIiwiYWxnIjoiRVMyNTZLIn0.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIlBob25lVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdGF0dXMiOnsidHlwZSI6IlZlbG9jaXR5UmV2b2NhdGlvbkxpc3RKYW4yMDIxIiwiaWQiOiJldGhlcmV1bToweDFDMjk0NjFDNzQ4MGQxZDg1NzBkZjdjMEE0RjMxNEQwYkU4Y0Q1QmYvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4ZUM1MjliN2JiQzg0ODIxRDJlNzEwMDFjOUY0ZmVkODBkZDQ0YjY1NSZsaXN0SWQ9NTg5OTgwOTMyODEwNDImaW5kZXg9NTQ0NiIsInN0YXR1c0xpc3RJbmRleCI6NTQ0Niwic3RhdHVzTGlzdENyZWRlbnRpYWwiOiJldGhlcmV1bToweDFDMjk0NjFDNzQ4MGQxZDg1NzBkZjdjMEE0RjMxNEQwYkU4Y0Q1QmYvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4ZUM1MjliN2JiQzg0ODIxRDJlNzEwMDFjOUY0ZmVkODBkZDQ0YjY1NSZsaXN0SWQ9NTg5OTgwOTMyODEwNDIiLCJsaW5rQ29kZUNvbW1pdCI6IkVpQ01VLzhnNEkxWVdZTmI2OGpsUFQwMEEwWStqVEtLV2p5TXJsZ2NpOW5BUHc9PSJ9LCJjb250ZW50SGFzaCI6eyJ0eXBlIjoiVmVsb2NpdHlDb250ZW50SGFzaDIwMjAiLCJ2YWx1ZSI6IjRmMDI1MmM0ZTEyOGU2ZDMxOTdjZWEwN2I3NmZjYmFhMTI2ZGUzZDQwYmY2NTc5ZGZhNTM0NWU1Y2YxYThmYjIifSwiY3JlZGVudGlhbFNjaGVtYSI6eyJpZCI6Imh0dHBzOi8vc3RhZ2luZ3JlZ2lzdHJhci52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbi9zY2hlbWFzL3Bob25lLXYxLjAuc2NoZW1hLmpzb24iLCJ0eXBlIjoiSnNvblNjaGVtYVZhbGlkYXRvcjIwMTgifSwiY3JlZGVudGlhbFN1YmplY3QiOnsicGhvbmUiOiIrMTU1NTYxOTIxOTEifX0sImlzcyI6ImRpZDppb246RWlDQ04tNlpSaG9Kd0RQdTgzQVBwaUltT3RJUHljMWNDNzJraFZxV3lCSTNGdyIsImp0aSI6ImRpZDp2ZWxvY2l0eTp2MjoweDM0ZmZjYzljY2IwYjgwZWNmOWMzNDFiYTI3MjZjZTE3OWYzYzdhODk6MTg1MDcwNTM4MDA4MjY4OjIzNzciLCJpYXQiOjE2NTM2NDMxNDksIm5iZiI6MTY1MzY0MzE0OX0.J018SSJ4b2f1EapuIsmevqGpHrrn3D3S4qIg1AogIdVIH_u_5WliUkHstpPdaF5EJSzk_ab-nql_JbvjlWpeNA"
    const val AdamSmithEmailJwtStaging =
        "eyJ0eXAiOiJKV1QiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHgzNGZmY2M5Y2NiMGI4MGVjZjljMzQxYmEyNzI2Y2UxNzlmM2M3YTg5OjE4NTA3MDUzODAwODI2ODoxNjk2I2tleS0xIiwiYWxnIjoiRVMyNTZLIn0.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIkVtYWlsVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdGF0dXMiOnsidHlwZSI6IlZlbG9jaXR5UmV2b2NhdGlvbkxpc3RKYW4yMDIxIiwiaWQiOiJldGhlcmV1bToweDFDMjk0NjFDNzQ4MGQxZDg1NzBkZjdjMEE0RjMxNEQwYkU4Y0Q1QmYvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4ZUM1MjliN2JiQzg0ODIxRDJlNzEwMDFjOUY0ZmVkODBkZDQ0YjY1NSZsaXN0SWQ9NTg5OTgwOTMyODEwNDImaW5kZXg9MjY5NSIsInN0YXR1c0xpc3RJbmRleCI6MjY5NSwic3RhdHVzTGlzdENyZWRlbnRpYWwiOiJldGhlcmV1bToweDFDMjk0NjFDNzQ4MGQxZDg1NzBkZjdjMEE0RjMxNEQwYkU4Y0Q1QmYvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4ZUM1MjliN2JiQzg0ODIxRDJlNzEwMDFjOUY0ZmVkODBkZDQ0YjY1NSZsaXN0SWQ9NTg5OTgwOTMyODEwNDIiLCJsaW5rQ29kZUNvbW1pdCI6IkVpQVBlLzAyY0t1Q1A4cWM0eGhncjMxd3JhN2pUM29nQnI5L3lEN0pRTXdJZWc9PSJ9LCJjb250ZW50SGFzaCI6eyJ0eXBlIjoiVmVsb2NpdHlDb250ZW50SGFzaDIwMjAiLCJ2YWx1ZSI6Ijg5ZDRkY2M4NmQ1NDBjNmVlYTM5ZDE3OGVlZGMzMDIxM2U3ODJmMjUxZTAzYjc2ZjQyNzMxMDY4MDhkZDBkNGYifSwiY3JlZGVudGlhbFNjaGVtYSI6eyJpZCI6Imh0dHBzOi8vc3RhZ2luZ3JlZ2lzdHJhci52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbi9zY2hlbWFzL2VtYWlsLXYxLjAuc2NoZW1hLmpzb24iLCJ0eXBlIjoiSnNvblNjaGVtYVZhbGlkYXRvcjIwMTgifSwiY3JlZGVudGlhbFN1YmplY3QiOnsiZW1haWwiOiJhZGFtLnNtaXRoQGV4YW1wbGUuY29tIn19LCJpc3MiOiJkaWQ6aW9uOkVpQ0NOLTZaUmhvSndEUHU4M0FQcGlJbU90SVB5YzFjQzcya2hWcVd5QkkzRnciLCJqdGkiOiJkaWQ6dmVsb2NpdHk6djI6MHgzNGZmY2M5Y2NiMGI4MGVjZjljMzQxYmEyNzI2Y2UxNzlmM2M3YTg5OjE4NTA3MDUzODAwODI2ODoxNjk2IiwiaWF0IjoxNjUzNjQzMTQ5LCJuYmYiOjE2NTM2NDMxNDl9.mUjH9z2xRP4ZUgWpPCRHS2G6_PCzJteYjrAYkHUkHOva2iYVKZ2LHjxkWLHzeG7YF4269544RYKUC7jGFmAk5g"

    //    Credential id is taken from jti field
    private const val CredentialId1Dev =
        "did:velocity:v2:0x2bef092530ccc122f5fe439b78eddf6010685e88:248532930732481:1963"
    private const val CredentialId2Dev =
        "did:velocity:v2:0x2bef092530ccc122f5fe439b78eddf6010685e88:248532930732481:1963"
    private const val CredentialId1Staging =
        "did:velocity:v2:0xfef35344bca1454bbe844e13af77c92d4fbed13b:73421631052335:6705"
    private const val CredentialId2Staging =
        "did:velocity:v2:0xfef35344bca1454bbe844e13af77c92d4fbed13b:73421631052335:9368"
    fun getCredentialIdsToRefresh(environment: VCLEnvironment) =
        if (environment == VCLEnvironment.Dev)
            listOf(CredentialId1Dev, CredentialId2Dev)
        else listOf(CredentialId1Staging, CredentialId2Staging)

    const val IssuingServiceEndPoint =
        "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/issue/get-credential-manifest"

    const val IssuingServiceJsonStr =
        "{\"id\":\"did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA#credential-agent-issuer-1\",\"type\":\"VelocityCredentialAgentIssuer_v1.0\",\"credentialTypes\":[\"Course\",\"EducationDegree\",\"Badge\"],\"serviceEndpoint\":\"$IssuingServiceEndPoint\"}"

    private val IdentificationListListDev = listOf(
        VCLVerifiableCredential(inputDescriptor = "PhoneV1.0", jwtVc = AdamSmithPhoneJwtDev),
        VCLVerifiableCredential(inputDescriptor = "EmailV1.0", jwtVc = AdamSmithEmailJwtDev),
    )

    private val IdentificationListStaging = listOf(
        VCLVerifiableCredential(inputDescriptor = "PhoneV1.0", jwtVc = AdamSmithPhoneJwtStaging),
        VCLVerifiableCredential(inputDescriptor = "EmailV1.0", jwtVc = AdamSmithEmailJwtStaging),
    )

    fun getIdentificationList(environment: VCLEnvironment) =
        if (environment == VCLEnvironment.Dev)
            Constants.IdentificationListListDev
        else
            Constants.IdentificationListStaging

    val OrganizationsSearchDescriptor = VCLOrganizationsSearchDescriptor(
        filter = VCLFilter(
//            did: DID,
            serviceTypes = VCLServiceTypes(serviceType = VCLServiceType.Issuer),
            credentialTypes = listOf("EducationDegree")
        ),
        page = VCLPage(size = "1", skip = "1"),
        sort = listOf(listOf("createdAt", "DESC"), listOf("pdatedAt", "ASC")),
        query = "Bank"
    )

    const val IssuerDidDev = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA"
    const val IssuerDidStaging = "did:ion:EiByBvq95tfmhl41DOxJeaa26HjSxAUoz908PITFwMRDNA"

    val OrganizationsSearchDescriptorByDidDev = VCLOrganizationsSearchDescriptor(
        filter = VCLFilter(
            did = IssuerDidDev
        )
    )
    val OrganizationsSearchDescriptorByDidStaging = VCLOrganizationsSearchDescriptor(
        filter = VCLFilter(
            did = IssuerDidStaging
        )
    )

    val CredentialTypes = listOf(
        "EducationDegreeRegistrationV1.0",
        "EducationDegreeStudyV1.0",
        "EducationDegreeGraduationV1.0",
        "EmploymentPastV1.1",
        "Badge",
        "BadgeV1.1",
        "OpenBadgeV1.0",
        "OpenBadgeV2.0",
        "OpenBadgeCredential"
    )

    const val ResidentPermitV10 = "ResidentPermitV1.0"

    fun getVerifiedProfileDescriptor(environment: VCLEnvironment) =
        if(environment == VCLEnvironment.Dev)
            VCLVerifiedProfileDescriptor(did = IssuerDidDev)
        else VCLVerifiedProfileDescriptor(did = IssuerDidStaging)

    val SomeJwt = VCLJwt(
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJqd2siOnsia3R5IjoiRUMiLCJjcnYiOiJzZWNwMjU2azEiLCJ4IjoiQ1JFNzc0WV8ydkctdTZka2UwSmQzYVhrd1R4WkE2TV96cDZ2TkR0Vmt5NCIsInkiOiJZLWhIdS1FSXlHSGFRRTdmamxZVVlBZ2lVanFqZFc2VXlIaHI2OVFZTS04IiwidXNlIjoic2lnIn19.eyJwMSI6InYxIiwicDIiOiJ2MTIiLCJuYmYiOjE2OTQ0MzUyMjAsImp0aSI6Ijk4YTc4MGFmLTIyZGYtNGU3ZC1iYTZjLTBmYjE0Njk2Zjg0NSIsImlzcyI6ImlzczEyMyIsInN1YiI6IlpHNXQwT1ZrT08iLCJpYXQiOjE2OTQ0MzUyMjB9.kaEGDsRFjFylIAQ1DDX0GQyWBD1y5rG7WNpFZbrL1DFPrfFgDrydXXOCaBbr8TN81kPrbkscsHUuioY-tGCxMw"
    )
    val SomePublicJwk = VCLPublicJwk(
        valueStr = "{ \"kty\": \"EC\", \"crv\": \"secp256k1\", \"x\": \"CRE774Y_2vG-u6dke0Jd3aXkwTxZA6M_zp6vNDtVky4\", \"y\": \"Y-hHu-EIyGHaQE7fjlYUYAgiUjqjdW6UyHhr69QYM-8\", \"use\": \"sig\" }"
    )
    val SomePayload = JSONObject("{\"p1\":\"v1\", \"p2\":\"v12\"}")

    private const val BaseUrl = "mockvendor.velocitycareerlabs.io"
    private fun getServiceBaseUrl(environment: VCLEnvironment): String {
        return when (environment) {
            VCLEnvironment.Dev ->
                "https://${VCLEnvironment.Dev.value}$BaseUrl"

            VCLEnvironment.Qa -> // not available yet
                "https://${VCLEnvironment.Qa.value}$BaseUrl"

            VCLEnvironment.Staging -> // not available yet
                "https://${VCLEnvironment.Staging.value}$BaseUrl"

            else -> // prod not available yet
                "https://$BaseUrl"
        }
    }

    fun getJwtSignServiceUrl(environment: VCLEnvironment): String {
        return "${getServiceBaseUrl(environment = environment)}/api/jwt/sign"
    }

    fun getJwtVerifyServiceUrl(environment: VCLEnvironment): String {
        return "${getServiceBaseUrl(environment = environment)}/api/jwt/verify"
    }

    fun getCreateDidKeyServiceUrl(environment: VCLEnvironment): String {
        return "${getServiceBaseUrl(environment = environment)}/api/create_did_key"
    }
}