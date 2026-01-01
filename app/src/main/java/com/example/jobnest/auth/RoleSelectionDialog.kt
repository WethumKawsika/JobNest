package com.example.jobnest.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)

@Composable
fun RoleSelectionDialog(
    onDismiss: () -> Unit,
    onRoleSelected: (String) -> Unit
) {
    var selectedRole by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Your Role",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Choose how you'd like to use JobNest",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Student Role Card
                RoleCard(
                    icon = Icons.Default.School,
                    title = "Student",
                    description = "Find part-time jobs and opportunities",
                    isSelected = selectedRole == "student",
                    onClick = { selectedRole = "student" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Job Coordinator Role Card
                RoleCard(
                    icon = Icons.Default.BusinessCenter,
                    title = "Job Coordinator",
                    description = "Post and manage job listings",
                    isSelected = selectedRole == "owner",
                    onClick = { selectedRole = "owner" }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Next Button
                Button(
                    onClick = {
                        selectedRole?.let { role ->
                            onRoleSelected(role)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedRole != null,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color(0xFFE2E8F0)
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
                                if (selectedRole != null) {
                                    Brush.horizontalGradient(
                                        colors = listOf(PrimaryBlue, LightBlue)
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0))
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Next",
                            color = if (selectedRole != null) Color.White else Color(0xFF9CA3AF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleCard(
    icon: ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) PrimaryBlue.copy(alpha = 0.08f) else Color(0xFFF8F9FD),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, PrimaryBlue)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        },
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        brush = if (isSelected) {
                            Brush.linearGradient(
                                colors = listOf(PrimaryBlue, LightBlue)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0))
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color(0xFF9CA3AF),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) PrimaryBlue else Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium
                )
            }

            // Selection Indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PrimaryBlue, LightBlue)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }
        }
    }
}