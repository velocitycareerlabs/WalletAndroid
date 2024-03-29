/**
 * Created by Michael Avoyan on 15/05/2023.
 */

package io.velocitycareerlabs.impl.keys

import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.api.entities.VCLDidJwkDescriptor
import io.velocitycareerlabs.api.entities.VCLPublicJwk
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.VCLToken
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.domain.infrastructure.db.SecretStoreService
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.GlobalConfig
import java.util.UUID

internal class VCLKeyServiceLocalImpl(
    private val secretStoreService: SecretStoreService,
): VCLKeyService {
    override fun generateDidJwk(
        didJwkDescriptor: VCLDidJwkDescriptor,
        completionBlock: (VCLResult<VCLDidJwk>) -> Unit
    ) {
        generateSecret(
            signatureAlgorithm = didJwkDescriptor.signatureAlgorithm
        ) { ecKeyResult ->
            ecKeyResult.handleResult(
                successHandler = { ecKey ->
                    completionBlock(
                    VCLResult.Success(
                        VCLDidJwk(
                            did = VCLDidJwk.generateDidJwk(ecKey),
                            publicJwk = VCLPublicJwk(ecKey.toPublicJWK().toJSONString()),
                            kid = VCLDidJwk.generateKidFromDidJwk(ecKey),
                            keyId = ecKey.keyID
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
        signatureAlgorithm: VCLSignatureAlgorithm,
        completionBlock: (VCLResult<ECKey>) -> Unit
    ) {
        try {
            val keyId = UUID.randomUUID().toString()
            val ecKey = ECKeyGenerator(signatureAlgorithm.curve)
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