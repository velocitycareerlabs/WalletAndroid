/**
 * Created by Michael Avoyan on 4/30/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLPresentationRequest
import io.velocitycareerlabs.api.entities.VCLVerifiedProfile
import io.velocitycareerlabs.impl.extensions.toJsonObject

class PresentationRequestMocks {
    companion object {
        const val EncodedPresentationRequest =
            "eyJ0eXAiOiJKV1QiLCJraWQiOiJkaWQ6dmVsb2NpdHk6MHhkNGRmMjk3MjZkNTAwZjliODViYzZjN2YxYjNjMDIxZjE2MzA1NjkyI2tleS0xIiwiYWxnIjoiRVMyNTZLIn0.eyJleGNoYW5nZV9pZCI6IjYwZWMxZjc2NmQyNzRlMDAwODY3NDhjYSIsIm1ldGFkYXRhIjp7ImNsaWVudF9uYW1lIjoiTWljcm9zb2Z0IENvcnBvcmF0aW9uIiwibG9nb191cmkiOiJodHRwczovL2Fnc29sLmNvbS93cC1jb250ZW50L3VwbG9hZHMvMjAxOC8wOS9uZXctbWljcm9zb2Z0LWxvZ28tU0laRUQtU1FVQVJFLmpwZyIsInRvc191cmkiOiJodHRwczovL3d3dy52ZWxvY2l0eWV4cGVyaWVuY2VjZW50ZXIuY29tL3Rlcm1zLWFuZC1jb25kaXRpb25zLXZuZiIsIm1heF9yZXRlbnRpb25fcGVyaW9kIjoiNm0ifSwicHJlc2VudGF0aW9uX2RlZmluaXRpb24iOnsiaWQiOiI2MGVjMWY3NjZkMjc0ZTAwMDg2NzQ4Y2EuNjBlYzE0M2U2ZDI3NGUwMDA4Njc0OGJjIiwicHVycG9zZSI6IklkIENoZWNrIiwiZm9ybWF0Ijp7Imp3dF92cCI6eyJhbGciOlsic2VjcDI1NmsxIl19fSwiaW5wdXRfZGVzY3JpcHRvcnMiOlt7ImlkIjoiSWREb2N1bWVudCIsInNjaGVtYSI6W3sidXJpIjoiaHR0cHM6Ly9kZXZzZXJ2aWNlcy52ZWxvY2l0eWNhcmVlcmxhYnMuaW8vYXBpL3YwLjYvc2NoZW1hcy9pZC1kb2N1bWVudC52MS5zY2hlbWEuanNvbiJ9XX0seyJpZCI6IkVtYWlsIiwic2NoZW1hIjpbeyJ1cmkiOiJodHRwczovL2RldnNlcnZpY2VzLnZlbG9jaXR5Y2FyZWVybGFicy5pby9hcGkvdjAuNi9zY2hlbWFzL2VtYWlsLnNjaGVtYS5qc29uIn1dfSx7ImlkIjoiUGhvbmUiLCJzY2hlbWEiOlt7InVyaSI6Imh0dHBzOi8vZGV2c2VydmljZXMudmVsb2NpdHljYXJlZXJsYWJzLmlvL2FwaS92MC42L3NjaGVtYXMvcGhvbmUuc2NoZW1hLmpzb24ifV19LHsiaWQiOiJQYXN0RW1wbG95bWVudFBvc2l0aW9uIiwic2NoZW1hIjpbeyJ1cmkiOiJodHRwczovL2RldnNlcnZpY2VzLnZlbG9jaXR5Y2FyZWVybGFicy5pby9hcGkvdjAuNi9zY2hlbWFzL3Bhc3QtZW1wbG95bWVudC1wb3NpdGlvbi5zY2hlbWEuanNvbiJ9XX0seyJpZCI6IkN1cnJlbnRFbXBsb3ltZW50UG9zaXRpb24iLCJzY2hlbWEiOlt7InVyaSI6Imh0dHBzOi8vZGV2c2VydmljZXMudmVsb2NpdHljYXJlZXJsYWJzLmlvL2FwaS92MC42L3NjaGVtYXMvY3VycmVudC1lbXBsb3ltZW50LXBvc2l0aW9uLnNjaGVtYS5qc29uIn1dfSx7ImlkIjoiRWR1Y2F0aW9uRGVncmVlIiwic2NoZW1hIjpbeyJ1cmkiOiJodHRwczovL2RldnNlcnZpY2VzLnZlbG9jaXR5Y2FyZWVybGFicy5pby9hcGkvdjAuNi9zY2hlbWFzL2VkdWNhdGlvbi1kZWdyZWUuc2NoZW1hLmpzb24ifV19XX0sImlzcyI6ImRpZDp2ZWxvY2l0eToweGQ0ZGYyOTcyNmQ1MDBmOWI4NWJjNmM3ZjFiM2MwMjFmMTYzMDU2OTIiLCJpYXQiOjE2MjYwODcyODYsImV4cCI6MTYyNjY5MjA4NiwibmJmIjoxNjI2MDg3Mjg2fQ.BSQnMNNJyicCsh6zeh7k5GBHC6T9QgPNV4SHhSXsnz3sBJMwNBFz7v4axLCoCiKHtIxNj5-bJ5ggI3wF6UVDeQ"
        const val EncodedPresentationRequestFeed =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJraWQiOiJkaWQ6d2ViOmRldnJlZ2lzdHJhci52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbjpkOmV4YW1wbGUtMjEuY29tLThiODJjZTlhI2V4Y2hhbmdlLWtleS0xIn0.eyJleGNoYW5nZV9pZCI6IjY3ZjI3NWYwYmQwMDE2ZjJiNDkwNWI1MSIsIm1ldGFkYXRhIjp7ImNsaWVudF9uYW1lIjoiVGV4YXMgV29tYW4ncyBVbml2ZXJzaXR5IiwibG9nb191cmkiOiJodHRwczovL2RvY3MudmVsb2NpdHljYXJlZXJsYWJzLmlvL0xvZ29zL1RXQS5wbmciLCJ0b3NfdXJpIjoiaHR0cHM6Ly93d3cudmVsb2NpdHlleHBlcmllbmNlY2VudGVyLmNvbS90ZXJtcy1hbmQtY29uZGl0aW9ucy12bmYiLCJtYXhfcmV0ZW50aW9uX3BlcmlvZCI6IjEwMG0iLCJwcm9ncmVzc191cmkiOiJodHRwczovL2RldmFnZW50LnZlbG9jaXR5Y2FyZWVybGFicy5pby9hcGkvaG9sZGVyL3YwLjYvb3JnL2RpZDp3ZWI6ZGV2cmVnaXN0cmFyLnZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uOmQ6ZXhhbXBsZS0yMS5jb20tOGI4MmNlOWEvZ2V0LWV4Y2hhbmdlLXByb2dyZXNzIiwiYXV0aF90b2tlbl91cmkiOiJodHRwczovL2RldmFnZW50LnZlbG9jaXR5Y2FyZWVybGFicy5pby9hcGkvaG9sZGVyL3YwLjYvb3JnL2RpZDp3ZWI6ZGV2cmVnaXN0cmFyLnZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uOmQ6ZXhhbXBsZS0yMS5jb20tOGI4MmNlOWEvb2F1dGgvdG9rZW4iLCJmZWVkIjp0cnVlLCJzdWJtaXRfcHJlc2VudGF0aW9uX3VyaSI6Imh0dHBzOi8vZGV2YWdlbnQudmVsb2NpdHljYXJlZXJsYWJzLmlvL2FwaS9ob2xkZXIvdjAuNi9vcmcvZGlkOndlYjpkZXZyZWdpc3RyYXIudmVsb2NpdHluZXR3b3JrLmZvdW5kYXRpb246ZDpleGFtcGxlLTIxLmNvbS04YjgyY2U5YS9pbnNwZWN0L3N1Ym1pdC1wcmVzZW50YXRpb24ifSwicHJlc2VudGF0aW9uX2RlZmluaXRpb24iOnsiaWQiOiI2N2YyNzVmMGJkMDAxNmYyYjQ5MDViNTEuNjY2YWJlZDBlZjU0NmI4OWFhZmMwYWJhIiwicHVycG9zZSI6IkRldiBUZXN0aW5nIiwibmFtZSI6IlZMLTc4MDUgVGVzdCBEaXNjbG9zdXJlIiwiZm9ybWF0Ijp7Imp3dF92cCI6eyJhbGciOlsic2VjcDI1NmsxIl19fSwiaW5wdXRfZGVzY3JpcHRvcnMiOlt7ImlkIjoiRW1haWxWMS4wIiwic2NoZW1hIjpbeyJ1cmkiOiJodHRwczovL2RldmxpYi52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbi9zY2hlbWFzL2VtYWlsLXYxLjAuc2NoZW1hLmpzb24ifV0sIm5hbWUiOiJFbWFpbCIsImdyb3VwIjpbIkEiXX1dLCJzdWJtaXNzaW9uX3JlcXVpcmVtZW50cyI6W3sicnVsZSI6InBpY2siLCJmcm9tIjoiQSIsIm1pbiI6MX1dfSwibmJmIjoxNzQzOTQzMTUyLCJpc3MiOiJkaWQ6d2ViOmRldnJlZ2lzdHJhci52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbjpkOmV4YW1wbGUtMjEuY29tLThiODJjZTlhIiwiZXhwIjoxNzQ0NTQ3OTUyLCJpYXQiOjE3NDM5NDMxNTJ9.qxfvy-lNm0s7c3yWUmjCkfsnD1Z9nAyw9tOspkeNtZhe4kMkX0-JfLbrJS90MsQExQfIU1_VoPVX_tcFgRlXng"

        const val EncodedPresentationRequestResponse =
            "{\"presentation_request\":\"$EncodedPresentationRequest\"}"

        val PresentationRequestJwt = VCLJwt(
            encodedJwt = EncodedPresentationRequest
        )
        val PresentationRequestFeedJwt = VCLJwt(
            encodedJwt = EncodedPresentationRequestFeed
        )

        const val JWK =
            "{\"alg\":\"ES256K\",\"use\":\"sig\",\"kid\":\"uemn6l5ro6hLNrgiPRl1Dy51V9whez4tu4hlwsNOTVk\",\"crv\":\"secp256k1\",\"x\":\"oLYCa-AlnVpW8Rq9iST_1eY_XoyvGRry7y1xS4vU4qo\",\"y\":\"PUMAsawZ24WaSnRIdDb_wNbShAvfsGF71ke1DcJGxlM\",\"kty\":\"EC\"}\n"

        val PresentationRequest = VCLPresentationRequest(
            jwt = PresentationRequestJwt,
            deepLink = VCLDeepLink(value = ""),
            didJwk = DidJwkMocks.DidJwk,
            verifiedProfile = VCLVerifiedProfile("{}".toJsonObject()!!)
        )

        val PresentationRequestFeed = VCLPresentationRequest(
            PresentationRequestFeedJwt,
            verifiedProfile = VCLVerifiedProfile("{}".toJsonObject()!!),
            deepLink = VCLDeepLink(value = ""),
            didJwk = DidJwkMocks.DidJwk,
        )
    }
}