/**
 * Created by Michael Avoyan on 21/11/2022.
 */

package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.api.entities.VCLPushDelegate
import io.velocitycareerlabs.impl.extensions.encode

class PresentationRequestDescriptorMocks {
    companion object {

        val inspectorDid = "did:velocity:0xd4df29726d500f9b85bc6c7f1b3c021f16305692"

        const val RequestUri =
            "https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f16305692%2Finspect%2Fget-presentation-request"

        val DeepLink = VCLDeepLink(value = "velocity-network-devnet://inspect?request_uri=${RequestUri}")

        var QParms = "id=61efe084b2658481a3d9248c&inspectorDid=${inspectorDid.encode()}&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22,%22KeyCode%22%3A%2254514480%22%7D,%22Token%22%3A%22832077a4%22%7D"

        var RequestUriWithQParams = "$RequestUri?$QParms"

        val DeepLinkWithQParams = VCLDeepLink(value = "velocity-network-devnet://inspect?request_uri=${RequestUriWithQParams}")

        val PushDelegate = VCLPushDelegate(
            pushUrl = "https://devservices.velocitycareerlabs.io/api/push-gateway",
            pushToken = "if0123asd129smw321"
        )
    }
}