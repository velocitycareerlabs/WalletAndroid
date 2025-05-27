package io.velocitycareerlabs.infrastructure.utils

import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode

class ErrorUtils {
    companion object {
        const val JOSEException_Curve_not_supported_secp256k1 =
            "com.nimbusds.jose.JOSEException: Curve not supported: secp256k1"

        fun isJOSEException_Curve_not_supported_secp256k1(error: VCLError): Boolean {
            println(
                "---------------------------------------------------------------\n" +
                        "Known error on Mac M1: ${error.toJsonObject()}" +
                        "\n---------------------------------------------------------------"
            )
            return error.errorCode == VCLErrorCode.SdkError.value &&
                    error.message?.contains(JOSEException_Curve_not_supported_secp256k1) == true
        }
    }
}