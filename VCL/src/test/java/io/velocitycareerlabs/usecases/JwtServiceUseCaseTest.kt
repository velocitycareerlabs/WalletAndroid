/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.jwt.VCLJwtServiceImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.JwtServiceUseCaseImpl
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.domain.usecases.JwtServiceUseCase
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toPublicJwk
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.JwtServiceMocks
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class JwtServiceUseCaseTest {

    lateinit var subject: JwtServiceUseCase
    lateinit var keyService: VCLKeyService

    @Before
    fun setUp() {
        subject = JwtServiceUseCaseImpl(
            JwtServiceRepositoryImpl(
                VCLJwtServiceImpl(VCLKeyServiceImpl(SecretStoreServiceMock.Instance))
            ),
            EmptyExecutor()
        )
        keyService = VCLKeyServiceImpl(SecretStoreServiceMock.Instance)
    }

    @Test
    fun testSign() {
        var resultJwt: VCLResult<VCLJwt>? = null

        subject.generateSignedJwt(
            jwtDescriptor = VCLJwtDescriptor(
                payload = JwtServiceMocks.Json.toJsonObject()!!,
                jti = "some jti",
                iss = "some iss"
            )
        ) {
            resultJwt = it
        }

        val jwt = resultJwt?.data
        assert(jwt!!.header?.toJSONObject()?.get("alg") as? String == "ES256K")
        assert(((jwt.header?.toJSONObject()?.get("jwk") as? Map<String, Any>)!!["crv"] as? String) == "secp256k1")
        assert(jwt.header?.toJSONObject()?.get("typ") as? String == "JWT")
    }

    @Test
    fun testSignVerify() {
        var resultJwt: VCLResult<VCLJwt>? = null
        var resultVerified: VCLResult<Boolean>? = null

        subject.generateSignedJwt(
            jwtDescriptor = VCLJwtDescriptor(
                payload = JwtServiceMocks.Json.toJsonObject()!!,
                jti = "some jti",
                iss = "some iss"
            )
        ) {
            resultJwt = it
        }

        val jwt = resultJwt?.data
        subject.verifyJwt(
            jwt = jwt!!,
            jwkPublic = VCLJwkPublic(valueStr = jwt.header?.jwk.toString())
        ) {
            resultVerified = it
        }
        val isVerified = resultVerified?.data as Boolean
        assert(isVerified)
    }

    @Test
    fun testSignByExistingKey() {
        keyService.generateDidJwk { didJwkResult ->
            didJwkResult.handleResult({ didJwk ->
                var resultJwt: VCLResult<VCLJwt>? = null
                var resultVerified: VCLResult<Boolean>? = null

                subject.generateSignedJwt(
                    kid = didJwk.kid,
                    nonce = "some nonce",
                    jwtDescriptor = VCLJwtDescriptor(
                        keyId = didJwk.keyId,
                        payload = JwtServiceMocks.Json.toJsonObject()!!,
                        jti = "some jti",
                        iss = "some iss"
                    )
                ) {
                    resultJwt = it
                }
                val jwt = resultJwt?.data
                subject.verifyJwt(
                    jwt = jwt!!,
//                    jwkPublic = VCLJwkPublic(valueStr = jwt.header.jwk.toString())
                    // Person binding provided did:jwk only:
                    jwkPublic = jwt.header?.toJSONObject()?.get("kid").toString().toPublicJwk()
                ) {
                    resultVerified = it
                }
                val isVerified = resultVerified?.data as Boolean
                assert(isVerified)
            }, {
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }
    }

    @After
    fun tearDown() {
    }
}