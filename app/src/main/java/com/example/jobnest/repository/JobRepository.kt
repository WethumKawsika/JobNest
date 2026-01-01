package com.example.jobnest.repository

import com.example.jobnest.data.Job
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class JobRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val jobsCollection = db.collection("jobs")

    // Create a new job post
    suspend fun createJob(job: Job): Result<String> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(Exception("User not logged in"))

            // Generate unique ID for the job
            val jobId = jobsCollection.document().id

            // Create job data map with proper timestamp
            val jobData = hashMapOf(
                "jobId" to jobId,
                "title" to job.title,
                "description" to job.description,
                "company" to job.company,
                "contactNumber" to job.contactNumber,
                "location" to job.location,
                "minSalary" to job.minSalary,
                "maxSalary" to job.maxSalary,
                "workType" to job.workType,
                "food" to job.food,
                "transport" to job.transport,
                "workTime" to job.workTime,
                "requiredPersons" to job.requiredPersons,
                "genderPreference" to job.genderPreference,
                "ageLimit" to job.ageLimit,
                "ownerId" to currentUser.uid,
                "ownerName" to job.ownerName,
                "ownerEmail" to currentUser.email.orEmpty(),
                "ownerPhone" to job.ownerPhone,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )

            // Save to Firestore
            jobsCollection.document(jobId).set(jobData).await()

            Result.success(jobId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateJob(jobId: String, job: Job): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(Exception("User not logged in"))

            // Verify ownership before updating
            val existingJob = getJob(jobId).getOrNull()
            if (existingJob?.ownerId != currentUser.uid) {
                return Result.failure(Exception("Unauthorized: You can only edit your own jobs"))
            }

            // Create update map
            val updateData = hashMapOf(
                "title" to job.title,
                "description" to job.description,
                "company" to job.company,
                "contactNumber" to job.contactNumber,
                "location" to job.location,
                "minSalary" to job.minSalary,
                "maxSalary" to job.maxSalary,
                "workType" to job.workType,
                "food" to job.food,
                "transport" to job.transport,
                "workTime" to job.workTime,
                "requiredPersons" to job.requiredPersons,
                "genderPreference" to job.genderPreference,
                "ageLimit" to job.ageLimit,
                "updatedAt" to System.currentTimeMillis()
            )

            jobsCollection.document(jobId).update(updateData as Map<String, Any>).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteJob(jobId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(Exception("User not logged in"))

            // Verify ownership before deleting
            val existingJob = getJob(jobId).getOrNull()
            if (existingJob?.ownerId != currentUser.uid) {
                return Result.failure(Exception("Unauthorized: You can only delete your own jobs"))
            }

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

    // Get all jobs (for HomeScreen) - NO orderBy to avoid index requirement
    suspend fun getAllJobs(): Result<List<Job>> {
        return try {
            val snapshot = jobsCollection.get().await()

            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(jobId = doc.id)
            }.sortedByDescending { it.getCreatedAtLong() } // Sort in memory

            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get jobs by current logged-in owner (for MyJobsScreen)
    suspend fun getJobsByOwner(ownerId: String): Result<List<Job>> {
        return try {
            val snapshot = jobsCollection
                .whereEqualTo("ownerId", ownerId)
                .get()
                .await()

            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(jobId = doc.id)
            }.sortedByDescending { it.getCreatedAtLong() } // Sort in memory

            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Search jobs - SEARCHES ACROSS ALL USERS' JOBS
    suspend fun searchJobs(query: String): Result<List<Job>> {
        return try {
            val snapshot = jobsCollection.get().await()

            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(jobId = doc.id)
            }.filter { job ->
                job.title.contains(query, ignoreCase = true) ||
                        job.description.contains(query, ignoreCase = true) ||
                        job.location.contains(query, ignoreCase = true) ||
                        job.company.contains(query, ignoreCase = true) ||
                        job.workType.contains(query, ignoreCase = true)
            }.sortedByDescending { it.getCreatedAtLong() } // Sort in memory

            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getJobsByIds(jobIds: List<String>): Result<List<Job>> {
        return try {
            if (jobIds.isEmpty()) {
                return Result.success(emptyList())
            }

            // Firestore has a limit of 10 items for whereIn queries
            val chunks = jobIds.chunked(10)
            val allJobs = mutableListOf<Job>()

            for (chunk in chunks) {
                val snapshot = jobsCollection
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()

                val jobs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Job::class.java)?.copy(jobId = doc.id)
                }
                allJobs.addAll(jobs)
            }

            Result.success(allJobs.sortedByDescending { it.getCreatedAtLong() })
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
            val snapshot = jobsCollection.get().await()

            var jobs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(jobId = doc.id)
            }

            workType?.let { jobs = jobs.filter { it.workType == workType } }
            workTime?.let { jobs = jobs.filter { it.workTime == workTime } }
            location?.let { jobs = jobs.filter { it.location.contains(location, ignoreCase = true) } }
            minSalary?.let { jobs = jobs.filter { it.minSalary >= minSalary } }

            Result.success(jobs.sortedByDescending { it.getCreatedAtLong() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}