package com.example.jobnest.utils

import com.example.jobnest.data.Job
import com.example.jobnest.main.screens.common.JobUI

fun Job.toUIJob(isBookmarked: Boolean = false): JobUI {
    return JobUI(
        id = this.jobId,
        title = this.title,
        company = this.company,
        location = this.location,
        salary = if (this.minSalary > 0 && this.maxSalary > 0) {
            "Rs. ${this.minSalary} - ${this.maxSalary}/day"
        } else {
            "Negotiable"
        },
        workType = this.workType,
        workTime = this.workTime,
        food = this.food,
        transport = this.transport,
        description = this.description,
        requiredPersons = this.requiredPersons,
        genderPreference = this.genderPreference,
        ageLimit = this.ageLimit,
        contactNumber = this.contactNumber,
        isBookmarked = isBookmarked,
        postedBy = this.ownerName.takeIf { it.isNotBlank() } ?: "Anonymous",
        postedAt = this.getCreatedAtLong() // Use the helper method instead of direct postedAt
    )
}