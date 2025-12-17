package com.example.jobnest.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobnest.main.screens.common.Job
import com.example.jobnest.main.screens.common.JobListItem

@Composable
fun HomeScreen(onSwitchView: () -> Unit = {}) {

    var searchQuery by remember { mutableStateOf("") }

    val jobs = remember {
        mutableStateListOf(
            Job("Part-Time Waiter", "Rs. 800/day", "Colombo 03", "Food Service",
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. THE BLUE HEADER (Top Section)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column {
                // Top Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Welcome back!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    TextButton(
                        onClick = onSwitchView,
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                    ) {
                        Text("Switch to Owner", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Hero Text
                Text(
                    text = "Find Your Job",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Discover amazing part-time opportunities.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }

        // 2. CONTENT AREA (Directly below header, no overlap)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search for jobs, roles...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            // Filter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Available Jobs",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${jobs.size} opportunities found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                IconButton(
                    onClick = { /* FILTER */ },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Filter results")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Job List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(jobs) { job ->
                    JobListItem(job = job, onBookmarkClick = { updatedJob ->
                        val index = jobs.indexOfFirst { it.title == updatedJob.title }
                        if (index != -1) {
                            jobs[index] = updatedJob
                        }
                    })
                }
            }
        }
    }
}