package com.example.jobnest.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobnest.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    onSendClicked: (String) -> Unit = {},
    onBackToLoginClicked: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    // Reset success state when error changes
    LaunchedEffect(authState.error) {
        if (authState.error != null) {
            showSuccess = false
        }
    }

    // Function to validate email
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2),
                        Color(0xFF667EEA)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Forgot Password",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter your email address and we'll send you a link to reset your password.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Input Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                    viewModel.clearError()
                },
                placeholder = { Text("Enter your email", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = Color(0xFF2E5BFF)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E5BFF),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF7FAFC),
                    unfocusedContainerColor = Color(0xFFF7FAFC),
                    cursorColor = Color(0xFF2E5BFF)
                ),
                isError = emailError.isNotEmpty() || authState.error != null,
                singleLine = true
            )

            // Email validation error
            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = Color(0xFFFF6B6B),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Firebase error message
            authState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF6B6B).copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when {
                            error.contains("no user record", ignoreCase = true) ->
                                "No account found with this email address"
                            error.contains("invalid-email", ignoreCase = true) ->
                                "Please enter a valid email address"
                            error.contains("network", ignoreCase = true) ->
                                "Network error. Please check your connection"
                            else -> error
                        },
                        color = Color(0xFFFF6B6B),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Success message
            if (showSuccess && authState.error == null && !authState.isLoading) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✓ Email Sent Successfully!",
                            color = Color(0xFF4CAF50),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Check your inbox for the password reset link",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Send Reset Link Button
            Button(
                onClick = {
                    when {
                        email.isBlank() -> {
                            emailError = "Email is required"
                        }
                        !isValidEmail(email) -> {
                            emailError = "Please enter a valid email address"
                        }
                        else -> {
                            emailError = ""
                            showSuccess = false
                            viewModel.resetPassword(
                                email = email,
                                onSuccess = {
                                    showSuccess = true
                                },
                                onError = { error ->
                                    showSuccess = false
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !authState.isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (authState.isLoading) {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF2E5BFF).copy(alpha = 0.6f),
                                        Color(0xFF667EEA).copy(alpha = 0.6f)
                                    )
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF2E5BFF), Color(0xFF667EEA))
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (authState.isLoading) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Sending...",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = "Send Reset Link",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Back to Login Button
            TextButton(
                onClick = onBackToLoginClicked
            ) {
                Text(
                    text = "← Back to Login",
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            // Auto-navigate after success (optional)
            LaunchedEffect(showSuccess) {
                if (showSuccess && authState.error == null && !authState.isLoading) {
                    kotlinx.coroutines.delay(3000)
                    onBackToLoginClicked()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen()
}