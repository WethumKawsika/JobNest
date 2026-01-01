package com.example.jobnest.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobnest.R
import com.example.jobnest.viewmodel.AuthViewModel

private val PrimaryBlue = Color(0xFF2E5BFF)
private val SecondaryPurple = Color(0xFF8E44AD)
private val AccentPink = Color(0xFFFF6B9D)
private val LightBlue = Color(0xFF667EEA)

@Composable
fun LoginScreen(
    onSignUpClicked: () -> Unit,
    onStudentLoginSuccess: () -> Unit,
    onOwnerLoginSuccess: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onGoogleSignInClicked: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val authState by viewModel.authState.collectAsState()

    // Handle navigation after successful authentication
    LaunchedEffect(authState.isAuthenticated, authState.currentUserData?.userType) {
        if (authState.isAuthenticated) {
            val userData = authState.currentUserData
            if (userData == null) {
                // Authenticated but profile not fetched yet - allow entry (default to student)
                onStudentLoginSuccess()
            } else {
                val userType = userData.userType
                if (userType == null) {
                    // New Google user without role - show dialog
                    showRoleDialog = true
                } else {
                    // Existing user with role - navigate directly
                    when (userType) {
                        "student" -> onStudentLoginSuccess()
                        "owner" -> onOwnerLoginSuccess()
                        else -> onStudentLoginSuccess()
                    }
                }
            }
        }
    }

    // Role selection dialog for new Google users
    if (showRoleDialog) {
        RoleSelectionDialog(
            onDismiss = {
                showRoleDialog = false
                viewModel.signOut()
            },
            onRoleSelected = { role ->
                showRoleDialog = false
                viewModel.updateUserRole(role)

                // Navigate based on selected role
                when (role) {
                    "student" -> onStudentLoginSuccess()
                    "owner" -> onOwnerLoginSuccess()
                }
            }
        )
    }

    // Animated floating effect
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    val logoPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

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
            )
    ) {
        // Floating decorative circles
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = 100.dp + floatY.dp)
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .blur(50.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp - floatY.dp)
                .size(250.dp)
                .clip(CircleShape)
                .background(AccentPink.copy(alpha = 0.2f))
                .blur(60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Image(
                painter = painterResource(id = R.drawable.jobnest_logo),
                contentDescription = "JobNest Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoPulse),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to explore amazing opportunities",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Google Sign In Button
                    OutlinedButton(
                        onClick = onGoogleSignInClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF2D3748)
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google_logo),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Continue with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D3748)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // OR Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE2E8F0),
                            thickness = 1.dp
                        )
                        Text(
                            text = "OR",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280)
                        )
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE2E8F0),
                            thickness = 1.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Email Field
                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Enter your email", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF7FAFC),
                            unfocusedContainerColor = Color(0xFFF7FAFC),
                            cursorColor = PrimaryBlue,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter your password", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color.Gray
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF7FAFC),
                            unfocusedContainerColor = Color(0xFFF7FAFC),
                            cursorColor = PrimaryBlue,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onForgotPasswordClicked,
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text(
                                "Forgot password?",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Error message
                    authState.error?.let { error ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sign In Button
                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                viewModel.signInWithEmail(email, password)
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
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp
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
                            Text(
                                if (authState.isLoading) "Signing In..." else "Sign In",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign Up
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Don't have an account?",
                            color = Color(0xFF4A5568),
                            fontSize = 14.sp
                        )
                        TextButton(
                            onClick = onSignUpClicked,
                            contentPadding = PaddingValues(start = 4.dp)
                        ) {
                            Text(
                                "Sign Up",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}