package com.example.jobnest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobnest.data.Job
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class JobState(
    val jobs: List<Job> = emptyList(),
    val savedJobs: List<Job> = emptyList(),
    val savedJobIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class JobViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _jobState = MutableStateFlow(JobState())
    val jobState: StateFlow<JobState> = _jobState

    private var jobsListener: ListenerRegistration? = null
    private var myJobsListener: ListenerRegistration? = null

    fun loadJobs() {
        _jobState.value = _jobState.value.copy(isLoading = true, error = null)
        jobsListener?.remove()
        jobsListener = db.collection("jobs")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _jobState.value = _jobState.value.copy(
                        isLoading = false,
                        error = "Failed to load jobs: ${e.message}"
                    )
                    return@addSnapshotListener
                }

                val jobs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Job::class.java)?.copy(jobId = doc.id)
                }?.sortedByDescending { it.getCreatedAtLong() } ?: emptyList()

                _jobState.value = _jobState.value.copy(
                    jobs = jobs,
                    isLoading = false
                )
                loadSavedJobIds()
            }
    }

    fun loadMyJobs() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _jobState.value = _jobState.value.copy(
                error = "User not logged in",
                isLoading = false
            )
            return
        }

        _jobState.value = _jobState.value.copy(isLoading = true, error = null)
        myJobsListener?.remove()
        myJobsListener = db.collection("jobs")
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _jobState.value = _jobState.value.copy(
                        isLoading = false,
                        error = "Failed to load your jobs: ${e.message}"
                    )
                    return@addSnapshotListener
                }

                val jobs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Job::class.java)?.copy(jobId = doc.id)
                }?.sortedByDescending { it.getCreatedAtLong() } ?: emptyList()

                _jobState.value = _jobState.value.copy(
                    jobs = jobs,
                    isLoading = false
                )
            }
    }

    fun createJob(job: Job, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _jobState.value = _jobState.value.copy(error = "User not logged in")
                return@launch
            }

            _jobState.value = _jobState.value.copy(isLoading = true, error = null)
            try {
                val jobId = db.collection("jobs").document().id

                val jobData = hashMapOf(
                    "jobId" to jobId,
                    "title" to job.title,
                    "description" to job.description,
                    "company" to job.company,
                    "contactNumber" to job.contactNumber,
                    "location" to job.location,
                    "locationLat" to job.locationLat,
                    "locationLng" to job.locationLng,
                    "minSalary" to job.minSalary,
                    "maxSalary" to job.maxSalary,
                    "workType" to job.workType,
                    "food" to job.food,
                    "transport" to job.transport,
                    "workTime" to job.workTime,
                    "requiredPersons" to job.requiredPersons,
                    "genderPreference" to job.genderPreference,
                    "ageLimit" to job.ageLimit,
                    "ownerId" to userId,
                    "ownerName" to job.ownerName,
                    "ownerEmail" to auth.currentUser?.email.orEmpty(),
                    "ownerPhone" to job.ownerPhone,
                    "createdAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )

                db.collection("jobs")
                    .document(jobId)
                    .set(jobData)
                    .await()

                _jobState.value = _jobState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = "Failed to post job: ${e.message}"
                )
            }
        }
    }

    fun searchJobs(query: String) {
        if (query.isBlank()) {
            loadJobs()
            return
        }

        jobsListener?.remove()
        viewModelScope.launch {
            _jobState.value = _jobState.value.copy(isLoading = true, error = null)
            try {
                val snapshot = db.collection("jobs")
                    .get()
                    .await()

                val allJobs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Job::class.java)?.copy(jobId = doc.id)
                }

                val filteredJobs = allJobs.filter { job ->
                    job.title.contains(query, ignoreCase = true) ||
                            job.company.contains(query, ignoreCase = true) ||
                            job.location.contains(query, ignoreCase = true) ||
                            job.workType.contains(query, ignoreCase = true)
                }.sortedByDescending { it.getCreatedAtLong() }

                _jobState.value = _jobState.value.copy(
                    jobs = filteredJobs,
                    isLoading = false
                )
                loadSavedJobIds()
            } catch (e: Exception) {
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = "Failed to search jobs: ${e.message}"
                )
            }
        }
    }

    fun toggleBookmark(jobId: String) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            try {
                val bookmarkRef = db.collection("users")
                    .document(userId)
                    .collection("bookmarks")
                    .document(jobId)

                val doc = bookmarkRef.get().await()
                if (doc.exists()) {
                    bookmarkRef.delete().await()
                } else {
                    bookmarkRef.set(mapOf("createdAt" to System.currentTimeMillis())).await()
                }
                loadSavedJobIds()
                if (_jobState.value.savedJobs.any { it.jobId == jobId }) {
                    loadSavedJobs()
                }
            } catch (e: Exception) {
                _jobState.value = _jobState.value.copy(
                    error = "Failed to update bookmark: ${e.message}"
                )
            }
        }
    }

    fun loadSavedJobIds() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            try {
                val snapshot = db.collection("users")
                    .document(userId)
                    .collection("bookmarks")
                    .get()
                    .await()

                val savedIds = snapshot.documents.map { it.id }.toSet()
                _jobState.value = _jobState.value.copy(savedJobIds = savedIds)
            } catch (e: Exception) {
                // Silently fail for bookmarks
            }
        }
    }

    fun loadSavedJobs() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _jobState.value = _jobState.value.copy(error = "User not logged in", isLoading = false)
                return@launch
            }

            _jobState.value = _jobState.value.copy(isLoading = true, error = null)
            try {
                val bookmarksSnapshot = db.collection("users")
                    .document(userId)
                    .collection("bookmarks")
                    .get()
                    .await()

                val jobIds = bookmarksSnapshot.documents.map { it.id }

                if (jobIds.isEmpty()) {
                    _jobState.value = _jobState.value.copy(savedJobs = emptyList(), isLoading = false)
                    return@launch
                }

                val savedJobsList = mutableListOf<Job>()
                jobIds.chunked(10).forEach { batch ->
                    val jobsSnapshot = db.collection("jobs")
                        .whereIn("jobId", batch)
                        .get()
                        .await()
                    jobsSnapshot.documents.forEach { doc ->
                        doc.toObject(Job::class.java)?.let { savedJobsList.add(it) }
                    }
                }

                _jobState.value = _jobState.value.copy(
                    savedJobs = savedJobsList.sortedByDescending { it.getCreatedAtLong() },
                    isLoading = false
                )

            } catch (e: Exception) {
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = "Failed to load saved jobs: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _jobState.value = _jobState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        jobsListener?.remove()
        myJobsListener?.remove()
    }
}