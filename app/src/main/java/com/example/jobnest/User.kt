package com.example.jobnest

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val university: String = "",
    val userType: String = "student"
)