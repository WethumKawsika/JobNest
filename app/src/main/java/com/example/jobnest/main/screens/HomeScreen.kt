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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
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
            )
        )
    }
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {

        // HEADER WITH GRADIENT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            colorScheme.primary,
                            colorScheme.secondary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Welcome back!",
                        style = typography.bodyMedium,
                        color = colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                    TextButton(onClick = onSwitchView) {
                        Text("Switch to Owner", color = colorScheme.onPrimary)
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Find Your Job",
                    style = typography.headlineLarge,
                    color = colorScheme.onPrimary
                )
                Text(
                    text = "Discover amazing part-time opportunities",
                    style = typography.bodyLarge,
                    color = colorScheme.onPrimary.copy(alpha = 0.85f)
                )
            }
        }

        // WHITE ROUNDED CARD AREA
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-24).dp)
                .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // SEARCH BAR
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search jobs...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available Jobs",
                        style = typography.titleLarge,
                        color = colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { /* FILTER SCREEN */ },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter results")
                    }
                }

                Text(
                    text = "${jobs.size} opportunities found",
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // JOB LIST
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
}
