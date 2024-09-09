/**
 * Created by Michael Avoyan on 01/06/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLDidJwkDescriptor
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.repositories.KeyServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.KeyServiceUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.KeyServiceUseCase
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

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
    fun testGenerateJwkES256() {
        subject.generateDidJwk(
            VCLDidJwkDescriptor(VCLSignatureAlgorithm.ES256)
        ) {
            it.handleResult(
                successHandler = { didJwk ->
                    val jwkJsonObj = didJwk.publicJwk.valueJson

                    assert(didJwk.did.startsWith(VCLDidJwk.DidJwkPrefix))

                    assert(jwkJsonObj.optString("kty") == "EC")
                    assert(jwkJsonObj.optString("use") == "sig")
                    assert(jwkJsonObj.optString("crv") == VCLSignatureAlgorithm.ES256.curve.name)
                    assert(jwkJsonObj.optString("use") == "sig")
                    assert(jwkJsonObj.optString("x") != null)
                    assert(jwkJsonObj.optString("y") != null)
                },
                errorHandler = {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testGenerateJwkSECP256k1() {
        subject.generateDidJwk(
            VCLDidJwkDescriptor(VCLSignatureAlgorithm.SECP256k1)
        ) {
            it.handleResult(
                successHandler = { didJwk ->
                    val jwkJsonObj = didJwk.publicJwk.valueJson

                    assert(didJwk.did.startsWith(VCLDidJwk.DidJwkPrefix))

                    assert(jwkJsonObj.optString("kty") == "EC")
                    assert(jwkJsonObj.optString("use") == "sig")
                    assert(jwkJsonObj.optString("crv") == VCLSignatureAlgorithm.SECP256k1.curve.name)
                    assert(jwkJsonObj.optString("use") == "sig")
                    assert(jwkJsonObj.optString("x") != null)
                    assert(jwkJsonObj.optString("y") != null)
                },
                errorHandler = {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testGenerateDifferentJwks() {
        subject.generateDidJwk(
            VCLDidJwkDescriptor(VCLSignatureAlgorithm.ES256)
        ) { didJwk1Res ->
            didJwk1Res.handleResult(
                { didJwk1 ->
                    subject.generateDidJwk(
                        VCLDidJwkDescriptor(VCLSignatureAlgorithm.ES256)
                    ) { didJwk2Res ->
                        didJwk2Res.handleResult(
                            { didJwk2 ->
                                assert(didJwk1.did != didJwk2.did)
                                assert(didJwk1.keyId != didJwk2.keyId)
                            },
                            {
                                assert(false) { "${it.toJsonObject()}" }
                            }
                        )
                    }
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }
}