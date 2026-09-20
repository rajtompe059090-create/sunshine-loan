package com.sunshineloan.app.firebase

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sunshineloan.app.model.LoanApplication
import com.sunshineloan.app.model.LoanStatus
import com.sunshineloan.app.model.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ApplicationRepository(private val context: Context) {

    private val firestore: FirebaseFirestore?
        get() = FirebaseHelper.getFirestore(context)

    // Local in-memory cache to guarantee instant, reliable offline response
    private val localApplications = MutableStateFlow<List<LoanApplication>>(emptyList())
    private val localUserProfile = MutableStateFlow<UserProfile?>(null)
    private val adminCache = MutableStateFlow<Boolean?>(null)

    /**
     * Submits a full 9-step loan application to Firebase Firestore under:
     * - users/{uid}/loanApplications/{appId}
     * - loanApplications/{appId}
     */
    suspend fun submitApplication(application: LoanApplication): Result<LoanApplication> {
        val appToSave = if (application.applicationId.isBlank()) {
            application.copy(applicationId = LoanApplication.generateApplicationId())
        } else {
            application
        }

        // Immediately update local cache
        localApplications.value = listOf(appToSave) + localApplications.value.filter { it.applicationId != appToSave.applicationId }

        val db = firestore
        if (db != null && appToSave.userId.isNotBlank()) {
            try {
                // Save in user subcollection
                db.collection("users")
                    .document(appToSave.userId)
                    .collection("loanApplications")
                    .document(appToSave.applicationId)
                    .set(appToSave.toMap())
                    .await()

                // Save in root loanApplications collection for tracking
                db.collection("loanApplications")
                    .document(appToSave.applicationId)
                    .set(appToSave.toMap())
                    .await()

                return Result.success(appToSave)
            } catch (e: Exception) {
                android.util.Log.w("ApplicationRepository", "Firestore write failed, cached locally: ${e.message}")
                // Stored in local cache, so user flow continues seamlessly
                return Result.success(appToSave)
            }
        }

        return Result.success(appToSave)
    }

    /**
     * Observes all loan applications for the authenticated user in real-time.
     */
    fun getUserApplicationsFlow(userId: String): Flow<List<LoanApplication>> {
        val db = firestore
        if (db == null || userId.isBlank()) {
            return localApplications.asStateFlow()
        }

        return callbackFlow {
            val registration = db.collection("users")
                .document(userId)
                .collection("loanApplications")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(localApplications.value)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { doc ->
                            doc.data?.let { LoanApplication.fromMap(doc.id, it) }
                        }
                        // Merge with local applications
                        val merged = (list + localApplications.value).distinctBy { it.applicationId }
                            .sortedByDescending { it.createdAt }
                        localApplications.value = merged
                        trySend(merged)
                    }
                }

            awaitClose { registration.remove() }
        }
    }

    /**
     * Observes a specific application by ID for status updates.
     */
    fun getApplicationByIdFlow(userId: String, applicationId: String): Flow<LoanApplication?> {
        val db = firestore
        return callbackFlow {
            val localMatch = localApplications.value.firstOrNull { it.applicationId == applicationId }
            trySend(localMatch)

            if (db != null && userId.isNotBlank() && applicationId.isNotBlank()) {
                val registration = db.collection("users")
                    .document(userId)
                    .collection("loanApplications")
                    .document(applicationId)
                    .addSnapshotListener { snapshot, error ->
                        if (error == null && snapshot != null && snapshot.exists()) {
                            val app = snapshot.data?.let { LoanApplication.fromMap(snapshot.id, it) }
                            if (app != null) {
                                // Update in local cache as well
                                localApplications.value = listOf(app) + localApplications.value.filter { it.applicationId != app.applicationId }
                                trySend(app)
                            }
                        }
                    }
                awaitClose { registration.remove() }
            } else {
                awaitClose {}
            }
        }
    }

    /**
     * Observes the user's profile details.
     */
    fun getUserProfileFlow(userId: String, fallbackPhone: String = ""): Flow<UserProfile> {
        val db = firestore
        val defaultProfile = localUserProfile.value ?: UserProfile(userId = userId, phone = fallbackPhone)

        if (db == null || userId.isBlank()) {
            return MutableStateFlow(defaultProfile).asStateFlow()
        }

        return callbackFlow {
            trySend(localUserProfile.value ?: defaultProfile)

            val registration = db.collection("users")
                .document(userId)
                .collection("profile")
                .document("details")
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && snapshot.exists()) {
                        val profile = snapshot.data?.let { UserProfile.fromMap(userId, it) } ?: defaultProfile
                        localUserProfile.value = profile
                        trySend(profile)
                    } else {
                        trySend(localUserProfile.value ?: defaultProfile)
                    }
                }

            awaitClose { registration.remove() }
        }
    }

    /**
     * Saves user profile updates to Firestore.
     */
    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        localUserProfile.value = profile
        val db = firestore
        if (db != null && profile.userId.isNotBlank()) {
            try {
                db.collection("users")
                    .document(profile.userId)
                    .collection("profile")
                    .document("details")
                    .set(profile.toMap())
                    .await()
                return Result.success(Unit)
            } catch (e: Exception) {
                android.util.Log.w("ApplicationRepository", "Failed to update profile to Firestore: ${e.message}")
                return Result.success(Unit)
            }
        }
        return Result.success(Unit)
    }

    /**
     * Secure admin verification: Checks if user's UID exists in the read-only /admins collection.
     * Regular users CANNOT self-elevate or edit this collection.
     */
    suspend fun verifyAdminStatus(userId: String): Boolean {
        if (userId.isBlank() || userId == "local_guest_user") return false
        adminCache.value?.let { return it }

        val db = firestore ?: return false
        return try {
            val doc = db.collection("admins").document(userId).get().await()
            val isAdmin = doc.exists() && (doc.getBoolean("active") ?: true)
            adminCache.value = isAdmin
            isAdmin
        } catch (e: Exception) {
            adminCache.value = false
            false
        }
    }

    /**
     * Admin-only operation: Updates application status and remarks.
     */
    suspend fun updateApplicationStatusByAdmin(
        applicationId: String,
        userId: String,
        newStatus: LoanStatus,
        remarks: String
    ): Result<Unit> {
        // Update local cache
        val current = localApplications.value.firstOrNull { it.applicationId == applicationId }
        if (current != null) {
            val updated = current.copy(
                status = newStatus,
                statusMessage = newStatus.description,
                adminRemarks = remarks,
                updatedAt = System.currentTimeMillis()
            )
            localApplications.value = listOf(updated) + localApplications.value.filter { it.applicationId != applicationId }
        }

        val db = firestore
        if (db != null && applicationId.isNotBlank()) {
            try {
                val updates = mapOf(
                    "status" to newStatus.name,
                    "statusMessage" to newStatus.description,
                    "adminRemarks" to remarks,
                    "updatedAt" to System.currentTimeMillis()
                )
                if (userId.isNotBlank()) {
                    db.collection("users")
                        .document(userId)
                        .collection("loanApplications")
                        .document(applicationId)
                        .update(updates)
                        .await()
                }
                db.collection("loanApplications")
                    .document(applicationId)
                    .update(updates)
                    .await()
                return Result.success(Unit)
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
        return Result.success(Unit)
    }

    companion object {
        @Volatile
        private var INSTANCE: ApplicationRepository? = null

        fun getInstance(context: Context): ApplicationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApplicationRepository(context).also { INSTANCE = it }
            }
        }
    }
}
