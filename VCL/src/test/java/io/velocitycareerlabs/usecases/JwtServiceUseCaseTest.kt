/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.JwtServiceUseCaseImpl
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.domain.usecases.JwtServiceUseCase
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toPublicJwk
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.JwtServiceMocks
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class JwtServiceUseCaseTest {

    lateinit var subject: JwtServiceUseCase
    private lateinit var didJwk: VCLDidJwk
    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)

    @Before
    fun setUp() {
        keyService.generateDidJwk(null) { didJwkResult ->
            didJwkResult.handleResult({
                didJwk = it
            }, {
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }

        subject = JwtServiceUseCaseImpl(
            JwtServiceRepositoryImpl(
                VCLJwtSignServiceLocalImpl(keyService),
                VCLJwtVerifyServiceLocalImpl()
            ),
            EmptyExecutor()
        )
    }

    @Test
    fun testSign() {
        subject.generateSignedJwt(
            didJwk = didJwk,
            jwtDescriptor = VCLJwtDescriptor(
                payload = JwtServiceMocks.Json.toJsonObject()!!,
                jti = "some jti",
                iss = "some iss"
            ),
            remoteCryptoServicesToken = null
        ) {
            it.handleResult(
                { jwt ->
                    assert(jwt.header?.toJSONObject()?.get("alg") as? String == "ES256K")
                    assert(((jwt.header?.toJSONObject()?.get("jwk") as? Map<String, Any>)!!["crv"] as? String) == "secp256k1")
                    assert(jwt.header?.toJSONObject()?.get("typ") as? String == "JWT")
                },
                {
                    assert(false) { "${it.toJsonObject()}" }
                }
            )
        }
    }

    @Test
    fun testSignVerify() {
        subject.generateSignedJwt(
            didJwk = didJwk,
            jwtDescriptor = VCLJwtDescriptor(
                payload = JwtServiceMocks.Json.toJsonObject()!!,
                jti = "some jti",
                iss = "some iss"
            ),
            remoteCryptoServicesToken = null
        ) {
            it.handleResult(
                { jwt ->
                    subject.verifyJwt(
                        jwt = jwt,
                        publicJwk = VCLPublicJwk(valueStr = jwt.header?.jwk.toString()),
                        remoteCryptoServicesToken = null
                    ) { isVerifiedRes ->
                        isVerifiedRes.handleResult(
                            { isVerified ->
                                assert(isVerified)
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

    @Test
    fun testSignByExistingKey() {
        keyService.generateDidJwk(null) { didJwkResult ->
            didJwkResult.handleResult({ didJwk ->
                subject.generateSignedJwt(
                    didJwk = didJwk,
                    nonce = "some nonce",
                    jwtDescriptor = VCLJwtDescriptor(
                        payload = JwtServiceMocks.Json.toJsonObject()!!,
                        jti = "some jti",
                        iss = "some iss"
                    ),
                    remoteCryptoServicesToken = null
                ) { jwtRes ->
                    jwtRes.handleResult(
                        { jwt ->
                            subject.verifyJwt(
                                jwt = jwt,
//                    publicJwk = VCLPublicJwk(valueStr = jwt.header.jwk.toString())
                                // Person binding provided did:jwk only:
                                publicJwk = jwt.header?.toJSONObject()?.get("kid").toString().toPublicJwk(),
                                remoteCryptoServicesToken = null
                            ) { isVerifiedRes ->
                                isVerifiedRes.handleResult(
                                    { isVerified ->
                                        assert(isVerified)
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
            }, {
                assert(false) { "Failed to generate did:jwk ${it.toJsonObject()}" }
            })
        }
    }

    @After
    fun tearDown() {
    }
}