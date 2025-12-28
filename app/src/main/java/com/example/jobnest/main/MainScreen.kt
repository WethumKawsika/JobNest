package com.example.jobnest.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.jobnest.main.owner.MyJobsScreen
import com.example.jobnest.main.owner.PostJobScreen
import com.example.jobnest.main.screens.HomeScreen
import com.example.jobnest.main.screens.ProfileScreen
import com.example.jobnest.main.screens.SavedScreen
import com.example.jobnest.main.screens.SearchScreen
import com.example.jobnest.main.screens.common.EditProfileScreen

/* ---------------- COLORS ---------------- */

private val PrimaryBlue = Color(0xFF2E5BFF)
private val AccentPurple = Color(0xFF764BA2)
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

const val EDIT_PROFILE_ROUTE = "edit_profile"

/* ---------------- MAIN SCREEN ---------------- */

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var isOwnerView by remember { mutableStateOf(false) }

    val studentItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Saved,
        BottomNavItem.Profile
    )

    val ownerItems = listOf(
        BottomNavItem.MyJobs,
        BottomNavItem.PostJob,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            GlassFloatingNavBar(
                items = if (isOwnerView) ownerItems else studentItems,
                navController = navController
            )
        }
    ) { padding ->
        val onSwitchView = {
            isOwnerView = !isOwnerView
            val newStartRoute = if (isOwnerView) BottomNavItem.MyJobs.route else BottomNavItem.Home.route
            navController.navigate(newStartRoute) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen(onSwitchView = onSwitchView) }
            composable(BottomNavItem.Search.route) { SearchScreen() }
            composable(BottomNavItem.Saved.route) { SavedScreen(onSwitchView = onSwitchView) }
            composable(BottomNavItem.MyJobs.route) { MyJobsScreen(onSwitchView = onSwitchView, onPostJob = { navController.navigate(BottomNavItem.PostJob.route) }) }
            composable(BottomNavItem.PostJob.route) { PostJobScreen(onBackPressed = { navController.navigateUp() }, onPostJob = { navController.navigateUp() }) }
            composable(BottomNavItem.Profile.route) { ProfileScreen(onSwitchView = onSwitchView, isOwnerView = isOwnerView, navController = navController) }
            composable(EDIT_PROFILE_ROUTE) { EditProfileScreen(navController = navController, onSaveProfile = { navController.navigateUp() }) }
        }
    }
}

/* ---------------- GLASS FLOATING NAV BAR ---------------- */

@Composable
fun GlassFloatingNavBar(
    items: List<BottomNavItem>,
    navController: NavHostController
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

/* ---------------- NAV ITEM ---------------- */

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
                )
            ,
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
