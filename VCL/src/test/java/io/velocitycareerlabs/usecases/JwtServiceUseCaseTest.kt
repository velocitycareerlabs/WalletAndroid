/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import com.nimbusds.jose.crypto.ECDSAVerifier
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.JwtServiceUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.JwtServiceUseCase
import io.velocitycareerlabs.impl.extensions.decodeBase64
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

    @Before
    fun setUp() {
        subject = JwtServiceUseCaseImpl(
            JwtServiceRepositoryImpl(
                JwtServiceImpl()
            ),
            EmptyExecutor()
        )
    }

    @Test
    fun testGenerateSignedJwt() {
        val iss = "some iss"
        val jti = "some jti"
        var resultJwt: VCLResult<VCLJwt>? = null

        subject.generateSignedJwt(VCLJwtDescriptor(JwtServiceMocks.JsonObject, iss, jti)) {
            resultJwt = it
        }
        val jwtJson = resultJwt?.data!!.payload.toJSONObject()!!

        assert(jwtJson["iss"] == iss)
        assert(jwtJson["jti"] == jti)
    }

    @Test
    fun testSignVerify() {
        var resultJwt: VCLResult<VCLJwt>? = null
        var resultVerified: VCLResult<Boolean>? = null

        subject.generateSignedJwt(VCLJwtDescriptor(JwtServiceMocks.JsonObject, "", "")) {
            resultJwt = it
        }
        subject.verifyJwt(resultJwt?.data!!, VCLJwkPublic(resultJwt?.data!!.header.jwk.toString())) {
            resultVerified = it
        }
//        Verification actual algorithm
        val isVerified = resultJwt?.data!!.signedJwt.verify(ECDSAVerifier(resultJwt?.data!!.header.jwk.toECKey()))

//        Assert both have the same result
        assert(resultVerified?.data!! == isVerified)
    }

    @Test
    fun testGenerateDidJwk() {
        var resultDidJwk: VCLResult<VCLDidJwk>? = null

        subject.generateDidJwk {
            resultDidJwk = it
        }
        val didJwk = resultDidJwk?.data!!

        assert(didJwk.value.startsWith(VCLDidJwk.DidJwkPrefix))
        assert(didJwk.value.substringAfter(VCLDidJwk.DidJwkPrefix).decodeBase64().isNotEmpty())
    }

    @After
    fun tearDown() {
    }
}