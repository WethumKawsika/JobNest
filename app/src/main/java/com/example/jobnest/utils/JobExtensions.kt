package com.example.jobnest.utils

import com.example.jobnest.data.Job
import com.example.jobnest.main.screens.common.Job as UIJob

// Extension function to convert backend Job to UI Job format
fun Job.toUIJob(isBookmarked: Boolean = false): UIJob {
    return UIJob(
        id = jobId,
        title = title,
        description = description,
        company = company,
        location = location,
        salary = salaryDisplay,
        workType = workType,
        food = food,
        transport = transport,
        workTime = workTime,
        requiredPersons = "$genderPreference, $requiredPersons",
        ageLimit = ageLimit,
        isBookmarked = isBookmarked
    )
}

