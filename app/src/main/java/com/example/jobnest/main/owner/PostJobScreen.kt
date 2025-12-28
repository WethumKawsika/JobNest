package com.example.jobnest.main.owner

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Premium Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val SoftBackground = Color(0xFFF8F9FD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    onBackPressed: () -> Unit = {},
    onPostJob: () -> Unit = {}
) {
    var jobTitle by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var minSalary by remember { mutableStateOf("") }
    var maxSalary by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var requiredPersons by remember { mutableStateOf("") }
    var ageLimit by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var workTypeExpanded by remember { mutableStateOf(false) }
    var selectedWorkType by remember { mutableStateOf("Select work type") }

    var workTimeExpanded by remember { mutableStateOf(false) }
    var selectedWorkTime by remember { mutableStateOf("Select work time") }

    var foodExpanded by remember { mutableStateOf(false) }
    var selectedFood by remember { mutableStateOf("Select food availability") }

    var transportExpanded by remember { mutableStateOf(false) }
    var selectedTransport by remember { mutableStateOf("Select transport") }

    var genderExpanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Select gender preference") }

    val workTypes = listOf("Promotion", "Tuition", "Delivery", "Food Service", "Office Work", "Retail", "Other")
    val workTimes = listOf("Morning", "Afternoon", "Evening", "Night", "Flexible")
    val foodOptions = listOf("None", "Breakfast", "Lunch", "Dinner", "Breakfast & Lunch", "Lunch & Dinner", "All Meals")
    val transportOptions = listOf("Not Provided", "Provided", "Reimbursed")
    val genderOptions = listOf("Any", "Male", "Female", "Male Preferred", "Female Preferred")

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
                        .offset(x = (-30).dp, y = 20.dp + floatY.dp)
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBackPressed,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = "Post a New Job",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Box(modifier = Modifier.size(40.dp))
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Fill in the details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Text(
                        text = "Provide complete information about the job",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Form Fields
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        // Job Title
                        JobInputField(
                            label = "Job Title",
                            value = jobTitle,
                            onValueChange = { jobTitle = it },
                            leadingIcon = Icons.Default.Work,
                            placeholder = "e.g., Delivery Driver"
                        )

                        // Company
                        JobInputField(
                            label = "Company Name",
                            value = company,
                            onValueChange = { company = it },
                            leadingIcon = Icons.Default.Business,
                            placeholder = "e.g., Food Service Inc."
                        )

                        // Salary Range (Two fields side by side)
                        Column {
                            Text(
                                text = "💰 Salary Range (Rs./day)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3748),
                                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = minSalary,
                                    onValueChange = { minSalary = it },
                                    placeholder = { Text("Min", fontSize = 14.sp, color = Color.Gray) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.AttachMoney,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        focusedContainerColor = Color(0xFFF7FAFC),
                                        unfocusedContainerColor = Color(0xFFF7FAFC),
                                        cursorColor = PrimaryBlue
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = maxSalary,
                                    onValueChange = { maxSalary = it },
                                    placeholder = { Text("Max", fontSize = 14.sp, color = Color.Gray) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        focusedContainerColor = Color(0xFFF7FAFC),
                                        unfocusedContainerColor = Color(0xFFF7FAFC),
                                        cursorColor = PrimaryBlue
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                    singleLine = true
                                )
                            }
                        }

                        // Work Type Dropdown
                        DropdownField(
                            label = "🧰 Work Type",
                            value = selectedWorkType,
                            expanded = workTypeExpanded,
                            onExpandedChange = { workTypeExpanded = !workTypeExpanded },
                            items = workTypes,
                            onItemSelected = { selectedWorkType = it; workTypeExpanded = false },
                            icon = Icons.Default.Category
                        )

                        // Location
                        JobInputField(
                            label = "📍 Location",
                            value = location,
                            onValueChange = { location = it },
                            leadingIcon = Icons.Default.LocationOn,
                            placeholder = "e.g., Colombo 03"
                        )

                        // Work Time Dropdown
                        DropdownField(
                            label = "⏰ Work Time",
                            value = selectedWorkTime,
                            expanded = workTimeExpanded,
                            onExpandedChange = { workTimeExpanded = !workTimeExpanded },
                            items = workTimes,
                            onItemSelected = { selectedWorkTime = it; workTimeExpanded = false },
                            icon = Icons.Default.Schedule
                        )

                        // Food Availability Dropdown
                        DropdownField(
                            label = "🍱 Food Availability",
                            value = selectedFood,
                            expanded = foodExpanded,
                            onExpandedChange = { foodExpanded = !foodExpanded },
                            items = foodOptions,
                            onItemSelected = { selectedFood = it; foodExpanded = false },
                            icon = Icons.Default.Restaurant
                        )

                        // Transport Dropdown
                        DropdownField(
                            label = "🚗 Transport",
                            value = selectedTransport,
                            expanded = transportExpanded,
                            onExpandedChange = { transportExpanded = !transportExpanded },
                            items = transportOptions,
                            onItemSelected = { selectedTransport = it; transportExpanded = false },
                            icon = Icons.Default.DirectionsCar
                        )

                        // Gender Preference & Required Persons (Two fields side by side)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Gender Preference
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "👨‍💼 Gender",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D3748),
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                                )
                                ExposedDropdownMenuBox(
                                    expanded = genderExpanded,
                                    onExpandedChange = { genderExpanded = !genderExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = if (selectedGender == "Select gender preference") "Any" else selectedGender,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryBlue,
                                            unfocusedBorderColor = Color(0xFFE2E8F0),
                                            focusedContainerColor = Color(0xFFF7FAFC),
                                            unfocusedContainerColor = Color(0xFFF7FAFC)
                                        ),
                                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = genderExpanded,
                                        onDismissRequest = { genderExpanded = false }
                                    ) {
                                        genderOptions.forEach { gender ->
                                            DropdownMenuItem(
                                                text = { Text(gender, fontSize = 13.sp) },
                                                onClick = {
                                                    selectedGender = gender
                                                    genderExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Required Persons
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Count",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D3748),
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = requiredPersons,
                                    onValueChange = { requiredPersons = it },
                                    placeholder = { Text("e.g., 2", fontSize = 14.sp, color = Color.Gray) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Group,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        focusedContainerColor = Color(0xFFF7FAFC),
                                        unfocusedContainerColor = Color(0xFFF7FAFC),
                                        cursorColor = PrimaryBlue
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                    singleLine = true
                                )
                            }
                        }

                        // Age Limit (Optional)
                        JobInputField(
                            label = "🎂 Age Limit (Optional)",
                            value = ageLimit,
                            onValueChange = { ageLimit = it },
                            leadingIcon = Icons.Default.Cake,
                            placeholder = "e.g., 18-35",
                            keyboardType = KeyboardType.Text
                        )

                        // Job Description
                        Column {
                            Text(
                                text = "Job Description",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3748),
                                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = {
                                    Text(
                                        "Describe the job requirements and responsibilities",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color(0xFFF7FAFC),
                                    unfocusedContainerColor = Color(0xFFF7FAFC),
                                    cursorColor = PrimaryBlue
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                maxLines = 5
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Post Job Button
                    Button(
                        onClick = onPostJob,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(PrimaryBlue, LightBlue)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "Post Job",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cancel Button
                    TextButton(
                        onClick = onBackPressed,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B7280)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun JobInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(22.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color(0xFFF7FAFC),
                unfocusedContainerColor = Color(0xFFF7FAFC),
                cursorColor = PrimaryBlue
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF7FAFC),
                    unfocusedContainerColor = Color(0xFFF7FAFC)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item, fontSize = 14.sp) },
                        onClick = { onItemSelected(item) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostJobScreenPreview() {
    PostJobScreen()
}