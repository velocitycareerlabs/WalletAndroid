/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs

import android.content.Context
import io.velocitycareerlabs.api.VCLCryptoServiceType
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.initialization.VCLInjectedCryptoServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLJwtServiceUrls
import io.velocitycareerlabs.api.entities.initialization.VCLKeyServiceUrls
import io.velocitycareerlabs.api.entities.initialization.VCLCryptoServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLRemoteCryptoServicesUrlsDescriptor
import io.velocitycareerlabs.impl.VclBlocksProvider
import io.velocitycareerlabs.impl.jwt.VCLJwtServiceRemoteImpl
import io.velocitycareerlabs.impl.jwt.VCLJwtServiceLocalImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceRemoteImpl
import io.velocitycareerlabs.infrastructure.resources.valid.VCLJwtServiceMock
import io.velocitycareerlabs.infrastructure.resources.valid.VCLKeyServiceMock
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

internal class VclBlocksProviderTest {
    val subject = VclBlocksProvider

    @Mock
    lateinit var context: Context

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testChooseServiceByDefault() {
        try {
            val keyService = subject.chooseKeyService(
                context,
                VCLCryptoServicesDescriptor()
            )
            assert(keyService is VCLKeyServiceLocalImpl)

            val jwtService = subject.chooseJwtService(
                context,
                VCLCryptoServicesDescriptor()
            )
            assert(keyService is VCLKeyServiceLocalImpl)
            assert(jwtService is VCLJwtServiceLocalImpl)
        } catch (ex: Exception) {
            assert(false) { "$ex" }
        }
    }

    @Test
    fun testChooseRemoteService() {
        try {
            val keyService = subject.chooseKeyService(
                context,
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Remote,
                    remoteCryptoServicesUrlsDescriptor = VCLRemoteCryptoServicesUrlsDescriptor(
                        VCLKeyServiceUrls(""),
                        VCLJwtServiceUrls("", "")
                    )
                )
            )
            assert(keyService is VCLKeyServiceRemoteImpl)

            val jwtService = subject.chooseJwtService(
                context,
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Remote,
                    remoteCryptoServicesUrlsDescriptor = VCLRemoteCryptoServicesUrlsDescriptor(
                        VCLKeyServiceUrls(""),
                        VCLJwtServiceUrls("", "")
                    )
                )
            )
            assert(jwtService is VCLJwtServiceRemoteImpl)
        } catch (ex: Exception) {
            assert(false) { "$ex" }
        }
    }

    @Test
    fun testChooseInjectedKeyService() {
        try {
            val keyService = subject.chooseKeyService(
                context,
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Injected,
                    injectedCryptoServicesDescriptor = VCLInjectedCryptoServicesDescriptor(
                        VCLKeyServiceMock(),
                        VCLJwtServiceMock()
                    )
                )
            )
            assert(keyService is VCLKeyServiceMock)

            val jwtService = subject.chooseJwtService(
                context,
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Injected,
                    injectedCryptoServicesDescriptor = VCLInjectedCryptoServicesDescriptor(
                        VCLKeyServiceMock(),
                        VCLJwtServiceMock()
                    )
                )
            )
            assert(jwtService is VCLJwtServiceMock)
        } catch (ex: Exception) {
            assert(false) { "$ex" }
        }
    }

    @Test
    fun testChooseRemoteServiceError1() {
        try {
            subject.chooseKeyService(
                context,
                VCLCryptoServicesDescriptor(VCLCryptoServiceType.Remote)
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }

        try {
            subject.chooseJwtService(
                context,
                VCLCryptoServicesDescriptor(VCLCryptoServiceType.Remote)
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }
    }

    @Test
    fun testChooseInjectedServiceError1() {
        try {
            subject.chooseKeyService(
                context,
                VCLCryptoServicesDescriptor(VCLCryptoServiceType.Injected)
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.InjectedServicesNotFount.value)
        }

        try {
            subject.chooseJwtService(
                context,
                VCLCryptoServicesDescriptor(VCLCryptoServiceType.Injected)
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.InjectedServicesNotFount.value)
        }
    }

    @Test
    fun testChooseRemoteServiceError2() {
        try {
            subject.chooseKeyService(
                context,
                VCLCryptoServicesDescriptor(
                    VCLCryptoServiceType.Remote,
                    injectedCryptoServicesDescriptor = VCLInjectedCryptoServicesDescriptor(
                        VCLKeyServiceMock(),
                        VCLJwtServiceMock()
                    )
                )
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }

        try {
            subject.chooseJwtService(
                context,
                VCLCryptoServicesDescriptor(
                    VCLCryptoServiceType.Remote,
                    injectedCryptoServicesDescriptor = VCLInjectedCryptoServicesDescriptor(
                        VCLKeyServiceMock(),
                        VCLJwtServiceMock()
                    )
                )
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }
    }

    @Test
    fun testChooseInjectedServiceError2() {
        try {
            subject.chooseKeyService(
                context,
                VCLCryptoServicesDescriptor(
                    VCLCryptoServiceType.Injected,
                    remoteCryptoServicesUrlsDescriptor = VCLRemoteCryptoServicesUrlsDescriptor(
                        VCLKeyServiceUrls(""),
                        VCLJwtServiceUrls("", "")
                    )
                )
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.InjectedServicesNotFount.value)
        }

        try {
            subject.chooseJwtService(
                context,
                VCLCryptoServicesDescriptor(
                    VCLCryptoServiceType.Injected,
                    remoteCryptoServicesUrlsDescriptor = VCLRemoteCryptoServicesUrlsDescriptor(
                        VCLKeyServiceUrls(""),
                        VCLJwtServiceUrls("", "")
                    )
                )
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.InjectedServicesNotFount.value)
        }
    }
}