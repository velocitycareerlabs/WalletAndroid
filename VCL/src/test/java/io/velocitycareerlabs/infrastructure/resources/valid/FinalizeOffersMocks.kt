/**
 * Created by Michael Avoyan on 12/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

class FinalizeOffersMocks {
    companion object {
        const val EncodedJwtVerifiableCredential = "eyJ0eXAiOiJKV1QiLCJraWQiOiJkaWQ6dmVsb2NpdHk6MHhlMTVjNmZlNzZkZDI4MGZlYmU5OGM1YzdlM2Y5NDk0MTY2ZWQ3ZDg5I2tleS0xIiwiYWxnIjoiRVMyNTZLIn0.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIlBhc3RFbXBsb3ltZW50UG9zaXRpb24iLCJWZXJpZmlhYmxlQ3JlZGVudGlhbCJdLCJjcmVkZW50aWFsU2NoZW1hIjp7ImlkIjoiaHR0cHM6Ly9kZXZzZXJ2aWNlcy52ZWxvY2l0eWNhcmVlcmxhYnMuaW8vYXBpL3YwLjYvc2NoZW1hcy9wYXN0LWVtcGxveW1lbnQtcG9zaXRpb24uc2NoZW1hLmpzb24iLCJ0eXBlIjoiSnNvblNjaGVtYVZhbGlkYXRvcjIwMTgifSwiY3JlZGVudGlhbFN0YXR1cyI6eyJ0eXBlIjoiVmVsb2NpdHlSZXZvY2F0aW9uTGlzdEphbjIwMjEiLCJpZCI6ImV0aGVyZXVtOjB4Zjc1NUUxQ2E2NmJFMTJGMTc3MTc4RTdFYTY5Njk2OUUwQTU1QmI2NC9nZXRSZXZva2VkU3RhdHVzP2FkZHJlc3M9MHhENERGMjk3MjZENTAwRjliODVCYzZDN0YxYjNDMDIxZjE2MzA1NjkyJmxpc3RJZD0xMDAwMjU3NDUzJmluZGV4PTc4NjAiLCJyZXZvY2F0aW9uTGlzdEluZGV4Ijo3ODYwLCJyZXZvY2F0aW9uTGlzdENyZWRlbnRpYWwiOiJldGhlcmV1bToweGY3NTVFMUNhNjZiRTEyRjE3NzE3OEU3RWE2OTY5NjlFMEE1NUJiNjQvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4RDRERjI5NzI2RDUwMEY5Yjg1QmM2QzdGMWIzQzAyMWYxNjMwNTY5MiZsaXN0SWQ9MTAwMDI1NzQ1MyIsImxpbmtDb2RlQ29tbWl0IjoiXHUwMDEyXHUwMDAwIn0sImNvbnRlbnRIYXNoIjp7InR5cGUiOiJWZWxvY2l0eUNvbnRlbnRIYXNoMjAyMCIsInZhbHVlIjoiOTUwOWU2OGQ3NTM1MzFkNGMwZWVhZjI0NDQ4ZTZiZDdjNmM5ODQ0YzYwODBkNDE1YjkxOGE0MTdlOWRhZDdiNSJ9LCJjcmVkZW50aWFsU3ViamVjdCI6eyJjb21wYW55IjoiZGlkOmV0aHI6MHhkNGRmMjk3MjZkNTAwZjliODViYzZjN2YxYjNjMDIxZjE2MzA1NjkyIiwiY29tcGFueU5hbWUiOnsibG9jYWxpemVkIjp7ImVuIjoiTWljcm9zb2Z0IENvcnBvcmF0aW9uIn19LCJ0aXRsZSI6eyJsb2NhbGl6ZWQiOnsiZW4iOiJEaXJlY3RvciwgQ29tbXVuaWNhdGlvbnMgKEhvbG9MZW5zICYgTWl4ZWQgUmVhbGl0eSBFeHBlcmllbmNlcykifX0sInN0YXJ0TW9udGhZZWFyIjp7Im1vbnRoIjoiMTAiLCJ5ZWFyIjoiMjAxMCJ9LCJlbmRNb250aFllYXIiOnsibW9udGgiOiIwNiIsInllYXIiOiIyMDE5In0sImxvY2F0aW9uIjp7ImNvdW50cnlDb2RlIjoiVVMiLCJyZWdpb25Db2RlIjoiTUEifSwiYWxpZ25tZW50IjpbeyJ0YXJnZXROYW1lIjoiRGlyZWN0b3IsIENvbW11bmljYXRpb25zIiwidGFyZ2V0VXJsIjoiTWljcm9zb2Z0LmNvbSIsInRhcmdldERlc2NyaXB0aW9uIjoibCBEYXRhLCBBSSwgSHlicmlkLCBJb1QsIERhdGFjZW50ZXIsIE1peGVkIFJlYWxpdHkvSG9sb0xlbnMsIEQzNjUsIFBvd2VyIFBsYXRmb3JtIC0gYWxsIGtpbmRzIG9mIGZ1biBzdHVmZiEifV19fSwiaXNzIjoiZGlkOnZlbG9jaXR5OjB4ZDRkZjI5NzI2ZDUwMGY5Yjg1YmM2YzdmMWIzYzAyMWYxNjMwNTY5MiIsImp0aSI6ImRpZDp2ZWxvY2l0eToweGUxNWM2ZmU3NmRkMjgwZmViZTk4YzVjN2UzZjk0OTQxNjZlZDdkODkiLCJpYXQiOjE2MjA4MTkzODAsIm5iZiI6MTYyMDgxOTM4MH0.0uQemCE2iZz80O7h_xXUcVzp4Ra-qqs1t8F4DrkIMgmK5bO_fIQo_Dz1N-UR3CnkLxTUFuQalyDBiJdGzuVdyg"
        const val EncodedJwtVerifiableCredentials = "[\"$EncodedJwtVerifiableCredential\"]"
    }
}