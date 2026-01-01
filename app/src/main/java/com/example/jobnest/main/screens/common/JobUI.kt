package com.example.jobnest.main.screens.common

import com.google.android.gms.maps.model.LatLng

data class JobUI(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val locationLatLng: LatLng?,
    val salary: String,
    val workType: String,
    val workTime: String,
    val food: String,
    val transport: String,
    val description: String,
    val requiredPersons: Int,
    val genderPreference: String,
    val ageLimit: String?,
    val contactNumber: String,
    val isBookmarked: Boolean = false,
    val phoneNumber: String,
    val postedBy: String = "Anonymous",
    val postedAt: Long = System.currentTimeMillis(),
    val boysCount: Int? = null,
    val girlsCount: Int? = null,
    val minSalary: String
)