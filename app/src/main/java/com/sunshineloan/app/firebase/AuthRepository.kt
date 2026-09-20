package com.sunshineloan.app.firebase

import android.app.Activity
import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class CodeSent(
        val verificationId: String,
        val token: PhoneAuthProvider.ForceResendingToken? = null,
        val phoneNumber: String
    ) : AuthState()
    data class Authenticated(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthRepository(private val context: Context) {

    private val auth: FirebaseAuth?
        get() = FirebaseHelper.getAuth(context)

    private val _authState = MutableStateFlow<AuthState>(
        if (FirebaseHelper.getAuth(context)?.currentUser != null) {
            AuthState.Authenticated(FirebaseHelper.getAuth(context)?.currentUser)
        } else {
            AuthState.Idle
        }
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun isFirebaseConfigured(): Boolean = FirebaseHelper.isConfigured(context)

    fun getCurrentUser(): FirebaseUser? = auth?.currentUser

    fun isUserLoggedIn(): Boolean = auth?.currentUser != null

    private var storedResendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun getUserId(): String {
        return auth?.currentUser?.uid ?: "local_guest_user"
    }

    /**
     * Initiates Firebase Phone Number Verification.
     */
    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken? = null
    ) {
        val currentAuth = auth
        if (currentAuth == null) {
            val ex = FirebaseHelper.getLastInitException()
            val exMsg = ex?.message ?: ex?.javaClass?.simpleName ?: "FirebaseApp initialization failed"
            _authState.value = AuthState.Error(
                "Firebase is not initialized ($exMsg). Please verify that 'app/google-services.json' is present."
            )
            return
        }

        _authState.value = AuthState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Instant SMS auto-retrieval
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                android.util.Log.e("AuthRepository", "Firebase Phone Auth verification failed", e)
                val friendlyMessage = parseFirebaseError(e)
                _authState.value = AuthState.Error(friendlyMessage)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedResendToken = token
                _authState.value = AuthState.CodeSent(
                    verificationId = verificationId,
                    token = token,
                    phoneNumber = phoneNumber
                )
            }
        }

        val builder = PhoneAuthOptions.newBuilder(currentAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        val tokenToUse = resendToken ?: storedResendToken
        if (tokenToUse != null) {
            builder.setForceResendingToken(tokenToUse)
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    /**
     * Parses Firebase Auth exceptions into explicit, user-actionable explanations.
     */
    private fun parseFirebaseError(e: FirebaseException): String {
        val raw = e.localizedMessage ?: e.message ?: "Authentication failed"

        return when {
            raw.contains("not authorized", ignoreCase = true) ||
            raw.contains("app verification", ignoreCase = true) ||
            raw.contains("SHA-1", ignoreCase = true) ||
            raw.contains("Play Integrity", ignoreCase = true) ||
            raw.contains("SafetyNet", ignoreCase = true) -> {
                "Firebase App Verification Failed (SHA-1 Required):\n" +
                "Firebase rejected the OTP request because the debug SHA-1 certificate is missing in the Firebase Console.\n\n" +
                "• Package: com.sunshineloan.app\n" +
                "• Debug SHA-1: 49:15:46:5B:C2:86:21:F7:0D:7B:98:80:64:18:EE:D4:1E:98:F3:4E\n" +
                "• Debug SHA-256: DF:54:F6:05:57:3E:8C:0B:29:D5:B9:06:7C:AC:0C:A4:AD:CF:A7:67:B7:64:3F:1F:6A:93:3F:F3:55:3B:B2:F1\n\n" +
                "Add this SHA-1 in Firebase Console (Project Settings -> Your Apps -> Android -> Add Fingerprint)."
            }
            raw.contains("format", ignoreCase = true) || raw.contains("invalid phone", ignoreCase = true) -> {
                "Invalid Phone Number Format: The phone number was rejected by Firebase. Must be in E.164 format (+91XXXXXXXXXX)."
            }
            raw.contains("blocked all requests", ignoreCase = true) ||
            raw.contains("unusual activity", ignoreCase = true) ||
            raw.contains("Too many requests", ignoreCase = true) -> {
                "Firebase SMS Requests Blocked: Firebase has temporarily throttled SMS requests for this device/project. Please wait a few minutes, or use a Firebase Test Phone Number."
            }
            raw.contains("quota", ignoreCase = true) -> {
                "Firebase SMS Quota Exceeded: The daily SMS limit for your Firebase project has been reached."
            }
            raw.contains("disabled", ignoreCase = true) || raw.contains("sign-in provider is disabled", ignoreCase = true) -> {
                "Phone Auth Disabled: Phone Authentication is not enabled in Firebase Console (Authentication -> Sign-in method -> Phone)."
            }
            raw.contains("network", ignoreCase = true) || raw.contains("timeout", ignoreCase = true) -> {
                "Network Error: Unable to reach Firebase servers. Please verify your internet connection."
            }
            else -> {
                "Firebase Error: $raw"
            }
        }
    }

    /**
     * Verifies the user entered OTP code against the verification ID.
     */
    suspend fun verifyOtp(verificationId: String, code: String): Result<FirebaseUser?> {
        if (auth == null) {
            val err = "Firebase is not configured. Missing google-services.json."
            _authState.value = AuthState.Error(err)
            return Result.failure(IllegalStateException(err))
        }
        if (verificationId.isBlank()) {
            val err = "No active OTP verification session. Please tap 'Get OTP' first."
            _authState.value = AuthState.Error(err)
            return Result.failure(IllegalStateException(err))
        }
        if (code.length != 6) {
            val err = "Please enter the full 6-digit OTP code."
            _authState.value = AuthState.Error(err)
            return Result.failure(IllegalArgumentException(err))
        }

        return try {
            _authState.value = AuthState.Loading
            val currentAuth = auth ?: throw IllegalStateException("Firebase is not initialized.")
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val authResult = currentAuth.signInWithCredential(credential).await()
            _authState.value = AuthState.Authenticated(authResult.user)
            Result.success(authResult.user)
        } catch (e: Exception) {
            val msg = if (e is FirebaseException) {
                if (e.message?.contains("invalid", ignoreCase = true) == true ||
                    e.message?.contains("code", ignoreCase = true) == true) {
                    "Invalid OTP: The 6-digit code entered is incorrect or expired. Please check and re-enter, or tap Resend OTP."
                } else {
                    parseFirebaseError(e)
                }
            } else {
                e.localizedMessage ?: "Failed to verify OTP code."
            }
            _authState.value = AuthState.Error(msg)
            Result.failure(e)
        }
    }

    /**
     * Signs in with already resolved credential (e.g. instant SMS verification).
     */
    fun signInWithCredential(credential: PhoneAuthCredential) {
        val currentAuth = auth ?: return
        currentAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                _authState.value = AuthState.Authenticated(authResult.user)
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.localizedMessage ?: "Authentication failed")
            }
    }

    fun signOut() {
        auth?.signOut()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        val user = auth?.currentUser
        if (user != null) {
            _authState.value = AuthState.Authenticated(user)
        } else {
            _authState.value = AuthState.Idle
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository(context).also { INSTANCE = it }
            }
        }
    }
}
