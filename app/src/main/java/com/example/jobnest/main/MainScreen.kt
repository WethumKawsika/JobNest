@file:Suppress("unused", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "UNUSED_IMPORT")

package com.example.jobnest.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
import androidx.navigation.compose.*
import com.example.jobnest.main.owner.MyJobsScreen
import com.example.jobnest.main.screens.HomeScreen
import com.example.jobnest.main.screens.ProfileScreen
import com.example.jobnest.main.screens.SavedScreen
import com.example.jobnest.main.screens.SearchScreen
import com.example.jobnest.viewmodel.AuthViewModel
import com.example.jobnest.viewmodel.JobViewModel

/* ---------------- COLORS ---------------- */

private val PrimaryBlue = Color(0xFF2E5BFF)
private val AccentPurple = Color(0xFF764BA2)
private val AccentGreen = Color(0xFF10B981)
private val InactiveGray = Color(0xFF9CA3AF)

/* ---------------- NAV ITEMS ---------------- */

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Search : BottomNavItem("search", Icons.Default.Search, "Search")
    object Saved : BottomNavItem("saved", Icons.Default.Favorite, "Saved")
    object MyJobs : BottomNavItem("my_jobs", Icons.AutoMirrored.Filled.List, "My Jobs")
    object PostJob : BottomNavItem("post_job", Icons.Default.Add, "Post")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

/* ---------------- MAIN SCREEN ---------------- */

@Composable
fun MainScreen(
    initialUserType: String? = null,
    onNavigateToPostJob: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onLogout: () -> Unit,
    refreshTrigger: Int = 0,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    val shouldShowOwnerView = remember(initialUserType, authState.currentUserData?.userType) {
        initialUserType == "owner" || authState.currentUserData?.userType == "owner"
    }

    var currentView by remember(shouldShowOwnerView) {
        mutableStateOf(if (shouldShowOwnerView) "owner" else "student")
    }
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            PremiumNavigationBar(
                currentView = currentView,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateToPostJob = onNavigateToPostJob
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentView) {
                "student" -> {
                    when (selectedTab) {
                        0 -> HomeScreen(
                            onSwitchView = {
                                currentView = "owner"
                                selectedTab = 0
                            }
                        )
                        1 -> SavedScreen(
                            onSwitchView = {
                                currentView = "owner"
                                selectedTab = 0
                            }
                        )
                        2 -> SearchScreen(
                            onBackClick = { selectedTab = 0 },
                            onSwitchView = {
                                currentView = "owner"
                                selectedTab = 0
                            }
                        )
                        3 -> ProfileScreen(
                            onSwitchView = {
                                currentView = "owner"
                                selectedTab = 0
                            },
                            isOwnerView = false,
                            onNavigateToEditProfile = onNavigateToEditProfile,
                            onLogout = onLogout
                        )
                    }
                }
                "owner" -> {
                    when (selectedTab) {
                        0 -> {
                            MyJobsScreen(
                                onSwitchView = {
                                    currentView = "student"
                                    selectedTab = 0
                                },
                                refreshTrigger = refreshTrigger
                            )
                        }
                        1 -> ProfileScreen(
                            onSwitchView = {
                                currentView = "student"
                                selectedTab = 0
                            },
                            isOwnerView = true,
                            onNavigateToEditProfile = onNavigateToEditProfile,
                            onLogout = onLogout
                        )
                    }
                }
            }
        }
    }
}

/* ---------------- PREMIUM NAVIGATION BAR ---------------- */

@Composable
fun PremiumNavigationBar(
    currentView: String,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToPostJob: () -> Unit
) {
    // New polished container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pill background with subtle shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(32.dp))
        )

        // Row of nav items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentView == "student") {
                PremiumNavItem(icon = Icons.Default.Home, label = "Home", selected = selectedTab == 0, onClick = { onTabSelected(0) })
                PremiumNavItem(icon = Icons.Default.Bookmark, label = "Saved", selected = selectedTab == 1, onClick = { onTabSelected(1) })

                // Small spacer where center FAB would be in owner view
                Spacer(modifier = Modifier.width(56.dp))

                PremiumNavItem(icon = Icons.Default.Search, label = "Search", selected = selectedTab == 2, onClick = { onTabSelected(2) })
                PremiumNavItem(icon = Icons.Default.Person, label = "Profile", selected = selectedTab == 3, onClick = { onTabSelected(3) })
            } else {
                PremiumNavItem(icon = Icons.Default.Work, label = "My Jobs", selected = selectedTab == 0, onClick = { onTabSelected(0) })

                // Center space for FAB
                Spacer(modifier = Modifier.width(12.dp))

                PremiumNavItem(icon = Icons.Default.Person, label = "Profile", selected = selectedTab == 1, onClick = { onTabSelected(1) })
            }
        }

        // Floating center action (owner only) - elevated and larger
        if (currentView != "student") {
            Box(modifier = Modifier.align(Alignment.TopCenter).offset(y = (-18).dp)) {
                FloatingActionButton(icon = Icons.Default.Add, onClick = onNavigateToPostJob)
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
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val iconSize by animateDpAsState(
        targetValue = if (selected) 28.dp else 22.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconSize"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(onClick = onClick, indication = null, interactionSource = remember { MutableInteractionSource() })
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon background circle when active
        // Apply brush background when selected, otherwise transparent color
        val boxModifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .let { base ->
                if (selected) base.background(brush = Brush.linearGradient(listOf(PrimaryBlue, AccentPurple)))
                else base.background(color = Color.Transparent)
            }

        Box(
            modifier = boxModifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = if (selected) Color.White else InactiveGray, modifier = Modifier.size(iconSize))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Animated label visibility
        AnimatedVisibility(visible = selected) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
        }

        // Active indicator dot
        if (selected) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
        }
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

    Box(
        modifier = Modifier
            .scale(scale)
            .size(56.dp)
            .offset(y = (-8).dp)
            .clip(CircleShape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryBlue, AccentPurple)
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Post Job",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

/* ---------------- GLASS FLOATING NAV BAR (Alternative Style) ---------------- */

@Composable
fun GlassFloatingNavBar(
    items: List<BottomNavItem>,
    navController: NavHostController,
    onNavigateToPostJob: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.85f)
            ),
            elevation = CardDefaults.cardElevation(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == item.route } == true

                    GlassNavItem(
                        item = item,
                        selected = selected
                    ) {
                        if (item.route == BottomNavItem.PostJob.route) {
                            onNavigateToPostJob()
                        } else {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- GLASS NAV ITEM ---------------- */

@Composable
fun GlassNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val lift by animateDpAsState(
        targetValue = if (selected) (-10).dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "lift"
    )

    Column(
        modifier = Modifier
            .offset(y = lift)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    brush = if (selected)
                        Brush.linearGradient(
                            colors = listOf(PrimaryBlue, AccentPurple)
                        )
                    else
                        Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color.Transparent)
                        )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = if (selected) Color.White else InactiveGray,
                modifier = Modifier.size(if (selected) 26.dp else 24.dp)
            )
        }

        if (selected) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlue
            )
        }
    }
}
