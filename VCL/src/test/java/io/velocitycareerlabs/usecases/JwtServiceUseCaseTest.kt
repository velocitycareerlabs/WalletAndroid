/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.keys.KeyServiceImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.JwtServiceUseCaseImpl
import io.velocitycareerlabs.impl.domain.infrastructure.keys.KeyService
import io.velocitycareerlabs.impl.domain.usecases.JwtServiceUseCase
import io.velocitycareerlabs.impl.extensions.toJsonObject
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
    lateinit var keyService: KeyService

    @Before
    fun setUp() {
        subject = JwtServiceUseCaseImpl(
            JwtServiceRepositoryImpl(
                JwtServiceImpl(KeyServiceImpl(SecretStoreServiceMock.Instance))
            ),
            EmptyExecutor()
        )
        keyService = KeyServiceImpl(SecretStoreServiceMock.Instance)
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
        assert(jwt!!.header.toJSONObject()["alg"] as? String == "ES256K")
        assert(((jwt.header.toJSONObject()["jwk"] as? Map<String, Any>)!!["crv"] as? String) == "secp256k1")
        assert(jwt.header.toJSONObject()["typ"] as? String == "JWT")
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
            jwkPublic = VCLJwkPublic(valueStr = jwt.header.jwk.toString())
        ) {
            resultVerified = it
        }
        val isVerified = resultVerified?.data as Boolean
        assert(isVerified)
    }

    @Test
    fun testSignByExistingKey() {
        val didJwk = keyService.generateDidJwk()

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
            jwkPublic = VCLJwkPublic(valueStr = jwt.header.jwk.toString())
        ) {
            resultVerified = it
        }
        val isVerified = resultVerified?.data as Boolean
        assert(isVerified)
    }

    @After
    fun tearDown() {
    }
}