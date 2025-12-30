package com.example.jobnest.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Job(
    val jobId: String = "",
    val title: String = "",
    val description: String = "",
    val company: String = "",
    val contactNumber: String = "",
    val location: String = "",
    val minSalary: Int = 0,
    val maxSalary: Int = 0,
    val workType: String = "",
    val food: String = "",
    val transport: String = "",
    val workTime: String = "",
    val requiredPersons: Int = 1,
    val genderPreference: String = "Any",
    val ageLimit: String? = null,

    // Owner information
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerEmail: String = "",
    val ownerPhone: String = "",

    // Metadata - Handle both Long and Timestamp from Firestore
    @PropertyName("createdAt")
    val createdAt: Any? = null, // Can be Long or Timestamp

    @PropertyName("updatedAt")
    val updatedAt: Any? = null // Can be Long or Timestamp
) {
    // Helper functions to get timestamps as Long
    fun getCreatedAtLong(): Long {
        return when (createdAt) {
            is Long -> createdAt
            is Timestamp -> createdAt.toDate().time
            else -> System.currentTimeMillis() // Fallback to current time
        }
    }

    fun getUpdatedAtLong(): Long {
        return when (updatedAt) {
            is Long -> updatedAt
            is Timestamp -> updatedAt.toDate().time
            else -> getCreatedAtLong() // Fallback to createdAt
        }
    }
}