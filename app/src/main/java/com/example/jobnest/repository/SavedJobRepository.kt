package com.example.jobnest.repository

import com.example.jobnest.data.SavedJob
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SavedJobRepository {
    private val db = FirebaseFirestore.getInstance()
    private val savedJobsCollection = db.collection("savedJobs")
    
    suspend fun saveJob(userId: String, jobId: String): Result<Unit> {
        return try {
            // Check if already saved
            val existing = savedJobsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("jobId", jobId)
                .get()
                .await()
            
            if (existing.isEmpty) {
                val savedJob = SavedJob(
                    userId = userId,
                    jobId = jobId
                )
                savedJobsCollection.add(savedJob).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unsaveJob(userId: String, jobId: String): Result<Unit> {
        return try {
            val snapshot = savedJobsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("jobId", jobId)
                .get()
                .await()
            
            snapshot.documents.forEach { doc ->
                savedJobsCollection.document(doc.id).delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun isJobSaved(userId: String, jobId: String): Result<Boolean> {
        return try {
            val snapshot = savedJobsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("jobId", jobId)
                .get()
                .await()
            Result.success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSavedJobIds(userId: String): Result<List<String>> {
        return try {
            val snapshot = savedJobsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val jobIds = snapshot.documents.mapNotNull { doc ->
                doc.toObject(SavedJob::class.java)?.jobId
            }
            Result.success(jobIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

