/**
 * Created by Michael Avoyan on 04/06/2023.
 */

package io.velocitycareerlabs.impl.data.infrastructure.db

import java.io.File
import java.security.KeyStore


internal class KeyStoreProvider private constructor() {

//    val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    val keyStore: KeyStore = KeyStore.getInstance(KEY_STORE_TYPE_JKS)
//    val keyStore: KeyStore = KeyStore.getInstance(KEY_STORE_TYPE_ANDROID_KEY_STORE)

    init {
        keyStore.load(null)
    }

    companion object {
        private val KEY_STORE_TYPE_JKS = "JKS"
        private val KEY_STORE_TYPE_ANDROID_KEY_STORE = "AndroidKeyStore"

        val Instance = KeyStoreProvider()
    }
}