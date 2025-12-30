package com.example.jobnest.main.screens

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
import com.example.jobnest.viewmodel.AuthViewModel

// Premium Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val SoftBackground = Color(0xFFF8F9FD)

@Composable
fun EditProfileScreen(
    viewModel: AuthViewModel, // Make sure this parameter exists
    onBackPressed: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val currentUser = authState.currentUserData

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Initialize fields with current user data
    LaunchedEffect(authState.currentUserData) {
        authState.currentUserData?.let { user ->
            fullName = user.fullName
            phoneNumber = user.phoneNumber
            address = user.address
        }
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

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onBackPressed()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Success!", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
            text = { Text("Your profile has been updated successfully.", fontSize = 15.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBackPressed()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Done") }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Error", fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("OK") }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Gradient
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
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

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Edit Profile",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Update your information",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Avatar with Gradient Ring
                    Box(
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(colors = listOf(PrimaryBlue, AccentPurple))
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(122.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .size(115.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            PrimaryBlue.copy(alpha = 0.2f),
                                            AccentPurple.copy(alpha = 0.2f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fullName.take(2).uppercase().ifEmpty { "JD" },
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-8).dp, y = (-8).dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Photo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Form Fields
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ProfileInputField(
                            label = "Email",
                            value = authState.currentUserData?.email ?: "",
                            onValueChange = {},
                            icon = Icons.Default.Email,
                            placeholder = "email@example.com",
                            enabled = false
                        )

                        ProfileInputField(
                            label = "Full Name",
                            value = fullName,
                            onValueChange = { fullName = it },
                            icon = Icons.Default.Person,
                            placeholder = "Enter your full name"
                        )

                        ProfileInputField(
                            label = "Phone Number",
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            icon = Icons.Default.Phone,
                            placeholder = "Enter phone number",
                            keyboardType = KeyboardType.Phone
                        )

                        ProfileInputField(
                            label = "Address",
                            value = address,
                            onValueChange = { address = it },
                            icon = Icons.Default.LocationOn,
                            placeholder = "Enter your address",
                            minLines = 3
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Save Button
                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                errorMessage = "Full name is required"
                                showErrorDialog = true
                                return@Button
                            }

                            if (phoneNumber.isBlank()) {
                                errorMessage = "Phone number is required"
                                showErrorDialog = true
                                return@Button
                            }

                            viewModel.updateProfile(
                                fullName = fullName,
                                phoneNumber = phoneNumber,
                                address = address,
                                onSuccess = { showSuccessDialog = true },
                                onError = { error ->
                                    errorMessage = error
                                    showErrorDialog = true
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !authState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(colors = listOf(PrimaryBlue, LightBlue))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (authState.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.White)
                                    Text("Save Changes", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = onBackPressed,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B7280))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    minLines: Int = 1
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
            leadingIcon = { Icon(icon, contentDescription = null, tint = if (enabled) PrimaryBlue else Color.Gray, modifier = Modifier.size(22.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .then(if (minLines > 1) Modifier.height((56 * minLines).dp) else Modifier.height(56.dp)),
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color(0xFFF7FAFC),
                unfocusedContainerColor = Color(0xFFF7FAFC),
                disabledContainerColor = Color(0xFFF3F4F6),
                disabledBorderColor = Color(0xFFE2E8F0),
                disabledTextColor = Color(0xFF6B7280),
                cursorColor = PrimaryBlue
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            minLines = minLines,
            maxLines = if (minLines > 1) minLines else 1
        )

        if (!enabled && label == "Email") {
            Spacer(modifier = Modifier.height(6.dp))
            Text("Email cannot be changed", fontSize = 12.sp, color = Color(0xFF9CA3AF), modifier = Modifier.padding(start = 4.dp))
        }
    }
}
