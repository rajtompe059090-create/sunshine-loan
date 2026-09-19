package com.sunshineloan.app.firebase

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sunshineloan.app.model.LoanRecord
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class LoanRepository(private val context: Context) {

    private val firestore: FirebaseFirestore?
        get() = FirebaseHelper.getFirestore(context)
    
    // In-memory cache & fallback for offline/pre-configured states
    private val localFallbackRecords = MutableStateFlow<List<LoanRecord>>(emptyList())

    /**
     * Saves a loan calculation record under users/{uid}/loanRecords/{recordId}
     */
    suspend fun saveLoanRecord(userId: String, record: LoanRecord): Result<LoanRecord> {
        val recordId = if (record.id.isNotBlank()) record.id else UUID.randomUUID().toString()
        val recordToSave = record.copy(id = recordId, userId = userId)

        // Always update local fallback
        localFallbackRecords.value = listOf(recordToSave) + localFallbackRecords.value.filter { it.id != recordId }

        val currentFirestore = firestore
        if (currentFirestore != null && userId.isNotBlank()) {
            return try {
                currentFirestore.collection("users")
                    .document(userId)
                    .collection("loanRecords")
                    .document(recordId)
                    .set(recordToSave.toMap())
                    .await()
                Result.success(recordToSave)
            } catch (e: Exception) {
                // If Firestore throws error (e.g. offline, security rule, or network), local cache still holds it
                Result.success(recordToSave)
            }
        }
        return Result.success(recordToSave)
    }

    /**
     * Observes real-time loan records for the specified user ID.
     */
    fun getLoanRecordsFlow(userId: String): Flow<List<LoanRecord>> {
        val currentFirestore = firestore
        if (currentFirestore == null || userId.isBlank()) {
            return localFallbackRecords.asStateFlow()
        }

        return callbackFlow {
            val registration = currentFirestore.collection("users")
                .document(userId)
                .collection("loanRecords")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(localFallbackRecords.value)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val records = snapshot.documents.mapNotNull { doc ->
                            doc.data?.let { LoanRecord.fromMap(doc.id, it) }
                        }
                        localFallbackRecords.value = records
                        trySend(records)
                    }
                }

            awaitClose {
                registration.remove()
            }
        }
    }

    /**
     * Deletes a loan record from users/{uid}/loanRecords/{recordId}
     */
    suspend fun deleteLoanRecord(userId: String, recordId: String): Result<Unit> {
        // Remove from local fallback
        localFallbackRecords.value = localFallbackRecords.value.filter { it.id != recordId }

        val currentFirestore = firestore
        if (currentFirestore != null && userId.isNotBlank()) {
            return try {
                currentFirestore.collection("users")
                    .document(userId)
                    .collection("loanRecords")
                    .document(recordId)
                    .delete()
                    .await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.success(Unit)
            }
        }
        return Result.success(Unit)
    }

    companion object {
        @Volatile
        private var INSTANCE: LoanRepository? = null

        fun getInstance(context: Context): LoanRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LoanRepository(context).also { INSTANCE = it }
            }
        }
    }
}
