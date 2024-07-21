/**
 * Created by Michael Avoyan on 01/06/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources

import io.velocitycareerlabs.api.entities.VCLJwt
import io.velocitycareerlabs.api.entities.VCLToken



class CommonMocks {
    companion object {
        val UUID1 = "6E6B7489-13C8-45E8-A24D-0BC309B484D1"
        val UUID2 = "6E6B7489-13C8-45E8-A24D-0BC309B484D2"
        val UUID3 = "6E6B7489-13C8-45E8-A24D-0BC309B484D3"
        val UUID4 = "6E6B7489-13C8-45E8-A24D-0BC309B484D4"
        val UUID5 = "6E6B7489-13C8-45E8-A24D-0BC309B484D5"
        val DID1 = "did:ion:EiAbP9xvCYnUOiLwqgbkV4auH_26Pv7BT2pYYT3masvvh1"
        val DID2 = "did:ion:EiAbP9xvCYnUOiLwqgbkV4auH_26Pv7BT2pYYT3masvvh2"
        val DID3 = "did:ion:EiAbP9xvCYnUOiLwqgbkV4auH_26Pv7BT2pYYT3masvvh3"
        val DID4 = "did:ion:EiAbP9xvCYnUOiLwqgbkV4auH_26Pv7BT2pYYT3masvvh4"
        val DID5 = "did:ion:EiAbP9xvCYnUOiLwqgbkV4auH_26Pv7BT2pYYT3masvvh5"

        val JWT = VCLJwt("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")

        var Token = VCLToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    }
}