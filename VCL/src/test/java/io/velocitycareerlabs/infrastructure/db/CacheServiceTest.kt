/**
 * Created by Michael Avoyan on 19/10/2022.
 */

package io.velocitycareerlabs.infrastructure.db

import android.content.Context
import android.content.SharedPreferences
import io.velocitycareerlabs.impl.data.infrastructure.db.CacheServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.db.CacheServiceImpl.Companion.KEY_CACHE_SEQUENCE_COUNTRIES
import io.velocitycareerlabs.impl.data.infrastructure.db.CacheServiceImpl.Companion.KEY_CACHE_SEQUENCE_CREDENTIAL_TYPES
import io.velocitycareerlabs.impl.data.infrastructure.db.CacheServiceImpl.Companion.KEY_CACHE_SEQUENCE_CREDENTIAL_TYPE_SCHEMA
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.extensions.encodeToBase64
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner::class)
internal class CacheServiceTest {
    lateinit var subject: CacheService

    @Mock
    lateinit var  context: Context
    @Mock
    lateinit var  sharedPreferences: SharedPreferences
    @Mock
    lateinit var  sharedPreferencesEditor: SharedPreferences.Editor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        Mockito.`when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        Mockito.`when`(context.getSharedPreferences(anyString(), anyInt()).edit()).thenReturn(sharedPreferencesEditor)
        subject = CacheServiceImpl(context)
    }

    @Test
    fun testGetCountries() {
        Mockito.`when`(sharedPreferences.getString("key", null)).thenReturn("country".encodeToBase64())
        assertEquals(subject.getCountries("key"), "country")

        Mockito.`when`(sharedPreferences.getInt(KEY_CACHE_SEQUENCE_COUNTRIES, 0)).thenReturn(1)
        assertFalse(subject.isResetCacheCountries(0))
        assertFalse(subject.isResetCacheCountries(1))
        assertTrue(subject.isResetCacheCountries(2))
    }

    @Test
    fun testGetCredentialTypes() {
        Mockito.`when`(sharedPreferences.getString("key", null)).thenReturn("credential types".encodeToBase64())
        assertEquals(subject.getCredentialTypes("key"), "credential types")

        Mockito.`when`(sharedPreferences.getInt(KEY_CACHE_SEQUENCE_CREDENTIAL_TYPES, 0)).thenReturn(1)
        assertFalse(subject.isResetCacheCredentialTypes(0))
        assertFalse(subject.isResetCacheCredentialTypes(1))
        assertTrue(subject.isResetCacheCredentialTypes(2))
    }

    @Test
    fun testGetCredentialTypeSchema() {
        Mockito.`when`(sharedPreferences.getString("key", null)).thenReturn("credential type schemas".encodeToBase64())
        assertEquals(subject.getCredentialTypeSchema("key"), "credential type schemas")

        Mockito.`when`(sharedPreferences.getInt(KEY_CACHE_SEQUENCE_CREDENTIAL_TYPE_SCHEMA, 0)).thenReturn(1)
        assertFalse(subject.isResetCacheCredentialTypeSchema(0))
        assertFalse(subject.isResetCacheCredentialTypeSchema(1))
        assertTrue(subject.isResetCacheCredentialTypeSchema(2))
    }

    @After
    fun tearDown() {
    }
}