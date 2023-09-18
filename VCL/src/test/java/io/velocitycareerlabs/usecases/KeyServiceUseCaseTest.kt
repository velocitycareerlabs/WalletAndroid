/**
 * Created by Michael Avoyan on 01/06/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.data
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.repositories.KeyServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.KeyServiceUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.KeyServiceUseCase
import io.velocitycareerlabs.impl.extensions.decodeBase64
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.Exception

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class KeyServiceUseCaseTest {

    private lateinit var subject: KeyServiceUseCase

    @Before
    fun setUp() {
        subject = KeyServiceUseCaseImpl(
            KeyServiceRepositoryImpl(
                VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)
            ),
            EmptyExecutor()
        )
    }

    @Test
    fun testGenerateJwk() {
        var resultDidJwk: VCLResult<VCLDidJwk>? = null

        subject.generateDidJwk {
            it.handleResult(
                successHandler = { didJwk ->
                    val jwkJsonObj = didJwk.publicJwk.valueJson

                    assert(didJwk.did.startsWith(VCLDidJwk.DidJwkPrefix))

                    assert(jwkJsonObj.optString("kty") == "EC")
                    assert(jwkJsonObj.optString("use") == "sig")
                    assert(jwkJsonObj.optString("crv") == "secp256k1")
                    assert(jwkJsonObj.optString("use") == "sig")
                    assert(jwkJsonObj.optString("x") != null)
                    assert(jwkJsonObj.optString("y") != null)
                },
                errorHandler = {
                    assert(false) { "$it" }
                }
            )
        }
    }

    @Test
    fun testGenerateDifferentJwks() {
        var resultDidJwk1: VCLResult<VCLDidJwk>? = null
        var resultDidJwk2: VCLResult<VCLDidJwk>? = null

        subject.generateDidJwk {
            resultDidJwk1 = it
        }
        subject.generateDidJwk {
            resultDidJwk2 = it
        }
        try {
            val didJwk1 = resultDidJwk1?.data
            val didJwk2 = resultDidJwk2?.data
            assert(didJwk1!!.did != didJwk2!!.did)
            assert(didJwk1.keyId != didJwk2.keyId)
        } catch (ex: Exception) {
            assert(false) {"$ex"}
        }
    }
}