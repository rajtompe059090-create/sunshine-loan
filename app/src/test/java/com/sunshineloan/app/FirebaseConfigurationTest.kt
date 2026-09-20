package com.sunshineloan.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.sunshineloan.app.firebase.AuthRepository
import com.sunshineloan.app.firebase.AuthState
import com.sunshineloan.app.firebase.FirebaseHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FirebaseConfigurationTest {

    @Test
    fun testFirebaseProjectConfigurationMatchesGoogleServices() {
        assertEquals("sunshine-loan-b8296", FirebaseHelper.FIREBASE_PROJECT_ID)
        assertEquals("1:753087265246:android:d248f97acdfaa37323c54f", FirebaseHelper.FIREBASE_APPLICATION_ID)
        assertEquals("AIzaSyAfOKdUPRQcnp2UUT1e-Na9LV7IVEvaNIc", FirebaseHelper.FIREBASE_API_KEY)
        assertEquals("753087265246", FirebaseHelper.FIREBASE_GCM_SENDER_ID)
        assertEquals("sunshine-loan-b8296.firebasestorage.app", FirebaseHelper.FIREBASE_STORAGE_BUCKET)
    }

    @Test
    fun testFirebaseHelperInitialization() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val initialized = FirebaseHelper.ensureInitialized(context)
        assertTrue("FirebaseHelper should successfully initialize FirebaseApp", initialized)

        val app = FirebaseApp.getInstance()
        assertNotNull(app)
        assertEquals("sunshine-loan-b8296", app.options.projectId)
        assertEquals("1:753087265246:android:d248f97acdfaa37323c54f", app.options.applicationId)

        val auth = FirebaseHelper.getAuth(context)
        assertNotNull("FirebaseAuth instance should not be null", auth)

        val authRepo = AuthRepository.getInstance(context)
        assertTrue(authRepo.isFirebaseConfigured())
        assertEquals(AuthState.Idle::class, authRepo.authState.value::class)
    }
}
