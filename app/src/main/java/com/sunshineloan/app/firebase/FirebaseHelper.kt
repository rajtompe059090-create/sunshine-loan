package com.sunshineloan.app.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Utility for safely accessing Firebase services without crashing if google-services.json
 * is not yet supplied by the developer.
 */
object FirebaseHelper {

    fun isConfigured(context: Context): Boolean {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                // Initialize default FirebaseApp using google-services generated resources if not already done
                FirebaseApp.initializeApp(context)
            }
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }

    fun getAuth(context: Context): FirebaseAuth? {
        return try {
            if (isConfigured(context)) FirebaseAuth.getInstance() else null
        } catch (_: Exception) {
            null
        }
    }

    fun getFirestore(context: Context): FirebaseFirestore? {
        return try {
            if (isConfigured(context)) FirebaseFirestore.getInstance() else null
        } catch (_: Exception) {
            null
        }
    }
}
