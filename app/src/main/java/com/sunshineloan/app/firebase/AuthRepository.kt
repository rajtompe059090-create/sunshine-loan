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

    private val auth: FirebaseAuth? = FirebaseHelper.getAuth(context)

    private val _authState = MutableStateFlow<AuthState>(
        if (auth?.currentUser != null) {
            AuthState.Authenticated(auth.currentUser)
        } else {
            AuthState.Idle
        }
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun isFirebaseConfigured(): Boolean = FirebaseHelper.isConfigured(context)

    fun getCurrentUser(): FirebaseUser? = auth?.currentUser

    fun isUserLoggedIn(): Boolean = auth?.currentUser != null

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
        if (auth == null) {
            _authState.value = AuthState.Error(
                "Firebase is not configured. Please add 'app/google-services.json' to enable Firebase Phone Auth."
            )
            return
        }

        _authState.value = AuthState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Instant verification or auto-retrieval
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Verification failed. Please check phone number.")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                _authState.value = AuthState.CodeSent(
                    verificationId = verificationId,
                    token = token,
                    phoneNumber = phoneNumber
                )
            }
        }

        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        if (resendToken != null) {
            builder.setForceResendingToken(resendToken)
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    /**
     * Verifies the user entered OTP code against the verification ID.
     */
    suspend fun verifyOtp(verificationId: String, code: String): Result<FirebaseUser?> {
        if (auth == null) {
            return Result.failure(
                IllegalStateException("Firebase is not configured. Missing google-services.json at app/google-services.json")
            )
        }
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val authResult = auth.signInWithCredential(credential).await()
            _authState.value = AuthState.Authenticated(authResult.user)
            Result.success(authResult.user)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.localizedMessage ?: "Invalid OTP code entered.")
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
        if (auth?.currentUser != null) {
            _authState.value = AuthState.Authenticated(auth.currentUser)
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
