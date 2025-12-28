package com.example.jobnest.main.owner

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobnest.main.screens.common.Job
import com.example.jobnest.main.screens.common.JobListItem

// Premium Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val SoftBackground = Color(0xFFF8F9FD)

@Composable
fun MyJobsScreen(
    onSwitchView: () -> Unit = {},
    onPostJob: () -> Unit = {}
) {
    // FIXED: Updated list with all mandatory fields from your new Job data class
    val myJobs = listOf(
        Job(
            title = "Part-Time Waiter",
            description = "Looking for a friendly waiter for evening shifts at a busy restaurant.",
            company = "Food Service",
            location = "Colombo 03",
            salary = "Rs. 800/day",
            workType = "Part-Time",
            food = "Dinner provided",
            transport = "Not provided",
            workTime = "Evening (5 PM - 11 PM)",
            requiredPersons = "Male, 2",
            isBookmarked = true
        ),
        Job(
            title = "Delivery Driver",
            description = "Delivery driver needed for morning shifts. Must have own vehicle.",
            company = "Flash Delivery",
            location = "Galle",
            salary = "Rs. 1200/day",
            workType = "Delivery",
            food = "Not provided",
            transport = "Self",
            workTime = "Morning (8 AM - 12 PM)",
            requiredPersons = "Any, 5",
            isBookmarked = false
        )
    )

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
        Column(modifier = Modifier.fillMaxSize()) {
            // Enhanced Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(LightBlue, AccentPurple)
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = (-30).dp, y = 30.dp + floatY.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .blur(25.dp)
                )

                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "My Posted Jobs",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Manage your job postings",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Surface(
                            onClick = onSwitchView,
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        ) {
                            Text(
                                text = "Student View",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Content Card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-16).dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                if (myJobs.isEmpty()) {
                    // Empty State UI
                    EmptyStateView(onPostJob)
                } else {
                    // Jobs List
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // Stats Badge
                        StatsBadge(myJobs.size)

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Listings",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )

                            FloatingActionButton(
                                onClick = onPostJob,
                                modifier = Modifier.size(48.dp),
                                containerColor = PrimaryBlue,
                                contentColor = Color.White,
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Post Job")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(myJobs) { job ->
                                // FIXED: JobListItem now correctly receives the updated Job object
                                JobListItem(
                                    job = job,
                                    onBookmarkClick = { /* Owner doesn't bookmark own job */ },
                                    onCallClick = { /* Handle edit or view applicants here */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsBadge(jobCount: Int) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = PrimaryBlue.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$jobCount",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryBlue
                )
                Text(
                    text = "Active Job${if (jobCount != 1) "s" else ""}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280)
                )
            }
            Icon(
                imageVector = Icons.Default.WorkOutline,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun EmptyStateView(onPostJob: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.WorkOutline,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = PrimaryBlue.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("No Jobs Posted Yet", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onPostJob,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Post Your First Job", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyJobsScreenPreview() {
    MyJobsScreen()
}