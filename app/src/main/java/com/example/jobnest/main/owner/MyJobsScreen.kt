package com.example.jobnest.main.owner

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobnest.main.screens.common.JobListItem
import com.example.jobnest.main.screens.common.JobUI
import com.example.jobnest.utils.toUIJob
import com.example.jobnest.viewmodel.JobViewModel

// Premium Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val SoftBackground = Color(0xFFF8F9FD)
private val CardBackground = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)

@Composable
fun MyJobsScreen(
    onSwitchView: () -> Unit = {},
    viewModel: JobViewModel = viewModel(),
    refreshTrigger: Int = 0
) {
    val jobState by viewModel.jobState.collectAsState()
    val context = LocalContext.current
    var jobToDelete by remember { mutableStateOf<com.example.jobnest.main.screens.common.JobUI?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load jobs created by the current user - reload when refreshTrigger changes
    LaunchedEffect(refreshTrigger) {
        viewModel.loadMyJobs()
    }

    val myJobs: List<JobUI> = jobState.jobs.map { job ->
        job.toUIJob(isBookmarked = false) // Owner's own jobs don't need bookmark state
    }

    // Animated floating effect
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    val floatX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatX"
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
            /* ================= PREMIUM GRADIENT HEADER ================= */
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667EEA),
                                    Color(0xFF764BA2),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        )
                ) {
                    // Animated floating decorative elements
                    Box(
                        modifier = Modifier
                            .offset(x = (-40).dp + floatX.dp, y = 60.dp + floatY.dp)
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .blur(35.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 50.dp - floatX.dp, y = (-30).dp - floatY.dp)
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.15f))
                            .blur(45.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 100.dp + floatX.dp, y = 20.dp)
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.06f))
                            .blur(25.dp)
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 32.dp, bottom = 28.dp)
                    ) {
                        // Header badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Owner Dashboard",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Main title
                        Text(
                            text = "My Posted Jobs",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color.White
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Manage your job postings and track applications efficiently",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(20.dp))

                        // Stats Cards Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Active Jobs Card
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                shadowElevation = 0.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Work,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "${myJobs.size}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Active Jobs",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Total Views Card
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                shadowElevation = 0.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "---",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Total Views",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            /* ================= SECTION HEADER ================= */
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 28.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = CardBackground,
                    shadowElevation = 2.dp,
                    tonalElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Your Job Postings",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 20.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryBlue.copy(alpha = 0.1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(PrimaryBlue)
                                    )
                                }
                                Text(
                                    text = "${myJobs.size} posting${if (myJobs.size != 1) "s" else ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimaryBlue.copy(alpha = 0.1f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.BarChart,
                                    contentDescription = "Statistics",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            /* ================= JOB LIST ================= */
            if (jobState.isLoading && myJobs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryBlue,
                                strokeWidth = 3.dp
                            )
                            Text(
                                text = "Loading your jobs...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else if (myJobs.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = CardBackground,
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF3F4F6),
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.WorkOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = Color(0xFF9CA3AF)
                                    )
                                }
                            }
                            Text(
                                text = "No Jobs Posted Yet",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Start building your team by posting your first job opening",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 20.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PrimaryBlue.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Tap the + button below to create a job",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                items(
                    items = myJobs,
                    key = { it.id }
                ) { job ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                        JobListItem(
                            job = job,
                            onBookmarkClick = {
                                // Owner's own jobs - bookmark not applicable
                            },
                            onCallClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = "tel:${job.phoneNumber}".toUri()
                                }
                                context.startActivity(intent)
                            },
                            onLocationClick = {
                                job.locationLatLng?.let { latLng ->
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
                            },
                            onDelete = {
                                jobToDelete = job
                                showDeleteDialog = true
                            }
                        )
                    }
                }

                // Show error if any
                jobState.error?.let { err ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFDC2626))
                                Spacer(Modifier.width(12.dp))
                                Text(text = err, color = Color(0xFFDC2626))
                            }
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog must be outside LazyColumn (cannot call composables directly inside LazyColumn scope)
        if (showDeleteDialog && jobToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false; jobToDelete = null },
                title = { Text("Delete Job") },
                text = { Text("Are you sure you want to delete \"${jobToDelete?.title}\"? This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        val id = jobToDelete?.id ?: ""
                        if (id.isNotBlank()) {
                            viewModel.deleteJob(id) {
                                showDeleteDialog = false
                                jobToDelete = null
                            }
                        } else {
                            showDeleteDialog = false
                            jobToDelete = null
                        }
                    }) {
                        Text("Delete", color = Color(0xFFEF4444))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false; jobToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
