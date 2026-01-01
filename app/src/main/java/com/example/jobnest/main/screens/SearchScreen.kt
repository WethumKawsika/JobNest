package com.example.jobnest.main.screens
import com.example.jobnest.utils.toUIJob
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobnest.viewmodel.JobViewModel
import com.example.jobnest.main.screens.common.JobListItem
import com.example.jobnest.main.screens.common.JobUI
import androidx.navigation.NavController

// Light Theme Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val SoftBackground = Color(0xFFFAFAFA)
private val CardBackground = Color.White
private val DarkText = Color(0xFF1A1A2E)
private val MutedText = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE2E8F0)
private val ChipBackground = Color(0xFFF7FAFC)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit = {},
    onSwitchView: () -> Unit = {},
    viewModel: JobViewModel = viewModel()
) {

    var searchQuery by remember { mutableStateOf("") }
    var salaryRange by remember { mutableStateOf(500f..3000f) }
    var workTypeExpanded by remember { mutableStateOf(false) }
    var selectedWorkType by remember { mutableStateOf("Select work type") }
    var locationExpanded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("Select location") }
    var selectedWorkTime by remember { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }

    val workTypes = listOf("Food Service", "Delivery", "Office Work", "Retail", "Promotion", "Tuition")
    val locations = listOf("Colombo", "Galle", "Kandy", "Jaffna", "Negombo", "Kurunegala")
    val workTimes = listOf("Morning", "Afternoon", "Evening", "Night", "Flexible")

    val jobState by viewModel.jobState.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadJobs() }

    val allJobs: List<JobUI> = jobState.jobs.map { it.toUIJob(isBookmarked = jobState.savedJobIds.contains(it.jobId)) }

    val filteredJobs by remember(searchQuery, salaryRange, selectedWorkType, selectedLocation, selectedWorkTime, jobState.jobs) {
        derivedStateOf {
            allJobs.filter { job ->
                val matchesQuery = searchQuery.isBlank() || listOf(job.title, job.company, job.location).any { it.contains(searchQuery, ignoreCase = true) }
                val matchesWorkType = (selectedWorkType == "Select work type") || job.workType.equals(selectedWorkType, ignoreCase = true)
                val matchesLocation = (selectedLocation == "Select location") || job.location.contains(selectedLocation, ignoreCase = true)
                val matchesWorkTime = (selectedWorkTime.isBlank()) || job.workTime.equals(selectedWorkTime, ignoreCase = true)
                val matchesSalary = job.minSalary.toIntOrNull()?.let { it >= salaryRange.start.toInt() } ?: true

                matchesQuery && matchesWorkType && matchesLocation && matchesWorkTime && matchesSalary
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
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
            // Enhanced Header with Light Theme
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(LightBlue, AccentPurple),
                            startY = 0f,
                            endY = 500f
                        )
                    )
            ) {
                // Decorative floating circles
                Box(
                    modifier = Modifier
                        .offset(x = (-40).dp, y = 15.dp + floatY.dp)
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .blur(30.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 50.dp, y = (-20).dp - floatY.dp)
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.2f))
                        .blur(35.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                                .shadow(4.dp, CircleShape)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Search & Filter",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Find your perfect job",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        IconButton(
                            onClick = onSwitchView,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                                .shadow(4.dp, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "Advanced",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Main Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(y = (-20).dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by job title, company...", fontSize = 14.sp, color = MutedText) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = MutedText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = ChipBackground,
                            focusedContainerColor = ChipBackground,
                            unfocusedBorderColor = BorderColor,
                            focusedBorderColor = PrimaryBlue,
                            cursorColor = PrimaryBlue
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp, 24.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(PrimaryBlue, AccentPurple)
                                    ),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Filter Options",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                searchQuery = ""
                                salaryRange = 500f..3000f
                                selectedWorkType = "Select work type"
                                selectedLocation = "Select location"
                                selectedWorkTime = ""
                                showResults = false
                            }
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Clear All",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Salary Range Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = PrimaryBlue.copy(alpha = 0.06f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AttachMoney,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Salary Range",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = DarkText
                                    )
                                }
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = PrimaryBlue.copy(alpha = 0.15f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Rs. ${salaryRange.start.toInt()} - ${salaryRange.endInclusive.toInt()}",
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            RangeSlider(
                                value = salaryRange,
                                onValueChange = { salaryRange = it },
                                valueRange = 0f..5000f,
                                colors = SliderDefaults.colors(
                                    thumbColor = PrimaryBlue,
                                    activeTrackColor = PrimaryBlue,
                                    inactiveTrackColor = BorderColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Work Type & Location
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Category,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Work Type",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = workTypeExpanded,
                                onExpandedChange = { workTypeExpanded = it }
                            ) {
                                // Make the entire text field clickable (no ripple) so taps reliably toggle the menu
                                val interactionSource = remember { MutableInteractionSource() }
                                OutlinedTextField(
                                    value = if (selectedWorkType == "Select work type") "Select" else selectedWorkType,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { workTypeExpanded = !workTypeExpanded }) {
                                            Icon(
                                                imageVector = if (workTypeExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = MutedText
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                        .clickable(indication = null, interactionSource = interactionSource) { workTypeExpanded = !workTypeExpanded },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = ChipBackground,
                                        focusedContainerColor = ChipBackground,
                                        unfocusedBorderColor = BorderColor,
                                        focusedBorderColor = PrimaryBlue,
                                        disabledContainerColor = ChipBackground,
                                        disabledBorderColor = BorderColor,
                                        disabledTextColor = DarkText
                                    ),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                                )
                                ExposedDropdownMenu(
                                    expanded = workTypeExpanded,
                                    onDismissRequest = { workTypeExpanded = false }
                                ) {
                                    workTypes.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type, fontSize = 14.sp) },
                                            onClick = {
                                                selectedWorkType = type
                                                workTypeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Location",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = locationExpanded,
                                onExpandedChange = { locationExpanded = it }
                            ) {
                                val interactionSourceLoc = remember { MutableInteractionSource() }
                                OutlinedTextField(
                                    value = if (selectedLocation == "Select location") "Select" else selectedLocation,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { locationExpanded = !locationExpanded }) {
                                            Icon(
                                                imageVector = if (locationExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = MutedText
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                        .clickable(indication = null, interactionSource = interactionSourceLoc) { locationExpanded = !locationExpanded },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = ChipBackground,
                                        focusedContainerColor = ChipBackground,
                                        unfocusedBorderColor = BorderColor,
                                        focusedBorderColor = PrimaryBlue,
                                        disabledContainerColor = ChipBackground,
                                        disabledBorderColor = BorderColor,
                                        disabledTextColor = DarkText
                                    ),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                                )
                                ExposedDropdownMenu(
                                    expanded = locationExpanded,
                                    onDismissRequest = { locationExpanded = false }
                                ) {
                                    locations.forEach { location ->
                                        DropdownMenuItem(
                                            text = { Text(location, fontSize = 14.sp) },
                                            onClick = {
                                                selectedLocation = location
                                                locationExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Work Time
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Work Time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarkText
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        workTimes.forEach { time ->
                            val isSelected = selectedWorkTime == time
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedWorkTime = if (isSelected) "" else time },
                                label = {
                                    Text(
                                        time,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    containerColor = ChipBackground,
                                    labelColor = MutedText
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) PrimaryBlue else BorderColor,
                                    selectedBorderColor = PrimaryBlue,
                                    borderWidth = 1.5.dp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Apply Button
                    Button(
                        onClick = { showResults = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(PrimaryBlue, LightBlue, AccentPurple)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp),
                                    tint = Color.White
                                )
                                Text(
                                    "Apply Filters",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    // Results Section
                    if (showResults) {
                        Spacer(modifier = Modifier.height(28.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp, 24.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(PrimaryBlue, AccentPurple)
                                        ),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Search Results",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = PrimaryBlue.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "${filteredJobs.size} jobs",
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (jobState.isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = PrimaryBlue)
                            }
                        } else {
                            if (filteredJobs.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = ChipBackground
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.SearchOff,
                                            contentDescription = null,
                                            tint = MutedText,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            "No jobs found",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkText
                                        )
                                        Text(
                                            "Try adjusting your filters",
                                            fontSize = 13.sp,
                                            color = MutedText
                                        )
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    filteredJobs.forEach { job ->
                                        JobListItem(
                                            job = job,
                                            onBookmarkClick = { viewModel.toggleBookmark(job.id) },
                                            onCallClick = { /* call */ },
                                            onLocationClick = { /* open maps */ }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen()}