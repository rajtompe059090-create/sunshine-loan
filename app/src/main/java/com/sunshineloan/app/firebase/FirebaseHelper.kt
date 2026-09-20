package com.sunshineloan.app.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Utility for safely and reliably initializing and accessing Firebase services for Sunshine Loan.
 * Matches project configuration from app/google-services.json (sunshine-loan-b8296).
 */
object FirebaseHelper {

    private const val TAG = "FirebaseHelper"

    // Project credentials directly from app/google-services.json
    const val FIREBASE_PROJECT_ID = "sunshine-loan-b8296"
    const val FIREBASE_APPLICATION_ID = "1:753087265246:android:d248f97acdfaa37323c54f"
    const val FIREBASE_API_KEY = "AIzaSyAfOKdUPRQcnp2UUT1e-Na9LV7IVEvaNIc"
    const val FIREBASE_GCM_SENDER_ID = "753087265246"
    const val FIREBASE_STORAGE_BUCKET = "sunshine-loan-b8296.firebasestorage.app"

    @Volatile
    private var lastInitException: Throwable? = null

    fun getLastInitException(): Throwable? = lastInitException

    @Synchronized
    fun ensureInitialized(context: Context): Boolean {
        val appContext = context.applicationContext ?: context
        try {
            val existingApps = FirebaseApp.getApps(appContext)
            if (existingApps.isNotEmpty()) {
                lastInitException = null
                return true
            }

            // Attempt 1: Standard auto-initialization from google-services resources
            Log.d(TAG, "Attempting standard FirebaseApp.initializeApp(appContext)...")
            var app = try {
                FirebaseApp.initializeApp(appContext)
            } catch (e: Throwable) {
                Log.w(TAG, "Standard FirebaseApp.initializeApp threw: ${e.message}", e)
                null
            }

            // Attempt 2: Explicit FirebaseOptions using google-services credentials
            if (app == null) {
                Log.d(TAG, "Initializing FirebaseApp with explicit FirebaseOptions for project $FIREBASE_PROJECT_ID...")
                val options = buildFirebaseOptions(appContext)
                app = FirebaseApp.initializeApp(appContext, options)
            }

            if (app != null) {
                Log.i(TAG, "FirebaseApp successfully initialized: ${app.name} (project: ${app.options.projectId})")
                lastInitException = null
                return true
            } else {
                val ex = IllegalStateException("FirebaseApp.initializeApp returned null.")
                lastInitException = ex
                Log.e(TAG, "Failed to initialize FirebaseApp.", ex)
                return false
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Critical error during Firebase initialization: ${e.message}", e)
            lastInitException = e
            return false
        }
    }

    private fun buildFirebaseOptions(context: Context): FirebaseOptions {
        val resources = context.resources
        val packageName = context.packageName

        fun getResString(name: String): String? {
            val id = resources.getIdentifier(name, "string", packageName)
            return if (id != 0) {
                try {
                    resources.getString(id)
                } catch (_: Throwable) {
                    null
                }
            } else null
        }

        val appId = getResString("google_app_id") ?: FIREBASE_APPLICATION_ID
        val apiKey = getResString("google_api_key") ?: FIREBASE_API_KEY
        val projectId = getResString("project_id") ?: FIREBASE_PROJECT_ID
        val gcmSenderId = getResString("gcm_defaultSenderId") ?: FIREBASE_GCM_SENDER_ID
        val storageBucket = getResString("google_storage_bucket") ?: FIREBASE_STORAGE_BUCKET

        return FirebaseOptions.Builder()
            .setApplicationId(appId)
            .setApiKey(apiKey)
            .setProjectId(projectId)
            .setGcmSenderId(gcmSenderId)
            .setStorageBucket(storageBucket)
            .build()
    }

    fun isConfigured(context: Context): Boolean {
        return ensureInitialized(context)
    }

    fun getAuth(context: Context): FirebaseAuth? {
        val appContext = context.applicationContext ?: context
        return if (ensureInitialized(appContext)) {
            try {
                FirebaseAuth.getInstance()
            } catch (e: Throwable) {
                Log.e(TAG, "FirebaseAuth.getInstance() failed: ${e.message}", e)
                lastInitException = e
                null
            }
        } else {
            null
        }
    }

    fun getFirestore(context: Context): FirebaseFirestore? {
        val appContext = context.applicationContext ?: context
        return if (ensureInitialized(appContext)) {
            try {
                FirebaseFirestore.getInstance()
            } catch (e: Throwable) {
                Log.e(TAG, "FirebaseFirestore.getInstance() failed: ${e.message}", e)
                lastInitException = e
                null
            }
        } else {
            null
        }
    }
}
