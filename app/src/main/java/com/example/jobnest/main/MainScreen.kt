package com.example.jobnest.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jobnest.main.owner.MyJobsScreen
import com.example.jobnest.main.owner.PostJobScreen
import com.example.jobnest.main.screens.HomeScreen
import com.example.jobnest.main.screens.ProfileScreen
import com.example.jobnest.main.screens.SavedScreen
import com.example.jobnest.main.screens.SearchScreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    // Student items
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Search : BottomNavItem("search", Icons.Default.Search, "Search")
    object Saved : BottomNavItem("saved", Icons.Default.Favorite, "Saved")

    // Owner items
    object MyJobs : BottomNavItem("my_jobs", Icons.AutoMirrored.Filled.List, "My Jobs")
    object PostJob : BottomNavItem("post_job", Icons.Default.Add, "Post Job")

    // Shared
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@Composable
fun MainScreen() {
    var isOwnerView by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    val studentItems = listOf(BottomNavItem.Home, BottomNavItem.Search, BottomNavItem.Saved, BottomNavItem.Profile)
    val ownerItems = listOf(BottomNavItem.MyJobs, BottomNavItem.PostJob, BottomNavItem.Profile)

    Scaffold(
        bottomBar = {
            val currentItems = if (isOwnerView) ownerItems else studentItems
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                currentItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
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
            navController,
            startDestination = BottomNavItem.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen(onSwitchView = onSwitchView) }
            composable(BottomNavItem.Search.route) { SearchScreen(onSwitchView = onSwitchView) }
            composable(BottomNavItem.Saved.route) { SavedScreen(onSwitchView = onSwitchView) }
            composable(BottomNavItem.MyJobs.route) { MyJobsScreen(onSwitchView = onSwitchView) }
            composable(BottomNavItem.PostJob.route) { PostJobScreen(onSwitchView = onSwitchView) }
            composable(BottomNavItem.Profile.route) { ProfileScreen(onSwitchView = onSwitchView, isOwnerView = isOwnerView) }
        }
    }
}
