/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import com.nimbusds.jose.crypto.ECDSAVerifier
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.JwtServiceUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.JwtServiceUseCase
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.JwtServiceMocks
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class JwtServiceUseCaseTest {

    lateinit var subject: JwtServiceUseCase

    @Before
    fun setUp() {
    }

    @Test
    fun testSignVerify() {
//        Arrange
        subject = JwtServiceUseCaseImpl(
            JwtServiceRepositoryImpl(
                JwtServiceImpl()
            ),
            EmptyExecutor()
        )
        val iss = "some iss"
        val jti = "some jti"
        var resultJwt: VCLResult<VCLJWT>? = null
        var resultVerified: VCLResult<Boolean>? = null

//        Action
        subject.generateSignedJwt(JwtServiceMocks.JsonObject, iss, jti){
            resultJwt = it
        }
        subject.verifyJwt(resultJwt?.data!!, VCLPublicKey(resultJwt?.data!!.header.jwk.toString())) {
            resultVerified = it
        }
//        Verification actual algorithm
        val isVerified = resultJwt?.data!!.signedJwt.verify(ECDSAVerifier(resultJwt?.data!!.header.jwk.toECKey()))

//        Assert both have the same result
        assert(resultVerified?.data!! == isVerified)
    }

    @After
    fun tearDown() {
    }
}