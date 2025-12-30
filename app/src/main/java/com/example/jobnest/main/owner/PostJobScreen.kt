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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    onBackPressed: () -> Unit = {},
    onPostJob: () -> Unit = {},
    onOpenMapPicker: () -> Unit = {},
    selectedAddress: String = "",
    selectedLatLng: LatLng? = null,
    viewModel: JobViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var jobTitle by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
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
    var selectedGender by remember { mutableStateOf("Any") }

    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val jobState by viewModel.jobState.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    // Function to clear all form fields
    val clearForm = {
        jobTitle = ""
        company = ""
        // Don't clear the contact number as it might be pre-filled
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
        selectedGender = "Any"
        validationError = null
    }

    // Track if we just posted a job
    var justPostedJob by remember { mutableStateOf(false) }

    // Update location when selectedAddress changes
    LaunchedEffect(selectedAddress) {
        if (selectedAddress.isNotEmpty()) {
            location = selectedAddress
        }
    }

    // Update LatLng when it changes
    LaunchedEffect(selectedLatLng) {
        if (selectedLatLng != null) {
            currentLatLng = selectedLatLng
        }
    }

    // Pre-fill contact number from user profile
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

    // Handle successful job posting
    LaunchedEffect(jobState.isLoading, jobState.error, justPostedJob) {
        if (justPostedJob && !jobState.isLoading && jobState.error == null) {
            // Job posted successfully
            viewModel.loadJobs()
            justPostedJob = false
            onPostJob() // Navigate back
        }
    }

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
                            colors = listOf(LightBlue, AccentPurple)
                        )
                    )
            ) {
                // Floating circles
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
                        JobInputField("Job Title", jobTitle, { jobTitle = it }, Icons.Default.Work, "e.g., Delivery Driver")
                        JobInputField("Company Name", company, { company = it }, Icons.Default.Business, "e.g., Food Service Inc.")
                        JobInputField("📞 Contact Number", contactNumber, { contactNumber = it }, Icons.Default.Phone, "e.g., 0771234567", KeyboardType.Phone)

                        // Salary Range
                        Column {
                            Text("💰 Salary Range (Rs./day)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748), modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = minSalary,
                                    onValueChange = { minSalary = it },
                                    placeholder = { Text("Min", fontSize = 14.sp, color = Color.Gray) },
                                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp)) },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color(0xFFE2E8F0), focusedContainerColor = Color(0xFFF7FAFC), unfocusedContainerColor = Color(0xFFF7FAFC), cursorColor = PrimaryBlue),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = maxSalary,
                                    onValueChange = { maxSalary = it },
                                    placeholder = { Text("Max", fontSize = 14.sp, color = Color.Gray) },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color(0xFFE2E8F0), focusedContainerColor = Color(0xFFF7FAFC), unfocusedContainerColor = Color(0xFFF7FAFC), cursorColor = PrimaryBlue),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
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
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("👨‍💼 Gender", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748), modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
                                ExposedDropdownMenuBox(expanded = genderExpanded, onExpandedChange = { genderExpanded = !genderExpanded }) {
                                    OutlinedTextField(
                                        value = selectedGender,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                                        modifier = Modifier.fillMaxWidth().height(56.dp).menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color(0xFFE2E8F0), focusedContainerColor = Color(0xFFF7FAFC), unfocusedContainerColor = Color(0xFFF7FAFC)),
                                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                                    )
                                    ExposedDropdownMenu(expanded = genderExpanded, onDismissRequest = { genderExpanded = false }) {
                                        genderOptions.forEach { gender ->
                                            DropdownMenuItem(text = { Text(gender, fontSize = 13.sp) }, onClick = { selectedGender = gender; genderExpanded = false })
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Count", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748), modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
                                OutlinedTextField(
                                    value = requiredPersons,
                                    onValueChange = { requiredPersons = it },
                                    placeholder = { Text("e.g., 2", fontSize = 14.sp, color = Color.Gray) },
                                    leadingIcon = { Icon(Icons.Default.Group, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp)) },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color(0xFFE2E8F0), focusedContainerColor = Color(0xFFF7FAFC), unfocusedContainerColor = Color(0xFFF7FAFC), cursorColor = PrimaryBlue),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                    singleLine = true
                                )
                            }
                        }

                        JobInputField("🎂 Age Limit (Optional)", ageLimit, { ageLimit = it }, Icons.Default.Cake, "e.g., 18-35", KeyboardType.Text)

                        // Description
                        Column {
                            Text("Job Description", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748), modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = { Text("Describe the job requirements and responsibilities", color = Color.Gray, fontSize = 14.sp) },
                                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp)) },
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color(0xFFE2E8F0), focusedContainerColor = Color(0xFFF7FAFC), unfocusedContainerColor = Color(0xFFF7FAFC), cursorColor = PrimaryBlue),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                maxLines = 5
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    validationError?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFEE2E2)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = error,
                                    color = Color(0xFFDC2626),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    jobState.error?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFEE2E2)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = error,
                                    color = Color(0xFFDC2626),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Post Button
                    Button(
                        onClick = {
                            if (validateFields(jobTitle, company, contactNumber, minSalary, maxSalary, location, selectedWorkType, selectedWorkTime, selectedFood, selectedTransport, requiredPersons)) {
                                validationError = null
                                val job = Job(
                                    title = jobTitle,
                                    description = description,
                                    company = company,
                                    contactNumber = contactNumber,
                                    location = location,
                                    minSalary = minSalary.toIntOrNull() ?: 0,
                                    maxSalary = maxSalary.toIntOrNull() ?: 0,
                                    workType = selectedWorkType,
                                    food = selectedFood,
                                    transport = selectedTransport,
                                    workTime = selectedWorkTime,
                                    requiredPersons = requiredPersons.toIntOrNull() ?: 1,
                                    genderPreference = selectedGender,
                                    ageLimit = ageLimit.takeIf { it.isNotBlank() }
                                )

                                // Set flag that we're posting a job
                                justPostedJob = true

                                // Create the job
                                viewModel.createJob(job)
                            } else {
                                validationError = "Please fill in all required fields."
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !jobState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
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
                            if (jobState.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                        tint = Color.White
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
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = onBackPressed,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Cancel",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B7280)
                            )
                        }
                        TextButton(
                            onClick = clearForm,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Clear",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue
                            )
                        }
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
    leadingIcon: ImageVector,
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
            modifier = Modifier.fillMaxWidth().height(56.dp),
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
    icon: ImageVector
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

@Composable
private fun LocationInputField(location: String, onOpenMapPicker: () -> Unit) {
    Column {
        Text(
            "📍 Location",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = location,
            onValueChange = {},
            placeholder = {
                Text(
                    "Tap map icon to pick location",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(22.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onOpenMapPicker) {
                    Icon(
                        Icons.Default.Map,
                        contentDescription = "Pick from map",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = false,
            readOnly = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color(0xFFF7FAFC),
                unfocusedContainerColor = Color(0xFFF7FAFC),
                disabledTextColor = Color(0xFF2D3748),
                disabledBorderColor = Color(0xFFE2E8F0),
                disabledContainerColor = Color(0xFFF7FAFC)
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            singleLine = true
        )
        if (location.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Location selected",
                    fontSize = 12.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun validateFields(
    jobTitle: String,
    company: String,
    contactNumber: String,
    minSalary: String,
    maxSalary: String,
    location: String,
    workType: String,
    workTime: String,
    food: String,
    transport: String,
    requiredPersons: String
): Boolean {
    return jobTitle.isNotBlank() &&
            company.isNotBlank() &&
            contactNumber.isNotBlank() &&
            minSalary.isNotBlank() &&
            maxSalary.isNotBlank() &&
            location.isNotBlank() &&
            workType != "Select work type" &&
            workTime != "Select work time" &&
            food != "Select food availability" &&
            transport != "Select transport" &&
            requiredPersons.isNotBlank()
}
