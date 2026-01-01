package com.example.jobnest.data

data class SavedJob(
    val savedJobId: String = "",
    val userId: String = "",
    val jobId: String = "",
    val savedAt: Long = System.currentTimeMillis()
)

