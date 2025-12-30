package com.example.jobnest.data

data class User(
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "", // Changed from 'phone' to match AuthViewModel
    val address: String = "", // Added to match AuthViewModel
    val userType: String = "Student", // "Student" or "owner"
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)