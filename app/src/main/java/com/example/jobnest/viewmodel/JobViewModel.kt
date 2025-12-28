package com.example.jobnest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobnest.data.Job
import com.example.jobnest.repository.AuthRepository
import com.example.jobnest.repository.JobRepository
import com.example.jobnest.repository.SavedJobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class JobState(
    val isLoading: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val savedJobIds: Set<String> = emptySet(),
    val error: String? = null
)

class JobViewModel : ViewModel() {
    private val jobRepository = JobRepository()
    private val authRepository = AuthRepository()
    private val savedJobRepository = SavedJobRepository()
    
    private val _jobState = MutableStateFlow(JobState())
    val jobState: StateFlow<JobState> = _jobState.asStateFlow()
    
    init {
        loadJobs()
        loadSavedJobs()
    }
    
    fun loadJobs() {
        viewModelScope.launch {
            _jobState.value = _jobState.value.copy(isLoading = true, error = null)
            val result = jobRepository.getAllJobs()
            result.onSuccess { jobs ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    jobs = jobs
                )
            }.onFailure { exception ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to load jobs"
                )
            }
        }
    }
    
    fun createJob(job: Job) {
        viewModelScope.launch {
            _jobState.value = _jobState.value.copy(isLoading = true, error = null)
            val userId = authRepository.currentUserId ?: run {
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = "User not authenticated"
                )
                return@launch
            }
            
            val userData = authRepository.getCurrentUserData().getOrNull()
            val jobWithOwner = job.copy(
                ownerId = userId,
                ownerName = userData?.fullName ?: "",
                ownerEmail = userData?.email ?: "",
                ownerPhone = userData?.phone ?: ""
            )
            
            val result = jobRepository.createJob(jobWithOwner)
            result.onSuccess {
                loadJobs()
            }.onFailure { exception ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to create job"
                )
            }
        }
    }
    
    fun updateJob(jobId: String, job: Job) {
        viewModelScope.launch {
            _jobState.value = _jobState.value.copy(isLoading = true, error = null)
            val result = jobRepository.updateJob(jobId, job)
            result.onSuccess {
                loadJobs()
            }.onFailure { exception ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to update job"
                )
            }
        }
    }
    
    fun deleteJob(jobId: String) {
        viewModelScope.launch {
            _jobState.value = _jobState.value.copy(isLoading = true, error = null)
            val result = jobRepository.deleteJob(jobId)
            result.onSuccess {
                loadJobs()
            }.onFailure { exception ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to delete job"
                )
            }
        }
    }
    
    fun searchJobs(query: String) {
        viewModelScope.launch {
            _jobState.value = _jobState.value.copy(isLoading = true, error = null)
            val result = if (query.isBlank()) {
                jobRepository.getAllJobs()
            } else {
                jobRepository.searchJobs(query)
            }
            result.onSuccess { jobs ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    jobs = jobs
                )
            }.onFailure { exception ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Search failed"
                )
            }
        }
    }
    
    fun filterJobs(workType: String? = null, workTime: String? = null, location: String? = null, minSalary: Int? = null) {
        viewModelScope.launch {
            _jobState.value = _jobState.value.copy(isLoading = true, error = null)
            val result = jobRepository.filterJobs(workType, workTime, location, minSalary)
            result.onSuccess { jobs ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    jobs = jobs
                )
            }.onFailure { exception ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Filter failed"
                )
            }
        }
    }
    
    fun getJobsByOwner(ownerId: String) {
        viewModelScope.launch {
            _jobState.value = _jobState.value.copy(isLoading = true, error = null)
            val result = jobRepository.getJobsByOwner(ownerId)
            result.onSuccess { jobs ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    jobs = jobs
                )
            }.onFailure { exception ->
                _jobState.value = _jobState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to load jobs"
                )
            }
        }
    }
    
    private fun loadSavedJobs() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val result = savedJobRepository.getSavedJobIds(userId)
            result.onSuccess { jobIds ->
                _jobState.value = _jobState.value.copy(
                    savedJobIds = jobIds.toSet()
                )
            }
        }
    }
    
    fun toggleSaveJob(jobId: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val isSaved = _jobState.value.savedJobIds.contains(jobId)
            
            val result = if (isSaved) {
                savedJobRepository.unsaveJob(userId, jobId)
            } else {
                savedJobRepository.saveJob(userId, jobId)
            }
            
            result.onSuccess {
                loadSavedJobs()
            }
        }
    }
    
    fun clearError() {
        _jobState.value = _jobState.value.copy(error = null)
    }
}

