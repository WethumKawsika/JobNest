package com.example.jobnest.main.screens

import android.content.Intent
import com.example.jobnest.utils.toUIJob
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobnest.main.screens.common.JobListItem
import com.example.jobnest.utils.toUIJob
import com.example.jobnest.viewmodel.JobViewModel

// Premium Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val SoftBackground = Color(0xFFF8F9FD)

@Composable
fun SavedScreen(
    onSwitchView: () -> Unit = {},
    viewModel: JobViewModel = viewModel()
) {
    val jobState by viewModel.jobState.collectAsState()
    val savedJobsList = jobState.savedJobs
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadSavedJobs()
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
                // Floating decorative circles
                Box(
                    modifier = Modifier
                        .offset(x = (-30).dp, y = 30.dp + floatY.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .blur(25.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-10).dp - floatY.dp)
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.2f))
                        .blur(30.dp)
                )

                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Saved Jobs",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your bookmarked opportunities",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
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
                when {
                    savedJobsList.isEmpty() -> {
                        // Empty State
                        EmptySavedState()
                    }
                    else -> {
                        // Jobs List
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            // Stats Badge
                            SavedStatsBadge(savedJobsList.size)

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Your Collection",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(
                                    items = savedJobsList,
                                    key = { item -> item.jobId }
                                ) { job ->
                                    val jobUi = job.toUIJob(isBookmarked = true)
                                    JobListItem(
                                        job = jobUi,
                                        onBookmarkClick = {
                                            viewModel.toggleBookmark(jobId = job.jobId)
                                        },
                                        onCallClick = {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = "tel:${jobUi.phoneNumber}".toUri()
                                            }
                                            context.startActivity(intent)
                                        },
                                        onLocationClick = {
                                            jobUi.locationLatLng?.let { latLng ->
                                                val lat = latLng.latitude
                                                val lng = latLng.longitude
                                                val gmmIntentUri = "google.navigation:q=$lat,$lng".toUri()
                                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                                mapIntent.setPackage("com.google.android.apps.maps")

                                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                                    context.startActivity(mapIntent)
                                                } else {
                                                    val browserUri =
                                                        "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng".toUri()
                                                    context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedStatsBadge(count: Int) {
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
                    text = "$count",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryBlue
                )
                Text(
                    text = "Saved Job${if (count != 1) "s" else ""}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280)
                )
            }
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun EmptySavedState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = PrimaryBlue.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Saved Jobs Yet",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start bookmarking jobs to see them here",
            fontSize = 15.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SavedScreenPreview() {
    SavedScreen()
}
