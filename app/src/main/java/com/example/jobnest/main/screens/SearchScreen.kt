package com.example.jobnest.main.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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

// Premium Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val SoftBackground = Color(0xFFF8F9FD)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(onBackClick: () -> Unit = {}, onSwitchView: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var salaryRange by remember { mutableStateOf(500f..3000f) }
    var workTypeExpanded by remember { mutableStateOf(false) }
    var selectedWorkType by remember { mutableStateOf("Select work type") }
    var locationExpanded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("Select location") }
    var selectedWorkTime by remember { mutableStateOf("") }

    val workTypes = listOf("Food Service", "Delivery", "Office Work", "Retail")
    val locations = listOf("Colombo", "Galle", "Kandy", "Jaffna")
    val workTimes = listOf("Morning", "Afternoon", "Evening", "Night")

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
    ) {
        // --- Enhanced Header ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp) // Fixed height for header to prevent jumping
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(LightBlue, AccentPurple)
                    )
                )
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .offset(x = (-30).dp, y = 20.dp + floatY.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .blur(25.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Search & Filter",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Surface(
                    onClick = onSwitchView,
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ) {
                    Text(
                        text = "Owner",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // --- Main Content Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // CRITICAL: This fills the remaining screen space
                .offset(y = (-24).dp), // Slight overlap with header
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section: Inputs and Filters
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search jobs...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = PrimaryBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF7FAFC),
                            focusedContainerColor = Color(0xFFF7FAFC)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Filters", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { }) {
                            Text("Clear All", color = PrimaryBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Salary Range Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PrimaryBlue.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Salary Range", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Rs. ${salaryRange.start.toInt()} - ${salaryRange.endInclusive.toInt()}",
                                    color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            RangeSlider(
                                value = salaryRange,
                                onValueChange = { salaryRange = it },
                                valueRange = 0f..5000f
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdowns
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = workTypeExpanded,
                            onExpandedChange = { workTypeExpanded = !workTypeExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = if (selectedWorkType == "Select work type") "Work Type" else selectedWorkType,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(workTypeExpanded) },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        ExposedDropdownMenuBox(
                            expanded = locationExpanded,
                            onExpandedChange = { locationExpanded = !locationExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = if (selectedLocation == "Select location") "Location" else selectedLocation,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(locationExpanded) },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Work Time", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Work Time Buttons
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        workTimes.forEach { time ->
                            val isSelected = selectedWorkTime == time
                            Button(
                                onClick = { selectedWorkTime = time },
                                modifier = Modifier.height(45.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) PrimaryBlue else Color.White,
                                    contentColor = if (isSelected) Color.White else Color.Gray
                                ),
                                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
                            ) {
                                Text(time, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Bottom section: Apply Button
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(PrimaryBlue, LightBlue))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Apply Filters", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}