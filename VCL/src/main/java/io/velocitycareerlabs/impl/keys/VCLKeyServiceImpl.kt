/**
 * Created by Michael Avoyan on 15/05/2023.
 */

package io.velocitycareerlabs.impl.keys

import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.domain.infrastructure.db.SecretStoreService
import io.velocitycareerlabs.api.keys.VCLKeyService
import java.util.UUID

internal class VCLKeyServiceImpl(
    private val secretStoreService: SecretStoreService,
): VCLKeyService {
    override fun generateDidJwk(
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    ) {
        generateSecret { ecKeyResult ->
            ecKeyResult.handleResult(
                successHandler = { ecKey ->
                    completionBlock(
                    VCLResult.Success(
                        VCLDidJwk(
                            keyId = ecKey.keyID,
                            value = VCLDidJwk.generateDidJwk(ecKey),
                            kid = VCLDidJwk.generateKidFromDidJwk(ecKey)
                        )
                    ))
                },
                errorHandler = { error ->
                    completionBlock(VCLResult.Failure(error))
                }
            )
        }
    }

    override fun generateSecret(
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
        try {
            val keyId = UUID.randomUUID().toString()
            val ecKey = ECKeyGenerator(Curve.SECP256K1)
                .keyUse(KeyUse.SIGNATURE)
                .keyID(keyId) // must be provided, otherwise ecKey.keyID is null
//            .keyStore(KeyStoreProvider.Instance.keyStore)
                .generate()
            secretStoreService.storeKey(keyId, ecKey)
            completionBlock(VCLResult.Success(ecKey))
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }

    override fun retrieveSecretReference(
        keyId: String,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
//        val jwkSet = JWKSet.load(KeyStoreProvider.Instance.keyStore, null)
//        return jwkSet.getKeyByKeyId(keyId).toECKey()

        try {
            completionBlock(VCLResult.Success(secretStoreService.retrieveKey(keyId).toECKey()))
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }

    override fun retrievePublicJwk(
        ecKey: ECKey,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
        try {
            completionBlock(VCLResult.Success(ecKey.toPublicJWK()))
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex)))
        }
    }
}