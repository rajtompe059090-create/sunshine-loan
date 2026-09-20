package com.sunshineloan.app.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Utility for safely accessing Firebase services without crashing if google-services.json
 * is not yet supplied by the developer.
 */
object FirebaseHelper {

    private const val TAG = "FirebaseHelper"

    @Volatile
    private var lastInitException: Throwable? = null

    fun getLastInitException(): Throwable? = lastInitException

    fun isConfigured(context: Context): Boolean {
        val appContext = context.applicationContext ?: context
        return try {
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                Log.d(TAG, "No default FirebaseApp found. Attempting FirebaseApp.initializeApp(appContext)...")
                val app = FirebaseApp.initializeApp(appContext)
                if (app == null) {
                    Log.w(TAG, "FirebaseApp.initializeApp(appContext) returned null. Attempting explicit FirebaseOptions...")
                    initializeWithOptions(appContext)
                } else {
                    Log.i(TAG, "Successfully initialized FirebaseApp: ${app.name}")
                    lastInitException = null
                }
            }
            val configured = FirebaseApp.getApps(appContext).isNotEmpty()
            Log.d(TAG, "Firebase configured check: $configured (apps count: ${FirebaseApp.getApps(appContext).size})")
            configured
        } catch (e: Throwable) {
            Log.e(TAG, "Error checking/initializing Firebase: ${e.message}", e)
            lastInitException = e
            // Fallback: try initializing explicitly with resources
            try {
                initializeWithOptions(appContext)
                val configured = FirebaseApp.getApps(appContext).isNotEmpty()
                Log.d(TAG, "Fallback initialization result: $configured")
                configured
            } catch (fallbackEx: Throwable) {
                Log.e(TAG, "Fallback initialization also failed: ${fallbackEx.message}", fallbackEx)
                lastInitException = fallbackEx
                false
            }
        }
    }

    private fun initializeWithOptions(context: Context) {
        try {
            // Read resources injected by google-services gradle plugin
            val resources = context.resources
            val packageName = context.packageName

            fun getResId(name: String, type: String): Int =
                resources.getIdentifier(name, type, packageName)

            val appIdRes = getResId("google_app_id", "string")
            val apiKeyRes = getResId("google_api_key", "string")
            val projectIdRes = getResId("project_id", "string")
            val gcmSenderIdRes = getResId("gcm_defaultSenderId", "string")
            val storageBucketRes = getResId("google_storage_bucket", "string")

            if (appIdRes != 0 && apiKeyRes != 0) {
                val appId = resources.getString(appIdRes)
                val apiKey = resources.getString(apiKeyRes)
                val projectId = if (projectIdRes != 0) resources.getString(projectIdRes) else "sunshine-loan-b8296"
                val gcmSenderId = if (gcmSenderIdRes != 0) resources.getString(gcmSenderIdRes) else "753087265246"
                val storageBucket = if (storageBucketRes != 0) resources.getString(storageBucketRes) else "sunshine-loan-b8296.firebasestorage.app"

                val options = FirebaseOptions.Builder()
                    .setApplicationId(appId)
                    .setApiKey(apiKey)
                    .setProjectId(projectId)
                    .setGcmSenderId(gcmSenderId)
                    .setStorageBucket(storageBucket)
                    .build()

                Log.d(TAG, "Initializing FirebaseApp with explicit FirebaseOptions (projectId=$projectId, appId=$appId)")
                FirebaseApp.initializeApp(context, options)
                lastInitException = null
                Log.i(TAG, "Successfully initialized FirebaseApp with explicit FirebaseOptions.")
            } else {
                Log.e(TAG, "Cannot find google_app_id or google_api_key string resources in package $packageName")
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Exception in initializeWithOptions: ${e.message}", e)
            lastInitException = e
            throw e
        }
    }

    fun getAuth(context: Context): FirebaseAuth? {
        val appContext = context.applicationContext ?: context
        return try {
            if (isConfigured(appContext)) {
                FirebaseAuth.getInstance()
            } else {
                Log.w(TAG, "getAuth called but Firebase is not configured.")
                null
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error getting FirebaseAuth instance: ${e.message}", e)
            lastInitException = e
            null
        }
    }

    fun getFirestore(context: Context): FirebaseFirestore? {
        val appContext = context.applicationContext ?: context
        return try {
            if (isConfigured(appContext)) {
                FirebaseFirestore.getInstance()
            } else {
                Log.w(TAG, "getFirestore called but Firebase is not configured.")
                null
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error getting FirebaseFirestore instance: ${e.message}", e)
            lastInitException = e
            null
        }
    }
}
