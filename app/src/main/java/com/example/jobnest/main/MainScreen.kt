package com.example.jobnest.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jobnest.viewmodel.AuthViewModel

/* ---------------- COLORS ---------------- */

private val PrimaryBlue = Color(0xFF2E5BFF)
private val AccentPurple = Color(0xFF764BA2)
private val InactiveGray = Color(0xFF9CA3AF)

/* ---------------- MAIN SCREEN ---------------- */

@Composable
fun MainScreen(
    onNavigateToPostJob: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val isOwner = authState.currentUserData?.userType == "owner"

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            PremiumNavigationBar(
                navController = navController,
                isOwner = isOwner,
                onNavigateToPostJob = onNavigateToPostJob
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            BottomNavGraph(
                navController = navController,
                isOwner = isOwner,
                onNavigateToEditProfile = onNavigateToEditProfile,
                onLogout = onLogout
            )
        }
    }
}

/* ---------------- PREMIUM NAVIGATION BAR ---------------- */

@Composable
fun PremiumNavigationBar(
    navController: NavHostController,
    isOwner: Boolean,
    onNavigateToPostJob: () -> Unit
) {
    val studentItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Saved,
        BottomNavItem.Search,
        BottomNavItem.Profile
    )

    val ownerItems = listOf(
        BottomNavItem.MyJobs,
        BottomNavItem.Profile
    )

    val items = if (isOwner) ownerItems else studentItems
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.95f),
                                Color.White.copy(alpha = 0.88f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isOwner) {
                    PremiumNavItem(
                        icon = Icons.Default.Work,
                        label = BottomNavItem.MyJobs.title,
                        selected = currentDestination?.hierarchy?.any { it.route == BottomNavItem.MyJobs.route } == true,
                        onClick = {
                            navController.navigate(BottomNavItem.MyJobs.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    FloatingActionButton(
                        icon = Icons.Default.Add,
                        onClick = onNavigateToPostJob
                    )
                    PremiumNavItem(
                        icon = BottomNavItem.Profile.icon,
                        label = BottomNavItem.Profile.title,
                        selected = currentDestination?.hierarchy?.any { it.route == BottomNavItem.Profile.route } == true,
                        onClick = {
                            navController.navigate(BottomNavItem.Profile.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                } else {
                    items.forEach { screen ->
                        val icon = if (screen.route == "saved") Icons.Default.Bookmark else screen.icon
                        PremiumNavItem(
                            icon = icon,
                            label = screen.title,
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/* ---------------- PREMIUM NAV ITEM ---------------- */

@Composable
fun PremiumNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val iconSize by animateDpAsState(
        targetValue = if (selected) 26.dp else 24.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconSize"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (selected) {
                        Brush.linearGradient(
                            colors = listOf(PrimaryBlue, AccentPurple)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color.Transparent)
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color.White else InactiveGray,
                modifier = Modifier.size(iconSize)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = if (selected) 12.sp else 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) PrimaryBlue else InactiveGray.copy(alpha = 0.7f)
        )
    }
}

/* ---------------- FLOATING ACTION BUTTON ---------------- */

@Composable
fun FloatingActionButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "fabScale"
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .scale(scale)
            .size(64.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF8E44AD), Color(0xFF2E5BFF))
                )
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Post Job",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}
