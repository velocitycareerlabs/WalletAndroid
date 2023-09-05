/**
 * Created by Michael Avoyan on 04/09/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs

import android.content.Context
import io.velocitycareerlabs.api.VCLKeyServiceType
import io.velocitycareerlabs.api.entities.VCLError
import io.velocitycareerlabs.api.entities.VCLErrorCode
import io.velocitycareerlabs.api.entities.initialization.VCLInjectedServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLJwtServiceUrls
import io.velocitycareerlabs.api.entities.initialization.VCLKeyServiceUrls
import io.velocitycareerlabs.api.entities.initialization.VCLKeyServicesDescriptor
import io.velocitycareerlabs.api.entities.initialization.VCLRemoteServicesUrlsDescriptor
import io.velocitycareerlabs.impl.VclBlocksProvider
import io.velocitycareerlabs.impl.jwt.VCLJwtRemoteServiceImpl
import io.velocitycareerlabs.impl.jwt.VCLJwtServiceImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceImpl
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
        val keyService = subject.chooseKeyService(
            context,
            VCLKeyServicesDescriptor()
        )
        assert(keyService is VCLKeyServiceImpl)

        val jwtService = subject.chooseJwtService(
            context,
            VCLKeyServicesDescriptor()
        )
        assert(keyService is VCLKeyServiceImpl)
        assert(jwtService is VCLJwtServiceImpl)
    }

    @Test
    fun testChooseRemoteService() {
        val keyService = subject.chooseKeyService(
            context,
            VCLKeyServicesDescriptor(
                keyServiceType = VCLKeyServiceType.Remote,
                remoteServicesUrlsDescriptor = VCLRemoteServicesUrlsDescriptor(
                    VCLKeyServiceUrls(""),
                    VCLJwtServiceUrls("", "")
                )
            )
        )
        assert(keyService is VCLKeyServiceRemoteImpl)

        val jwtService = subject.chooseJwtService(
            context,
            VCLKeyServicesDescriptor(
                keyServiceType = VCLKeyServiceType.Remote,
                remoteServicesUrlsDescriptor = VCLRemoteServicesUrlsDescriptor(
                    VCLKeyServiceUrls(""),
                    VCLJwtServiceUrls("", "")
                )
            )
        )
        assert(jwtService is VCLJwtRemoteServiceImpl)
    }

    @Test
    fun testChooseInjectedKeyService() {
        val keyService = subject.chooseKeyService(
            context,
            VCLKeyServicesDescriptor(
                keyServiceType = VCLKeyServiceType.Injected,
                injectedServicesDescriptor = VCLInjectedServicesDescriptor(
                    VCLKeyServiceMock(),
                    VCLJwtServiceMock()
                )
            )
        )
        assert(keyService is VCLKeyServiceMock)

        val jwtService = subject.chooseJwtService(
            context,
            VCLKeyServicesDescriptor(
                keyServiceType = VCLKeyServiceType.Injected,
                injectedServicesDescriptor = VCLInjectedServicesDescriptor(
                    VCLKeyServiceMock(),
                    VCLJwtServiceMock()
                )
            )
        )
        assert(jwtService is VCLJwtServiceMock)
    }

    @Test
    fun testChooseRemoteServiceError1() {
        try {
            subject.chooseKeyService(
                context,
                VCLKeyServicesDescriptor(VCLKeyServiceType.Remote)
            )
        } catch (error: VCLError){
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }

        try {
            subject.chooseJwtService(
                context,
                VCLKeyServicesDescriptor(VCLKeyServiceType.Remote)
            )
        } catch (error: VCLError){
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }
    }

    @Test
    fun testChooseInjectedServiceError1() {
        try {
            subject.chooseKeyService(
                context,
                VCLKeyServicesDescriptor(VCLKeyServiceType.Injected)
            )
        } catch (error: VCLError){
            assert(error.errorCode == VCLErrorCode.InjectedServicesNotFount.value)
        }

        try {
            subject.chooseJwtService(
                context,
                VCLKeyServicesDescriptor(VCLKeyServiceType.Injected)
            )
        } catch (error: VCLError){
            assert(error.errorCode == VCLErrorCode.InjectedServicesNotFount.value)
        }
    }

    @Test
    fun testChooseRemoteServiceError2() {
        try {
            subject.chooseKeyService(
                context,
                VCLKeyServicesDescriptor(
                    VCLKeyServiceType.Remote,
                    injectedServicesDescriptor = VCLInjectedServicesDescriptor(
                        VCLKeyServiceMock(),
                        VCLJwtServiceMock()
                    )
                )
            )
        } catch (error: VCLError){
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }

        try {
            subject.chooseJwtService(
                context,
                VCLKeyServicesDescriptor(
                    VCLKeyServiceType.Remote,
                    injectedServicesDescriptor = VCLInjectedServicesDescriptor(
                        VCLKeyServiceMock(),
                        VCLJwtServiceMock()
                    )
                )
            )
        } catch (error: VCLError){
            assert(error.errorCode == VCLErrorCode.RemoteServicesUrlsNotFount.value)
        }
    }

    @Test
    fun testChooseInjectedServiceError2() {
        try {
            subject.chooseKeyService(
                context,
                VCLKeyServicesDescriptor(
                    VCLKeyServiceType.Injected,
                    remoteServicesUrlsDescriptor = VCLRemoteServicesUrlsDescriptor(
                        VCLKeyServiceUrls(""),
                        VCLJwtServiceUrls("", "")
                    )
                )
            )
        } catch (error: VCLError){
            assert(error.errorCode == VCLErrorCode.InjectedServicesNotFount.value)
        }

        try {
            subject.chooseJwtService(
                context,
                VCLKeyServicesDescriptor(
                    VCLKeyServiceType.Injected,
                    remoteServicesUrlsDescriptor = VCLRemoteServicesUrlsDescriptor(
                        VCLKeyServiceUrls(""),
                        VCLJwtServiceUrls("", "")
                    )
                )
            )
        } catch (error: VCLError){
            assert(error.errorCode == VCLErrorCode.InjectedServicesNotFount.value)
        }
    }
}