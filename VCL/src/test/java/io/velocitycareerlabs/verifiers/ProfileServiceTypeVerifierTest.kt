/**
 * Created by Michael Avoyan on 16/02/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.verifiers

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLStatusCode
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.data.repositories.VerifiedProfileRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.VerifiedProfileUseCaseImpl
import io.velocitycareerlabs.impl.utils.ProfileServiceTypeVerifier
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.VerifiedProfileMocks
import org.json.JSONObject
import org.junit.Test

class ProfileServiceTypeVerifierTest {

    internal lateinit var subject: ProfileServiceTypeVerifier

    @Test
    fun verificationSuccess1() {
        subject = ProfileServiceTypeVerifier(
            VerifiedProfileUseCaseImpl(
                VerifiedProfileRepositoryImpl(
                    NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileIssuerInspectorJsonStr)
                ),
                ExecutorImpl()
            )
        )

        subject.verifyServiceTypeOfVerifiedProfile(
            verifiedProfileDescriptor = VCLVerifiedProfileDescriptor(""),
            expectedServiceTypes = VCLServiceTypes(VCLIssuingType.Career),
            successHandler = {
                 assert(true)
            },
            errorHandler = {
                assert(false) { "${it.toJsonObject()}" }
            }
        )
    }

    @Test
    fun verificationSuccess2() {
        subject = ProfileServiceTypeVerifier(
            VerifiedProfileUseCaseImpl(
                VerifiedProfileRepositoryImpl(
                    NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileInspectorJsonStr)
                ),
                ExecutorImpl()
            )
        )

        subject.verifyServiceTypeOfVerifiedProfile(
            verifiedProfileDescriptor = VCLVerifiedProfileDescriptor(""),
            expectedServiceTypes = VCLServiceTypes(VCLServiceType.Inspector),
            successHandler = {
                assert(true)
            },
            errorHandler = {
                assert(false) { "${it.toJsonObject()}" }
            }
        )
    }

    @Test
    fun verificationSuccess3() {
        subject = ProfileServiceTypeVerifier(
            VerifiedProfileUseCaseImpl(
                VerifiedProfileRepositoryImpl(
                    NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileNotaryIssuerJsonStr)
                ),
                ExecutorImpl()
            )
        )

        subject.verifyServiceTypeOfVerifiedProfile(
            verifiedProfileDescriptor = VCLVerifiedProfileDescriptor(""),
            expectedServiceTypes = VCLServiceTypes(VCLIssuingType.Career),
            successHandler = {
                assert(true)
            },
            errorHandler = {
                assert(false) { "${it.toJsonObject()}" }
            }
        )
    }

    @Test
    fun verificationFailure1() {
        subject = ProfileServiceTypeVerifier(
            VerifiedProfileUseCaseImpl(
                VerifiedProfileRepositoryImpl(
                    NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileNotaryIssuerJsonStr)
                ),
                ExecutorImpl()
            )
        )

        subject.verifyServiceTypeOfVerifiedProfile(
            verifiedProfileDescriptor = VCLVerifiedProfileDescriptor(""),
            expectedServiceTypes = VCLServiceTypes(VCLIssuingType.Identity),
            successHandler = {
                assert(false)
            },
            errorHandler = { error ->
                assert(error.statusCode == VCLStatusCode.VerificationError.value)
                assert(JSONObject(error.message!!).optString("profileName") == "University of Massachusetts Amherst")
            }
        )
    }

    @Test
    fun verificationFailure2() {
        subject = ProfileServiceTypeVerifier(
            VerifiedProfileUseCaseImpl(
                VerifiedProfileRepositoryImpl(
                    NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileIssuerJsonStr1)
                ),
                ExecutorImpl()
            )
        )

        subject.verifyServiceTypeOfVerifiedProfile(
            verifiedProfileDescriptor = VCLVerifiedProfileDescriptor(""),
            expectedServiceTypes = VCLServiceTypes(VCLIssuingType.Identity),
            successHandler = {
                assert(false)
            },
            errorHandler = { error ->
                assert(error.statusCode == VCLStatusCode.VerificationError.value)
                assert(JSONObject(error.message!!).optString("profileName") == "University of Massachusetts Amherst")
            }
        )
    }

    @Test
    fun verificationFailure3() {
        subject = ProfileServiceTypeVerifier(
            VerifiedProfileUseCaseImpl(
                VerifiedProfileRepositoryImpl(
                    NetworkServiceSuccess(VerifiedProfileMocks.VerifiedProfileIssuerInspectorJsonStr)
                ),
                ExecutorImpl()
            )
        )

        subject.verifyServiceTypeOfVerifiedProfile(
            verifiedProfileDescriptor = VCLVerifiedProfileDescriptor(""),
            expectedServiceTypes = VCLServiceTypes(VCLServiceType.Undefined),
            successHandler = {
                assert(false)
            },
            errorHandler = { error ->
                assert(error.statusCode == VCLStatusCode.VerificationError.value)
                assert(JSONObject(error.message!!).optString("profileName") == "University of Massachusetts Amherst")
            }
        )
    }
}