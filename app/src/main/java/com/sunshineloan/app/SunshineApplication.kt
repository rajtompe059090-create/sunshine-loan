package com.sunshineloan.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.sunshineloan.app.firebase.FirebaseHelper

class SunshineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("SunshineApp", "SunshineApplication onCreate: Initializing Firebase...")
        try {
            val configured = FirebaseHelper.ensureInitialized(this)
            val appCount = FirebaseApp.getApps(this).size
            Log.i("SunshineApp", "Firebase initialization complete. Configured: $configured (active apps: $appCount)")
        } catch (e: Throwable) {
            Log.e("SunshineApp", "Failed during Firebase startup initialization: ${e.message}", e)
        }
    }
}
