package com.example.jobnest.main.screens.common

import androidx.compose.runtime.mutableStateListOf

object JobData {
    val jobs = mutableStateListOf(
        Job("Part-Time Waiter", "Rs. 1800/day", "Colombo 03", "Food Service",
            "Looking for a friendly waiter for evening shifts at a busy restaurant.", isSaved = false
        ),
        Job("Delivery Driver", "Rs. 1200/day", "Galle", "Delivery",
            "Morning shifts, must have own vehicle.", isSaved = true
        ),
        Job("Data Entry Operator", "Rs. 1000/day", "Kandy", "Office Work",
            "Fast typing skills required.", isSaved = false
        ),
        Job("Cashier", "Rs. 90/day", "Nugegoda", "Retail",
            "Weekend shifts available at local supermarket.", isSaved = false
        )
    )
}
