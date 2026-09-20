package com.sunshineloan.app

import android.app.Application
import android.util.Log
import com.sunshineloan.app.firebase.FirebaseHelper

class SunshineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("SunshineApp", "SunshineApplication onCreate: Initializing Firebase...")
        try {
            val configured = FirebaseHelper.isConfigured(this)
            Log.i("SunshineApp", "FirebaseHelper initialization status: $configured")
        } catch (e: Throwable) {
            Log.e("SunshineApp", "Failed during Firebase startup initialization: ${e.message}", e)
        }
    }
}
