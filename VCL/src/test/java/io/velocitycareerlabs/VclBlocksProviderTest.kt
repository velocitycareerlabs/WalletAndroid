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
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.remote.VCLJwtSignServiceRemoteImpl
import io.velocitycareerlabs.impl.jwt.remote.VCLJwtVerifyServiceRemoteImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceRemoteImpl
import io.velocitycareerlabs.infrastructure.resources.valid.VCLJwtSignServiceMock
import io.velocitycareerlabs.infrastructure.resources.valid.VCLJwtVerifyServiceMock
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

            val jwtSignService = subject.chooseJwtSignService(
                context,
                VCLCryptoServicesDescriptor()
            )
            val jwtVerifyService = subject.chooseJwtVerifyService(
                VCLCryptoServicesDescriptor()
            )
            assert(keyService is VCLKeyServiceLocalImpl)
            assert(jwtSignService is VCLJwtSignServiceLocalImpl)
            assert(jwtVerifyService is VCLJwtVerifyServiceLocalImpl)
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

            val jwtSignService = subject.chooseJwtSignService(
                context,
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Remote,
                    remoteCryptoServicesUrlsDescriptor = VCLRemoteCryptoServicesUrlsDescriptor(
                        VCLKeyServiceUrls(""),
                        VCLJwtServiceUrls("", "")
                    )
                )
            )
            assert(jwtSignService is VCLJwtSignServiceRemoteImpl)

            val jwtVerifyService = subject.chooseJwtVerifyService(
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Remote,
                    remoteCryptoServicesUrlsDescriptor = VCLRemoteCryptoServicesUrlsDescriptor(
                        VCLKeyServiceUrls(""),
                        VCLJwtServiceUrls("", "")
                    )
                )
            )
            assert(jwtVerifyService is VCLJwtVerifyServiceRemoteImpl)

            val jwtVerifyServiceVerifyUrlNull = subject.chooseJwtVerifyService(
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Remote,
                    remoteCryptoServicesUrlsDescriptor = VCLRemoteCryptoServicesUrlsDescriptor(
                        VCLKeyServiceUrls(""),
                        VCLJwtServiceUrls("")
                    )
                )
            )
            assert(jwtVerifyServiceVerifyUrlNull is VCLJwtVerifyServiceLocalImpl)
        } catch (ex: Exception) {
            assert(false) { "$ex" }
        }
    }

    @Test
    fun testChooseInjectedKeyService() {
        val injectedCryptoServicesDescriptor = VCLInjectedCryptoServicesDescriptor(
            VCLKeyServiceMock(),
            VCLJwtSignServiceMock(),
            VCLJwtVerifyServiceMock()
        )

        try {
            val keyService = subject.chooseKeyService(
                context,
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Injected,
                    injectedCryptoServicesDescriptor = injectedCryptoServicesDescriptor
                )
            )
            assert(keyService is VCLKeyServiceMock)

            val jwtSignService = subject.chooseJwtSignService(
                context,
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Injected,
                    injectedCryptoServicesDescriptor = injectedCryptoServicesDescriptor
                )
            )
            assert(jwtSignService is VCLJwtSignServiceMock)

            val jwtVerifyService = subject.chooseJwtVerifyService(
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Injected,
                    injectedCryptoServicesDescriptor = injectedCryptoServicesDescriptor
                )
            )
            assert(jwtVerifyService is VCLJwtVerifyServiceMock)

            val jwtVerifyServiceNull = subject.chooseJwtVerifyService(
                VCLCryptoServicesDescriptor(
                    cryptoServiceType = VCLCryptoServiceType.Injected,
                    injectedCryptoServicesDescriptor = VCLInjectedCryptoServicesDescriptor(
                        VCLKeyServiceMock(),
                        VCLJwtSignServiceMock()
                    )
                )
            )
            assert(jwtVerifyServiceNull is VCLJwtVerifyServiceLocalImpl)
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
            subject.chooseJwtSignService(
                context,
                VCLCryptoServicesDescriptor(VCLCryptoServiceType.Remote)
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }

        try {
            subject.chooseJwtVerifyService(
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
            subject.chooseJwtSignService(
                context,
                VCLCryptoServicesDescriptor(VCLCryptoServiceType.Injected)
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.InjectedServicesNotFount.value)
        }

        try {
            subject.chooseJwtVerifyService(
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
                        VCLJwtSignServiceMock(),
                        VCLJwtVerifyServiceMock()
                    )
                )
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }

        try {
            subject.chooseJwtSignService(
                context,
                VCLCryptoServicesDescriptor(
                    VCLCryptoServiceType.Remote,
                    injectedCryptoServicesDescriptor = VCLInjectedCryptoServicesDescriptor(
                        VCLKeyServiceMock(),
                        VCLJwtSignServiceMock(),
                        VCLJwtVerifyServiceMock()
                    )
                )
            )
        } catch (error: VCLError) {
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }

        try {
            subject.chooseJwtVerifyService(
                VCLCryptoServicesDescriptor(
                    VCLCryptoServiceType.Remote,
                    injectedCryptoServicesDescriptor = VCLInjectedCryptoServicesDescriptor(
                        VCLKeyServiceMock(),
                        VCLJwtSignServiceMock(),
                        VCLJwtVerifyServiceMock()
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
            subject.chooseJwtSignService(
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
            subject.chooseJwtVerifyService(
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