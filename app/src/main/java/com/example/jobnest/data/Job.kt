package com.example.jobnest.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Job(
    val jobId: String = "",
    val title: String = "",
    val description: String = "",
    val company: String = "",
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
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerEmail: String = "",
    val ownerPhone: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
    val isActive: Boolean = true
) {
    // Helper property for salary display
    val salaryDisplay: String
        get() = if (maxSalary > 0) {
            "Rs. $minSalary–$maxSalary/day"
        } else {
            "Rs. $minSalary/day"
        }
}

