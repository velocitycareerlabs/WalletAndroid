/**
 * Created by Michael Avoyan on 4/28/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.repositories

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.VCLDeepLink
import io.velocitycareerlabs.impl.data.repositories.PresentationRequestRepositoryImpl
import io.velocitycareerlabs.impl.domain.repositories.PresentationRequestRepository
import io.velocitycareerlabs.infrastructure.network.NetworkServiceFailure
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationRequestMocks
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch

internal class PresentationRequestRepositoryTest {

    lateinit var subject: PresentationRequestRepository

    @Before
    fun setUp() {
    }

    @Test
    fun testGetPresentationRequestFailEmptyStringt() {
//        Arrange
        val jsonFail = ""
        subject = PresentationRequestRepositoryImpl(NetworkServiceFailure(jsonFail))
        var result: VCLResult<String>? = null
        val latch = CountDownLatch(1)

//        Action
        subject.getPresentationRequest(VCLPresentationRequestDescriptor(VCLDeepLink(""))) {
            result = it
            latch.countDown()
        }
        latch.await()

//        Assert
        assert(result is VCLResult.Failure)
    }

    @Test
    fun getPresentationRequestSuccessTest() {
//        Arrange
        subject = PresentationRequestRepositoryImpl(NetworkServiceSuccess(PresentationRequestMocks.EncodedPresentationRequestResponse))
        var result: VCLResult<String>? = null
        val latch = CountDownLatch(1)
//        Action
        subject.getPresentationRequest(VCLPresentationRequestDescriptor(VCLDeepLink(""))) {
            result = it
            latch.countDown()
        }
        latch.await()
//        Assert
        assert(result is VCLResult.Success)
        assert(result?.data.equals(PresentationRequestMocks.EncodedPresentationRequest))
    }

    @After
    fun tearDown() {
    }
}