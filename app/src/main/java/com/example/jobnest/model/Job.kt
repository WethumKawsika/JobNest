package com.example.jobnest.model

data class Job(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val salary: String = "",
    val location: String = "",
    val workType: String = "",
    val workTime: String = "",
    val food: String = "",
    val ownerId: String = "",
    val transport: String = "",
    val requiredPersons: String = "",
    val ageLimit: String? = null, // Nullable as it's optional in your Composable
    val description: String = "",
    val isBookmarked: Boolean = false
)
    