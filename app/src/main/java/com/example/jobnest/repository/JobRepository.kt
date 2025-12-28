package com.example.jobnest.repository

import com.example.jobnest.data.Job
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class JobRepository {
    private val db = FirebaseFirestore.getInstance()
    private val jobsCollection = db.collection("jobs")
    
    suspend fun createJob(job: Job): Result<String> {
        return try {
            val docRef = jobsCollection.add(job).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateJob(jobId: String, job: Job): Result<Unit> {
        return try {
            jobsCollection.document(jobId).set(job.copy(jobId = jobId)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteJob(jobId: String): Result<Unit> {
        return try {
            jobsCollection.document(jobId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getJob(jobId: String): Result<Job?> {
        return try {
            val document = jobsCollection.document(jobId).get().await()
            val job = document.toObject(Job::class.java)?.copy(jobId = document.id)
            Result.success(job)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllJobs(): Result<List<Job>> {
        return try {
            // Get all active jobs, then sort in memory to avoid index requirement
            val snapshot = jobsCollection
                .whereEqualTo("isActive", true)
                .get()
                .await()
            
            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(jobId = doc.id)
            }.sortedByDescending { it.createdAt?.time ?: 0L }
            
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getJobsByOwner(ownerId: String): Result<List<Job>> {
        return try {
            // Get jobs by owner, then sort in memory to avoid index requirement
            val snapshot = jobsCollection
                .whereEqualTo("ownerId", ownerId)
                .get()
                .await()
            
            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(jobId = doc.id)
            }.sortedByDescending { it.createdAt?.time ?: 0L }
            
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchJobs(query: String): Result<List<Job>> {
        return try {
            // Get all active jobs, filter by search query, then sort
            val snapshot = jobsCollection
                .whereEqualTo("isActive", true)
                .get()
                .await()
            
            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(jobId = doc.id)
            }.filter { job ->
                job.title.contains(query, ignoreCase = true) ||
                job.description.contains(query, ignoreCase = true) ||
                job.location.contains(query, ignoreCase = true) ||
                job.company.contains(query, ignoreCase = true)
            }.sortedByDescending { it.createdAt?.time ?: 0L }
            
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun filterJobs(
        workType: String? = null,
        workTime: String? = null,
        location: String? = null,
        minSalary: Int? = null
    ): Result<List<Job>> {
        return try {
            // Get all active jobs first (Firestore has limitations on multiple where clauses)
            val snapshot = jobsCollection
                .whereEqualTo("isActive", true)
                .get()
                .await()
            
            var jobs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(jobId = doc.id)
            }
            
            // Apply filters in memory
            workType?.let { jobs = jobs.filter { it.workType == workType } }
            workTime?.let { jobs = jobs.filter { it.workTime == workTime } }
            location?.let { jobs = jobs.filter { it.location.contains(location, ignoreCase = true) } }
            minSalary?.let { jobs = jobs.filter { it.minSalary >= minSalary } }
            
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

