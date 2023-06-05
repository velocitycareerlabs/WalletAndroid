/**
 * Created by Michael Avoyan on 15/05/2023.
 */

package io.velocitycareerlabs.impl.data.infrastructure.keys

import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import io.velocitycareerlabs.api.entities.VCLDidJwk
import io.velocitycareerlabs.impl.data.infrastructure.db.KeyStoreProvider
import io.velocitycareerlabs.impl.domain.infrastructure.db.SecretStoreService
import io.velocitycareerlabs.impl.domain.infrastructure.keys.KeyService
import java.util.UUID

internal class KeyServiceImpl(
    private val secretStoreService: SecretStoreService,
): KeyService {
    override fun generateDidJwk(): VCLDidJwk {
        val ecKey = generateKey()
        return VCLDidJwk(
            keyId = ecKey.keyID,
            value = VCLDidJwk.generateDidJwk(ecKey),
            kid = VCLDidJwk.generateKidFromDidJwk(ecKey)
        )
    }

    override fun generateKey(): ECKey {
        val keyId = UUID.randomUUID().toString()
        val ecKey = ECKeyGenerator(Curve.SECP256K1)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(keyId) // must be provided, otherwise ecKey.keyID is null
//            .keyStore(KeyStoreProvider.Instance.keyStore)
            .generate()
        secretStoreService.storeKey(keyId, ecKey)
        return ecKey
    }

    override fun retrieveKey(keyId: String): ECKey {
//        val jwkSet = JWKSet.load(KeyStoreProvider.Instance.keyStore, null)
//        return jwkSet.getKeyByKeyId(keyId).toECKey()

        return secretStoreService.retrieveKey(keyId).toECKey()
    }

    override fun retrievePublicJwk(ecKey: ECKey): ECKey =
        ecKey.toPublicJWK()
}