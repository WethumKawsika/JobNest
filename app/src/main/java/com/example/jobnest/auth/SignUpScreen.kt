package com.example.jobnest.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobnest.R
import com.example.jobnest.ui.theme.LightBlue
import com.example.jobnest.ui.theme.PrimaryBlue
import com.example.jobnest.viewmodel.AuthViewModel

// Enhanced Color Palette
private val AccentPurple = Color(0xFF764BA2)
private val SoftWhite = Color(0xFFFAFAFA)
private val DarkText = Color(0xFF1A1A2E)
private val MutedText = Color(0xFF6B7280)
private val ErrorRed = Color(0xFFDC2626)
private val SuccessGreen = Color(0xFF10B981)

@Composable
fun SignUpScreen(
    onSignInClicked: () -> Unit = {},
    onStudentSignUpSuccess: () -> Unit = {},
    onJobCoordinatorSignUpSuccess: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Student") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.isAuthenticated, userType) {
        if (authState.isAuthenticated) {
            if (userType == "Student") {
                onStudentSignUpSuccess()
            } else {
                onJobCoordinatorSignUpSuccess()
            }
        }
    }

    // Animated floating effect
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    val floatX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatX"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(LightBlue, AccentPurple),
                    startY = 0f,
                    endY = 1500f
                )
            )
    ) {
        // Animated background circles
        Box(
            modifier = Modifier
                .offset(x = (-50).dp + floatX.dp, y = 100.dp + floatY.dp)
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .blur(40.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp - floatX.dp, y = (-30).dp - floatY.dp)
                .size(180.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.15f))
                .blur(45.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 50.dp + floatY.dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .blur(50.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // Logo with glow effect
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(20.dp, CircleShape, ambientColor = Color.White)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Join JobNest and start your journey",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Main Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(24.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(28.dp)) {

                    // Header inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp, 32.dp)
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
                                text = "Account Information",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "Fill in your details to get started",
                                fontSize = 13.sp,
                                color = MutedText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Full Name Field
                    EnhancedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Full Name",
                        placeholder = "Enter your full name",
                        icon = Icons.Default.Person,
                        isError = authState.error != null && fullName.isBlank()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    EnhancedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        placeholder = "your.email@example.com",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        isError = authState.error != null && email.isBlank()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    EnhancedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "Enter a strong password",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                        isError = authState.error != null && password.isBlank()
                    )

                    // Password strength indicator
                    if (password.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordStrengthIndicator(password)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password Field
                    EnhancedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        placeholder = "Re-enter your password",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        passwordVisible = confirmPasswordVisible,
                        onPasswordVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                        isError = authState.error != null &&
                                (confirmPassword.isBlank() || password != confirmPassword)
                    )

                    // Password match indicator
                    if (confirmPassword.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordMatchIndicator(password, confirmPassword)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // User Type Selection
                    Text(
                        text = "Select Account Type",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        UserTypeCard(
                            title = "Student",
                            icon = Icons.Default.School,
                            isSelected = userType == "Student",
                            onClick = { userType = "Student" },
                            modifier = Modifier.weight(1f)
                        )

                        UserTypeCard(
                            title = "Job Coordinator",
                            icon = Icons.Default.Work,
                            isSelected = userType == "Job Coordinator",
                            onClick = { userType = "Job Coordinator" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Error Message
                    authState.error?.let { error ->
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFEF2F2)
                            ),
                            shape = RoundedCornerShape(14.dp)
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
                                        imageVector = Icons.Default.Warning,
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

                    Spacer(modifier = Modifier.height(28.dp))

                    // Create Account Button
                    Button(
                        onClick = {
                            when {
                                fullName.isBlank() -> {}
                                email.isBlank() -> {}
                                password.isBlank() -> {}
                                password != confirmPassword -> {}
                                password.length < 6 -> {}
                                else -> {
                                    viewModel.signUpWithEmail(
                                        email = email,
                                        password = password,
                                        fullName = fullName,
                                        phoneNumber = "",
                                        address = "",
                                        userType = userType
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        enabled = !authState.isLoading &&
                                fullName.isNotBlank() &&
                                email.isNotBlank() &&
                                password.isNotBlank() &&
                                confirmPassword.isNotBlank() &&
                                password == confirmPassword &&
                                password.length >= 6,
                        shape = RoundedCornerShape(16.dp),
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
                            if (authState.isLoading) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color.White,
                                        strokeWidth = 2.5.dp
                                    )
                                    Text(
                                        "Creating Account...",
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
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
                                        "Create Account",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Sign In Link
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Already have an account?",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                TextButton(onClick = onSignInClicked) {
                    Text(
                        "Sign In",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: () -> Unit = {},
    isError: Boolean = false
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MutedText, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) ErrorRed else PrimaryBlue,
                unfocusedBorderColor = if (isError) ErrorRed.copy(alpha = 0.5f) else Color(0xFFE2E8F0),
                focusedContainerColor = Color(0xFFF7FAFC),
                unfocusedContainerColor = Color(0xFFF7FAFC),
                cursorColor = PrimaryBlue,
                errorBorderColor = ErrorRed
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onPasswordVisibilityToggle) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = MutedText
                        )
                    }
                }
            } else null,
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
            singleLine = true,
            isError = isError
        )
    }
}

@Composable
fun UserTypeCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else Color(0xFFF7FAFC)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = if (isSelected) PrimaryBlue else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) PrimaryBlue else Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MutedText,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) PrimaryBlue else DarkText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = calculatePasswordStrength(password)
    val color = when {
        strength < 2 -> ErrorRed
        strength < 4 -> Color(0xFFF59E0B)
        else -> SuccessGreen
    }
    val label = when {
        strength < 2 -> "Weak"
        strength < 4 -> "Medium"
        else -> "Strong"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index < strength) color else Color(0xFFE2E8F0))
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
fun PasswordMatchIndicator(password: String, confirmPassword: String) {
    val matches = password == confirmPassword
    val color = if (matches) SuccessGreen else ErrorRed
    val label = if (matches) "Passwords match" else "Passwords don't match"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            if (matches) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

fun calculatePasswordStrength(password: String): Int {
    var strength = 0
    if (password.length >= 6) strength++
    if (password.length >= 10) strength++
    if (password.any { it.isUpperCase() }) strength++
    if (password.any { it.isDigit() }) strength++
    if (password.any { !it.isLetterOrDigit() }) strength++
    return strength
}