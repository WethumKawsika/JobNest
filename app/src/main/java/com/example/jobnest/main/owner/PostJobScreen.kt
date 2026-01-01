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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobnest.data.Job
import com.example.jobnest.viewmodel.AuthViewModel
import com.example.jobnest.viewmodel.JobViewModel
import com.google.android.gms.maps.model.LatLng

// Premium Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val SoftBackground = Color(0xFFF8F9FD)
private val DarkText = Color(0xFF1A1A2E)
private val MutedText = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    onBackPressed: () -> Unit = {},
    onOpenMapPicker: () -> Unit = {},
    selectedAddress: String = "",
    onJobPostedSuccessfully: () -> Unit,
    selectedLatLng: LatLng? = null,
    viewModel: JobViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var jobTitle by rememberSaveable { mutableStateOf("") }
    var company by rememberSaveable { mutableStateOf("") }
    var contactNumber by rememberSaveable { mutableStateOf("") }
    var minSalary by rememberSaveable { mutableStateOf("") }
    var maxSalary by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var requiredPersons by rememberSaveable { mutableStateOf("") }
    var ageLimit by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    var workTypeExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedWorkType by rememberSaveable { mutableStateOf("Select work type") }

    var workTimeExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedWorkTime by rememberSaveable { mutableStateOf("Select work time") }

    var foodExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedFood by rememberSaveable { mutableStateOf("Select food availability") }

    var transportExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedTransport by rememberSaveable { mutableStateOf("Select transport") }

    var genderExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedGender by rememberSaveable { mutableStateOf("Male") }
    var boysCount by rememberSaveable { mutableStateOf("") }
    var girlsCount by rememberSaveable { mutableStateOf("") }

    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    var validationError by rememberSaveable { mutableStateOf<String?>(null) }

    val jobState by viewModel.jobState.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    var jobPostedSuccessfully by rememberSaveable { mutableStateOf(false) }

    val clearForm = {
        jobTitle = ""
        company = ""
        minSalary = ""
        maxSalary = ""
        location = ""
        requiredPersons = ""
        ageLimit = ""
        description = ""
        selectedWorkType = "Select work type"
        selectedWorkTime = "Select work time"
        selectedFood = "Select food availability"
        selectedTransport = "Select transport"
        selectedGender = "Male"
        boysCount = ""
        girlsCount = ""
        validationError = null
    }

    LaunchedEffect(selectedAddress) {
        if (selectedAddress.isNotEmpty()) {
            location = selectedAddress
        }
    }

    LaunchedEffect(selectedLatLng) {
        if (selectedLatLng != null) {
            currentLatLng = selectedLatLng
        }
    }

    LaunchedEffect(authState.currentUserData) {
        authState.currentUserData?.phoneNumber?.let {
            if (contactNumber.isEmpty() && it.isNotEmpty()) {
                contactNumber = it
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    LaunchedEffect(jobState.isLoading, jobState.error) {
        if (jobPostedSuccessfully && !jobState.isLoading && jobState.error == null) {
            clearForm()
            jobPostedSuccessfully = false
            onJobPostedSuccessfully()
        }
    }

    val workTypes = listOf("Promotion", "Tuition", "Delivery", "Food Service", "Office Work", "Retail", "Other")
    val workTimes = listOf("Morning", "Afternoon", "Evening", "Night", "Flexible")
    val foodOptions = listOf("None", "Breakfast", "Lunch", "Dinner", "Breakfast & Lunch", "Lunch & Dinner", "All Meals")
    val transportOptions = listOf("Not Provided", "Provided", "Reimbursed")
    val genderOptions = listOf("Male", "Female", "Both")

    // Animated floating effect
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

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Enhanced Header with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(LightBlue, AccentPurple),
                            startY = 0f,
                            endY = 600f
                        )
                    )
            ) {
                // Animated background elements
                Box(
                    modifier = Modifier
                        .offset(x = (-40).dp, y = 10.dp + floatY.dp)
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .blur(30.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 40.dp, y = (-20).dp - floatY.dp)
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.25f))
                        .blur(35.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 42.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBackPressed,
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
                                text = "Post a New Job",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Find the perfect candidates",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Box(modifier = Modifier.size(44.dp))
                    }
                }
            }

            // Content Card with elevated design
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-20).dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp, 28.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(PrimaryBlue, AccentPurple)
                                    ),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Job Details",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkText
                            )
                            Text(
                                text = "Provide complete and accurate information",
                                fontSize = 13.sp,
                                color = MutedText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Form Fields
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        JobInputField("Job Title", jobTitle, { jobTitle = it }, Icons.Default.Work, "e.g., Delivery Driver")
                        JobInputField("Company Name", company, { company = it }, Icons.Default.Business, "e.g., Food Service Inc.")
                        JobInputField("Contact Number", contactNumber, { contactNumber = it }, Icons.Default.Phone, "e.g., 0771234567", KeyboardType.Phone)

                        // Salary Range with enhanced design
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Salary Range (Rs./day)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = minSalary,
                                    onValueChange = { minSalary = it },
                                    placeholder = { Text("Minimum", fontSize = 14.sp, color = MutedText) },
                                    prefix = { Text("Rs. ", fontSize = 14.sp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold) },
                                    modifier = Modifier.weight(1f).height(58.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        focusedContainerColor = Color(0xFFF7FAFC),
                                        unfocusedContainerColor = Color(0xFFF7FAFC),
                                        cursorColor = PrimaryBlue
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = maxSalary,
                                    onValueChange = { maxSalary = it },
                                    placeholder = { Text("Maximum", fontSize = 14.sp, color = MutedText) },
                                    prefix = { Text("Rs. ", fontSize = 14.sp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold) },
                                    modifier = Modifier.weight(1f).height(58.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        focusedContainerColor = Color(0xFFF7FAFC),
                                        unfocusedContainerColor = Color(0xFFF7FAFC),
                                        cursorColor = PrimaryBlue
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                                    singleLine = true
                                )
                            }
                        }

                        DropdownField("🧰 Work Type", selectedWorkType, workTypeExpanded, { workTypeExpanded = !workTypeExpanded }, workTypes, { selectedWorkType = it; workTypeExpanded = false }, Icons.Default.Category)

                        LocationInputField(location, onOpenMapPicker)

                        DropdownField("⏰ Work Time", selectedWorkTime, workTimeExpanded, { workTimeExpanded = !workTimeExpanded }, workTimes, { selectedWorkTime = it; workTimeExpanded = false }, Icons.Default.Schedule)
                        DropdownField("🍱 Food Availability", selectedFood, foodExpanded, { foodExpanded = !foodExpanded }, foodOptions, { selectedFood = it; foodExpanded = false }, Icons.Default.Restaurant)
                        DropdownField("🚗 Transport", selectedTransport, transportExpanded, { transportExpanded = !transportExpanded }, transportOptions, { selectedTransport = it; transportExpanded = false }, Icons.Default.DirectionsCar)

                        // Gender & Count
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                DropdownField(
                                    label = "👨‍💼 Gender",
                                    value = selectedGender,
                                    expanded = genderExpanded,
                                    onExpandedChange = { genderExpanded = !genderExpanded },
                                    items = genderOptions,
                                    onItemSelected = { selectedGender = it; genderExpanded = false },
                                    icon = Icons.Default.Person
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Group,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Count",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkText
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                if (selectedGender == "Both") {
                                    OutlinedTextField(
                                        value = boysCount,
                                        onValueChange = { boysCount = it },
                                        placeholder = { Text("Boys", fontSize = 14.sp, color = MutedText) },
                                        leadingIcon = { Icon(Icons.Default.Male, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp)) },
                                        modifier = Modifier.fillMaxWidth().height(58.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryBlue,
                                            unfocusedBorderColor = Color(0xFFE2E8F0),
                                            focusedContainerColor = Color(0xFFF7FAFC),
                                            unfocusedContainerColor = Color(0xFFF7FAFC),
                                            cursorColor = PrimaryBlue
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = girlsCount,
                                        onValueChange = { girlsCount = it },
                                        placeholder = { Text("Girls", fontSize = 14.sp, color = MutedText) },
                                        leadingIcon = { Icon(Icons.Default.Female, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp)) },
                                        modifier = Modifier.fillMaxWidth().height(58.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryBlue,
                                            unfocusedBorderColor = Color(0xFFE2E8F0),
                                            focusedContainerColor = Color(0xFFF7FAFC),
                                            unfocusedContainerColor = Color(0xFFF7FAFC),
                                            cursorColor = PrimaryBlue
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                                        singleLine = true
                                    )
                                } else {
                                    OutlinedTextField(
                                        value = requiredPersons,
                                        onValueChange = { requiredPersons = it },
                                        placeholder = { Text("e.g., 2", fontSize = 14.sp, color = MutedText) },
                                        leadingIcon = { Icon(Icons.Default.Group, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp)) },
                                        modifier = Modifier.fillMaxWidth().height(58.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryBlue,
                                            unfocusedBorderColor = Color(0xFFE2E8F0),
                                            focusedContainerColor = Color(0xFFF7FAFC),
                                            unfocusedContainerColor = Color(0xFFF7FAFC),
                                            cursorColor = PrimaryBlue
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        JobInputField("🎂 Age Limit (Optional)", ageLimit, { ageLimit = it }, Icons.Default.Cake, "e.g., 18-35", KeyboardType.Text)

                        // Description with enhanced design
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Job Description",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = { Text("Describe the job requirements and responsibilities in detail", color = MutedText, fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth().height(140.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color(0xFFF7FAFC),
                                    unfocusedContainerColor = Color(0xFFF7FAFC),
                                    cursorColor = PrimaryBlue
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 22.sp),
                                maxLines = 6
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Show validation errors
                    validationError?.let { error ->
                        ErrorCard(error)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Show job posting errors
                    jobState.error?.let { error ->
                        ErrorCard(error)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Post Button with gradient and animation
                    Button(
                        onClick = {
                            var effectiveRequired = requiredPersons
                            var preValidationError: String? = null
                            if (selectedGender == "Both") {
                                if (boysCount.isBlank() || girlsCount.isBlank()) {
                                    preValidationError = "Please specify both boys and girls counts."
                                } else if (boysCount.toIntOrNull() == null || girlsCount.toIntOrNull() == null) {
                                    preValidationError = "Boys and girls counts must be valid numbers."
                                } else if ((boysCount.toInt() + girlsCount.toInt()) <= 0) {
                                    preValidationError = "Total required persons must be greater than 0."
                                } else {
                                    effectiveRequired = (boysCount.toInt() + girlsCount.toInt()).toString()
                                }
                            }

                            val errorMessage = preValidationError ?: getValidationError(
                                jobTitle = jobTitle,
                                company = company,
                                contactNumber = contactNumber,
                                minSalary = minSalary,
                                maxSalary = maxSalary,
                                location = location,
                                selectedWorkType = selectedWorkType,
                                selectedWorkTime = selectedWorkTime,
                                selectedFood = selectedFood,
                                selectedTransport = selectedTransport,
                                requiredPersons = effectiveRequired,
                                currentLatLng = currentLatLng
                            )

                            if (errorMessage == null) {
                                validationError = null
                                val job = Job(
                                    title = jobTitle,
                                    description = description,
                                    company = company,
                                    contactNumber = contactNumber,
                                    location = location,
                                    locationLat = currentLatLng?.latitude ?: 0.0,
                                    locationLng = currentLatLng?.longitude ?: 0.0,
                                    minSalary = minSalary.toIntOrNull() ?: 0,
                                    maxSalary = maxSalary.toIntOrNull() ?: 0,
                                    workType = selectedWorkType,
                                    food = selectedFood,
                                    transport = selectedTransport,
                                    workTime = selectedWorkTime,
                                    requiredPersons = effectiveRequired.toIntOrNull() ?: 1,
                                    genderPreference = selectedGender,
                                    ageLimit = ageLimit.takeIf { it.isNotBlank() },
                                    boysCount = if (selectedGender == "Both") boysCount.toIntOrNull() else null,
                                    girlsCount = if (selectedGender == "Both") girlsCount.toIntOrNull() else null,
                                    ownerName = authState.currentUserData?.fullName ?: "",
                                    ownerPhone = contactNumber
                                )

                                viewModel.createJob(
                                    job = job,
                                    onSuccess = {
                                        jobPostedSuccessfully = true
                                    }
                                )
                            } else {
                                validationError = errorMessage
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(8.dp, RoundedCornerShape(18.dp)),
                        enabled = !jobState.isLoading,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(PrimaryBlue, LightBlue, AccentPurple)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (jobState.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(26.dp),
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.White
                                    )
                                    Text(
                                        "Post Job",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBackPressed,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedText)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Cancel",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        OutlinedButton(
                            onClick = clearForm,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Clear",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun ErrorCard(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF2F2)
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ErrorRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = error,
                color = ErrorRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun JobInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                leadingIcon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MutedText, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color(0xFFF7FAFC),
                unfocusedContainerColor = Color(0xFFF7FAFC),
                cursorColor = PrimaryBlue
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
            singleLine = true
        )
    }
}

private fun getValidationError(
    jobTitle: String,
    company: String,
    contactNumber: String,
    minSalary: String,
    maxSalary: String,
    location: String,
    selectedWorkType: String,
    selectedWorkTime: String,
    selectedFood: String,
    selectedTransport: String,
    requiredPersons: String,
    currentLatLng: LatLng?
): String? {
    return when {
        jobTitle.isBlank() -> "Job title cannot be empty."
        company.isBlank() -> "Company name cannot be empty."
        contactNumber.isBlank() -> "Contact number cannot be empty."
        minSalary.isBlank() -> "Minimum salary cannot be empty."
        maxSalary.isBlank() -> "Maximum salary cannot be empty."
        (minSalary.toIntOrNull() ?: 0) > (maxSalary.toIntOrNull() ?: Int.MAX_VALUE) -> "Min salary cannot be greater than max salary."
        location.isBlank() || currentLatLng == null -> "Please select a location from the map."
        selectedWorkType == "Select work type" -> "Please select a work type."
        selectedWorkTime == "Select work time" -> "Please select a work time."
        selectedFood == "Select food availability" -> "Please select food availability."
        selectedTransport == "Select transport" -> "Please select transport availability."
        requiredPersons.isBlank() -> "Please specify the number of required persons."
        requiredPersons.toIntOrNull() == null || (requiredPersons.toIntOrNull() ?: 0) <= 0 -> "Required persons must be a valid number greater than 0."
        else -> null
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
    icon: ImageVector
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .menuAnchor(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF7FAFC),
                    unfocusedContainerColor = Color(0xFFF7FAFC)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
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

@Composable
private fun LocationInputField(location: String, onOpenMapPicker: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Location",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = location,
            onValueChange = {},
            placeholder = {
                Text(
                    "Tap map icon to select location",
                    color = MutedText,
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onOpenMapPicker,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.Default.Map,
                        contentDescription = "Pick from map",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            enabled = false,
            readOnly = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color(0xFFF7FAFC),
                unfocusedContainerColor = Color(0xFFF7FAFC),
                disabledTextColor = DarkText,
                disabledBorderColor = Color(0xFFE2E8F0),
                disabledContainerColor = Color(0xFFF7FAFC)
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
            singleLine = true
        )
        if (location.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Location successfully selected",
                        fontSize = 13.sp,
                        color = SuccessGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}