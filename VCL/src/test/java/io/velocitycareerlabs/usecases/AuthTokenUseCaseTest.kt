/**
 * Created by Michael Avoyan on 27/04/2025.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.VCLAuthTokenDescriptor
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.data.repositories.AuthTokenRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.AuthTokenUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.AuthTokenUseCase
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.TokenMocks
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

class AuthTokenUseCaseTest {
    internal lateinit var subject: AuthTokenUseCase

    private val expectedAuthToken = TokenMocks.AuthToken
    private val expectedAuthTokenStr = TokenMocks.AuthTokenStr

    @Test
    fun testGetAuthTokenSuccess() {
        subject = AuthTokenUseCaseImpl(
            authTokenRepository = AuthTokenRepositoryImpl(
                networkService = NetworkServiceSuccess(expectedAuthTokenStr)
            ),
            executor = EmptyExecutor()
        )

        subject.getAuthToken(VCLAuthTokenDescriptor(
            authTokenUri = "",
            walletDid = "wallet did",
            relyingPartyDid = "relying party did")
        ) {
            it.handleResult(successHandler = { authToken ->
                JSONAssert.assertEquals(
                    authToken.payload,
                    TokenMocks.AuthToken.payload,
                    JSONCompareMode.LENIENT
                )
                assertEquals(authToken.accessToken.value, expectedAuthToken.accessToken.value)
                assertEquals(authToken.refreshToken.value, expectedAuthToken.refreshToken.value)
                assertEquals(authToken.walletDid, expectedAuthToken.walletDid)
                assertEquals(authToken.relyingPartyDid, expectedAuthToken.relyingPartyDid)
            }, errorHandler = {
                assert(false) { "${it.toJsonObject()}" }
            })
        }
    }

    @Test
    fun testGetAuthTokenFailure() {
        subject = AuthTokenUseCaseImpl(
            authTokenRepository = AuthTokenRepositoryImpl(
                networkService = NetworkServiceSuccess("Wrong payload")
            ),
            executor = EmptyExecutor()
        )

        subject.getAuthToken(VCLAuthTokenDescriptor(authTokenUri = "")) {
            it.handleResult(successHandler = {
                assert(false) { "${VCLErrorCode.SdkError.value} error code is expected" }
            }, errorHandler = { error ->
                assert(error.errorCode == VCLErrorCode.SdkError.value)
            })
        }
    }
}