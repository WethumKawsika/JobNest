package com.example.jobnest.main.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Premium Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val SoftBackground = Color(0xFFF8F9FD)

@Composable
fun AboutScreen(
    onBackPressed: () -> Unit
) {
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
                            text = "About",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Learn more about JobNest",
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
                    // App Logo
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryBlue, AccentPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Work,
                            contentDescription = "JobNest Logo",
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "JobNest",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Version 1.0.0",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Description Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = PrimaryBlue.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your Gateway to Part-Time Opportunities",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "JobNest connects students with part-time job opportunities and helps employers find talented individuals for temporary positions. We make job hunting and hiring simple, fast, and efficient.",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Features Section
                    Text(
                        text = "Why Choose JobNest?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FeatureItem(
                        icon = Icons.Default.Search,
                        title = "Easy Job Discovery",
                        description = "Find relevant part-time jobs quickly with our smart search"
                    )

                    FeatureItem(
                        icon = Icons.Default.Map,
                        title = "Location-Based Search",
                        description = "Discover jobs near you with our map integration"
                    )

                    FeatureItem(
                        icon = Icons.Default.Star,
                        title = "Save Favorites",
                        description = "Bookmark interesting jobs and apply when you're ready"
                    )

                    FeatureItem(
                        icon = Icons.Default.Notifications,
                        title = "Real-Time Updates",
                        description = "Get notified about new opportunities instantly"
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // Links Section
                    Text(
                        text = "Resources",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AboutLinkItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        onClick = { /* TODO: Show terms */ }
                    )

                    AboutLinkItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        onClick = { /* TODO: Show privacy policy */ }
                    )

                    AboutLinkItem(
                        icon = Icons.Default.Gavel,
                        title = "Licenses",
                        onClick = { /* TODO: Show licenses */ }
                    )

                    AboutLinkItem(
                        icon = Icons.Default.Help,
                        title = "Help & Support",
                        onClick = { /* TODO: Show help */ }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // Contact Section
                    Text(
                        text = "Get in Touch",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ContactItem(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = "support@jobnest.com"
                    )

                    ContactItem(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = "+94 11 234 5678"
                    )

                    ContactItem(
                        icon = Icons.Default.Language,
                        label = "Website",
                        value = "www.jobnest.com"
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Social Media
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SocialMediaButton(icon = Icons.Default.Share, label = "Facebook")
                        Spacer(modifier = Modifier.width(16.dp))
                        SocialMediaButton(icon = Icons.Default.Tag, label = "Twitter")
                        Spacer(modifier = Modifier.width(16.dp))
                        SocialMediaButton(icon = Icons.Default.Camera, label = "Instagram")
                        Spacer(modifier = Modifier.width(16.dp))
                        SocialMediaButton(icon = Icons.Default.Business, label = "LinkedIn")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Footer
                    Text(
                        text = "Made with ❤️ in Sri Lanka",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "© 2025 JobNest. All rights reserved.",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7FAFC)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(colors = listOf(PrimaryBlue, LightBlue))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun AboutLinkItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7FAFC)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ContactItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7FAFC)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
            }
        }
    }
}

@Composable
private fun SocialMediaButton(icon: ImageVector, label: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(colors = listOf(PrimaryBlue, LightBlue))
            )
            .clickable { /* TODO: Open $label social media */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}