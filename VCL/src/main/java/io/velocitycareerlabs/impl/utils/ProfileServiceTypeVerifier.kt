/**
 * Created by Michael Avoyan on 16/02/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.utils

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.VCLImpl
import io.velocitycareerlabs.impl.domain.usecases.VerifiedProfileUseCase
import org.json.JSONObject
import java.lang.Exception

internal class ProfileServiceTypeVerifier(
    private val verifiedProfileUseCase: VerifiedProfileUseCase
    ) {

    fun verifyServiceTypeOfVerifiedProfile(
        verifiedProfileDescriptor: VCLVerifiedProfileDescriptor,
        expectedServiceTypes: VCLServiceTypes,
        successHandler: () -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        verifiedProfileUseCase.getVerifiedProfile(verifiedProfileDescriptor) { verifiedProfileResult ->
            verifiedProfileResult.handleResult(
                successHandler = { verifiedProfile ->
                    verifyServiceType(
                        verifiedProfile = verifiedProfile,
                        expectedServiceTypes = expectedServiceTypes,
                        successHandler = {
                            successHandler()
                        },
                        errorHandler = {
                            errorHandler(it)
                        }
                    )
                },
                errorHandler = {
                    errorHandler(it)
                }
            )
        }
    }

    private fun verifyServiceType(
        verifiedProfile: VCLVerifiedProfile,
        expectedServiceTypes: VCLServiceTypes,
        successHandler: () -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        if (verifiedProfile.serviceTypes.containsAtLeastOneOf(expectedServiceTypes))
            successHandler()
        else
            errorHandler(
                VCLError(
                    toJasonString(
                        verifiedProfile.name,
                        "Wrong service type - expected: ${expectedServiceTypes.all}, found: ${verifiedProfile.serviceTypes.all}"
                    ),
                    VCLErrorCode.VerificationError
                )
            )
    }

    private fun toJasonString(profileName: String?, message: String?): String {
        try {
            val jsonObject = JSONObject()
            jsonObject.putOpt("profileName", profileName)
                .putOpt("message", message)
            return jsonObject.toString()
        } catch (e: Exception) {
            VCLLog.e(VCLImpl.TAG, "", e)
        }
        return "$profileName $message"
    }
}