/**
 * Created by Michael Avoyan on 14/06/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.usecases

import android.os.Build
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.JwtServiceUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.JwtServiceUseCase
import io.velocitycareerlabs.impl.extensions.toJsonObject
import io.velocitycareerlabs.impl.extensions.toPublicJwk
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.infrastructure.db.SecretStoreServiceMock
import io.velocitycareerlabs.infrastructure.resources.EmptyExecutor
import io.velocitycareerlabs.infrastructure.resources.valid.JwtServiceMocks
import io.velocitycareerlabs.infrastructure.utils.ErrorUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
internal class JwtServiceUseCaseTest {

    lateinit var subject: JwtServiceUseCase
    private val keyService = VCLKeyServiceLocalImpl(SecretStoreServiceMock.Instance)
    private lateinit var didJwkES256: VCLDidJwk
    private var didJwkSECP256k1: VCLDidJwk? = null

    @Before
    fun setUp() {
        keyService.generateDidJwk(
            VCLDidJwkDescriptor(VCLSignatureAlgorithm.ES256)
        ) { jwkResult ->
            jwkResult.handleResult({
                didJwkES256 = it
            } ,{
                assert(false) { "Failed to generate did:jwk $it" }
            })
        }

        keyService.generateDidJwk(
            VCLDidJwkDescriptor(VCLSignatureAlgorithm.SECP256k1)
        ) { jwkResult ->
            jwkResult.handleResult({
                didJwkSECP256k1 = it
            } ,{
                if (ErrorUtils.isJOSEException_Curve_not_supported_secp256k1(it)) {
                    assert(true)
                } else {
                    assert(false) { "Failed to generate did:jwk $it" }
                }
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
    fun testSignSECP256k1() {
        didJwkSECP256k1?.let {
            subject.generateSignedJwt(
                jwtDescriptor = VCLJwtDescriptor(
                    payload = JwtServiceMocks.Json.toJsonObject()!!,
                    jti = "some jti",
                    iss = "some iss"
                ),
                didJwk = it,
                remoteCryptoServicesToken = null
            ) {
                it.handleResult(
                    { jwt ->
                        assert(
                            jwt.header?.toJSONObject()
                                ?.get("alg") as? String == VCLSignatureAlgorithm.SECP256k1.jwsAlgorithm.name
                        )
                        assert(
                            ((jwt.header?.toJSONObject()
                                ?.get("jwk") as? Map<*, *>)!!["crv"] as? String) == VCLSignatureAlgorithm.SECP256k1.curve.name
                        )
                        assert(jwt.header?.toJSONObject()?.get("typ") as? String == "JWT")
                    },
                    {
                        assert(false) { "${it.toJsonObject()}" }
                    }
                )
            }
        }
    }

    @Test
    fun testSignES256() {
        subject.generateSignedJwt(
            jwtDescriptor = VCLJwtDescriptor(
                payload = JwtServiceMocks.Json.toJsonObject()!!,
                jti = "some jti",
                iss = "some iss"
            ),
            didJwk = didJwkES256,
            remoteCryptoServicesToken = null
        ) {
            it.handleResult(
                { jwt ->
                    assert(jwt.header?.toJSONObject()?.get("alg") as? String == VCLSignatureAlgorithm.ES256.jwsAlgorithm.name)
                    assert(((jwt.header?.toJSONObject()?.get("jwk") as? Map<*, *>)!!["crv"] as? String) == VCLSignatureAlgorithm.ES256.curve.name)
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
            jwtDescriptor = VCLJwtDescriptor(
                payload = JwtServiceMocks.Json.toJsonObject()!!,
                jti = "some jti",
                iss = "some iss"
            ),
            didJwk = didJwkES256,
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
    fun testSignByExistingKeySECP256k1() {
        keyService.generateDidJwk(
            VCLDidJwkDescriptor(VCLSignatureAlgorithm.SECP256k1)
        ) { didJwkResult ->
            didJwkResult.handleResult({ didJwk ->
                subject.generateSignedJwt(
                    jwtDescriptor = VCLJwtDescriptor(
                        payload = JwtServiceMocks.Json.toJsonObject()!!,
                        jti = "some jti",
                        iss = "some iss"
                    ),
                    didJwk = didJwk,
                    remoteCryptoServicesToken = null
                ) { jwtRes ->
                    jwtRes.handleResult(
                        { jwt ->
                            val publicJwk1 = VCLPublicJwk(valueStr = jwt.header?.jwk.toString())
                            val publicJwk2 = jwt.header?.toJSONObject()?.get("kid").toString().toPublicJwk()

                            assert(publicJwk1.valueStr == publicJwk2.valueStr)

                            subject.verifyJwt(
                                jwt = jwt,
                                publicJwk = publicJwk1,
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
            }, { error ->
                if (ErrorUtils.isJOSEException_Curve_not_supported_secp256k1(error)) {
                    assert(true)
                } else {
                    assert(false) { "Failed to generate did:jwk $error" }
                }
            })
        }
    }

    @Test
    fun testSignByExistingKeyES256() {
        keyService.generateDidJwk(
            VCLDidJwkDescriptor(VCLSignatureAlgorithm.ES256)
        ) { didJwkResult ->
            didJwkResult.handleResult({ didJwk ->
                subject.generateSignedJwt(
                    jwtDescriptor = VCLJwtDescriptor(
                        payload = JwtServiceMocks.Json.toJsonObject()!!,
                        jti = "some jti",
                        iss = "some iss"
                    ),
                    didJwk = didJwk,
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
}