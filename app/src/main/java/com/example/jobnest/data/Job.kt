package com.example.jobnest.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.android.gms.maps.model.LatLng

data class Job(
    val jobId: String = "",
    val title: String = "",
    val description: String = "",
    val company: String = "",
    val contactNumber: String = "",
    val location: String = "",
    val locationLat: Double = 0.0,
    val locationLng: Double = 0.0,
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
    // Helper function to get LatLng
    fun getLatLng(): LatLng? {
        return if (locationLat != 0.0 && locationLng != 0.0) {
            LatLng(locationLat, locationLng)
        } else null
    }

    // Helper functions to get timestamps as Long
    fun getCreatedAtLong(): Long {
        return when (createdAt) {
            is Long -> createdAt
            is Timestamp -> createdAt.toDate().time
            else -> System.currentTimeMillis()
        }
    }

    fun getUpdatedAtLong(): Long {
        return when (updatedAt) {
            is Long -> updatedAt
            is Timestamp -> updatedAt.toDate().time
            else -> getCreatedAtLong()
        }
    }
}