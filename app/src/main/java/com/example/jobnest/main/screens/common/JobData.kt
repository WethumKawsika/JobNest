package com.example.jobnest.main.screens.common

import java.util.UUID

data class Job(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val company: String,
    val location: String,
    val salary: String,
    val workType: String,
    val food: String,
    val transport: String,
    val workTime: String,
    val requiredPersons: String,
    val ageLimit: String? = null,
    val isBookmarked: Boolean = false
)

object JobData {
    val jobs = listOf(
        Job(
            title = "Android Developer",
            description = "We are looking for an experienced Android developer to join our team.",
            company = "Google",
            location = "Mountain View, CA",
            salary = "Rs. 5000–8000/day",
            workType = "Full-time",
            food = "Lunch & Snacks provided",
            transport = "Shuttle provided",
            workTime = "Morning (9 AM - 5 PM)",
            requiredPersons = "Male/Female, 2",
            ageLimit = "22-35"
        ),
        Job(
            title = "Promotions Assistant",
            description = "Help promote our new product line at local malls.",
            company = "Marketing Pros",
            location = "Colombo, SL",
            salary = "Rs. 1500–2500/day",
            workType = "Promotion",
            food = "Dinner provided",
            transport = "Not provided",
            workTime = "Evening (4 PM - 10 PM)",
            requiredPersons = "Female, 5",
            ageLimit = "18-28"
        ),
        Job(
            title = "Home Tutor",
            description = "Grade 10 Mathematics tuition for a small group.",
            company = "Private Hire",
            location = "Kandy, SL",
            salary = "Rs. 3000/session",
            workType = "Tuition",
            food = "Tea & Snacks provided",
            transport = "Provided",
            workTime = "Night (6 PM - 8 PM)",
            requiredPersons = "Male/Female, 1"
        )
    )
}