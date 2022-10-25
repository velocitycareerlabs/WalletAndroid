/**
 * Created by Michael Avoyan on 19/10/2022.
 */

package io.velocitycareerlabs.infrastructure.db

import android.content.Context
import android.content.SharedPreferences
import io.velocitycareerlabs.impl.data.infrastructure.db.CacheServiceImpl
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.extensions.encodeToBase64
import junit.framework.Assert.assertEquals
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
        Mockito.`when`(sharedPreferences.getString("key1", null)).thenReturn("value1".encodeToBase64())
        assertEquals(subject.getCountries("key1"), "value1")
    }

    @Test
    fun testGetCredentialTypes() {
        Mockito.`when`(sharedPreferences.getString("key2", null)).thenReturn("value2".encodeToBase64())
        assertEquals(subject.getCredentialTypes("key2"), "value2")
    }

    @Test
    fun testGetCredentialTypeSchema() {
        Mockito.`when`(sharedPreferences.getString("key3", null)).thenReturn("value3".encodeToBase64())
        assertEquals(subject.getCredentialTypeSchema("key3"), "value3")
    }

    @After
    fun tearDown() {
    }
}