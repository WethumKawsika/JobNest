package com.example.jobnest.data

data class User(
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val userType: String = "Student", // "Student" or "JobOwner"
    val phone: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

