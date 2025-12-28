package com.example.jobnest.main.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobnest.main.screens.common.Job
import com.example.jobnest.main.screens.common.JobListItem
import com.example.jobnest.utils.toUIJob
import com.example.jobnest.viewmodel.JobViewModel

// Premium Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val SoftBackground = Color(0xFFF8F9FD)

@Composable
fun HomeScreen(
    onSwitchView: () -> Unit = {},
    viewModel: JobViewModel = viewModel()
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val jobState by viewModel.jobState
    
    // Load jobs on first launch
    LaunchedEffect(Unit) {
        viewModel.loadJobs()
    }
    
    // Search jobs when query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            viewModel.loadJobs()
        } else {
            viewModel.searchJobs(searchQuery)
        }
    }

    val filteredJobs = jobState.jobs.map { job ->
        job.toUIJob(isBookmarked = jobState.savedJobIds.contains(job.jobId))
    }

    // Animated floating effect
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            /* ================= ENHANCED HEADER ================= */
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    LightBlue,
                                    AccentPurple
                                )
                            )
                        )
                ) {
                    // Floating decorative circles
                    Box(
                        modifier = Modifier
                            .offset(x = (-30).dp, y = 40.dp + floatY.dp)
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .blur(30.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 30.dp, y = (-20).dp - floatY.dp)
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.2f))
                            .blur(40.dp)
                    )

                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Welcome back!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Find Your Dream Job",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 28.sp
                                    ),
                                    color = Color.White
                                )
                            }

                            // Modern Switch Button
                            Surface(
                                onClick = onSwitchView,
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = "Owner",
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 10.dp
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Discover amazing part-time opportunities",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(24.dp))

                        // Modern Search Bar
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Search for jobs, roles...",
                                    color = Color.Gray.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Search,
                                    contentDescription = "Search",
                                    tint = PrimaryBlue
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = PrimaryBlue
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            singleLine = true
                        )
                    }
                }
            }

            /* ================= CONTENT SECTION ================= */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Available Jobs",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 24.sp
                                ),
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${filteredJobs.size} opportunities found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Modern Filter Button
                        Surface(
                            onClick = { /* TODO: Filter */ },
                            shape = RoundedCornerShape(16.dp),
                            color = PrimaryBlue.copy(alpha = 0.1f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.Tune,
                                    contentDescription = "Filter",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            /* ================= JOB LIST ================= */
            if (jobState.isLoading && filteredJobs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(
                    items = filteredJobs,
                    key = { job: Job -> job.id }
                ) { job: Job ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                        JobListItem(
                            job = job,
                            onBookmarkClick = { updatedJob ->
                                val backendJob = jobState.jobs.find { it.jobId == updatedJob.id }
                                backendJob?.let {
                                    viewModel.toggleSaveJob(it.jobId)
                                }
                            },
                            onCallClick = {
                                // Handle call
                            }
                        )
                    }
                }
            }
        }
    }
}