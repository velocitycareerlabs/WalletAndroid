/**
 * Created by Michael Avoyan on 24/12/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.velocitycareerlabs.infrastructure.resources.valid

import io.velocitycareerlabs.api.entities.VCLJwt

class TokenMocks {
    companion object {
        const val TokenStr1 =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksifQ.eyJqdGkiOiI2NTg4MGY5ZThkMjY3NWE0NTBhZDVhYjgiLCJpc3MiOiJkaWQ6aW9uOkVpQXBNTGRNYjROUGI4c2FlOS1oWEdIUDc5VzFnaXNBcFZTRTgwVVNQRWJ0SkEiLCJhdWQiOiJkaWQ6aW9uOkVpQXBNTGRNYjROUGI4c2FlOS1oWEdIUDc5VzFnaXNBcFZTRTgwVVNQRWJ0SkEiLCJleHAiOjE3MDQwMjA1MTQsInN1YiI6IjYzODZmODI0ZTc3NDc4OWM0MDNjOTZhMCIsImlhdCI6MTcwMzQxNTcxNH0.AJwKvQ_YNviFTjcuoJUR7ZHFEIbKY9zLCJv4DfC_PPk3Q-15rwKucYy8GdlfKnHLioBA5X37lpG-js8EztEKDg"
        const val TokenStr2 =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJraWQiOiIjZXhjaGFuZ2Uta2V5LTEifQ.eyJuYmYiOjE3NDQwMjMwODksImp0aSI6Im5OZEhWRktyaDk2ZjJWWXFzN29UZiIsImlzcyI6ImRpZDp3ZWI6ZGV2cmVnaXN0cmFyLnZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uOmQ6ZXhhbXBsZS0yMS5jb20tOGI4MmNlOWEiLCJhdWQiOiJkaWQ6d2ViOmRldnJlZ2lzdHJhci52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbjpkOmV4YW1wbGUtMjEuY29tLThiODJjZTlhIiwiZXhwIjoxNzQ0NjI3ODg5LCJzdWIiOiI2NjZhZTExODE5MjVmNmE0YTQ5N2RiYmMiLCJpYXQiOjE3NDQwMjMwODl9.j7Wp9DaHc6ZiFftfEf6sydy_LD73i6LW-oRoj_raOBEj4WVU3r4Qzpv8bgVUTd5_YGeZC_w2HHHRZOWOHLdJCg"

        val TokenJwt1 = VCLJwt(encodedJwt = TokenMocks.TokenStr1)
        val TokenJwt2 = VCLJwt(encodedJwt = TokenMocks.TokenStr2)
    }
}