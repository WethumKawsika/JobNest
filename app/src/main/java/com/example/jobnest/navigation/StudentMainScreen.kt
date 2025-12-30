package com.example.jobnest.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobnest.main.screens.HomeScreen
import com.example.jobnest.main.screens.ProfileScreen
import com.example.jobnest.main.screens.SavedScreen
import com.example.jobnest.main.screens.SearchScreen
import com.example.jobnest.viewmodel.AuthViewModel
import com.example.jobnest.viewmodel.JobViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudentMainScreen(
    onLogout: () -> Unit,
    viewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val jobViewModel: JobViewModel = viewModel()
    var selectedItem by remember { mutableStateOf(0) }

    val items = listOf(
        BottomNavItem("Home", Icons.Filled.Home, "home"),
        BottomNavItem("Saved", Icons.Filled.Bookmark, "saved"),
        BottomNavItem("Search", Icons.Filled.Search, "search"),
        BottomNavItem("Profile", Icons.Filled.Person, "profile")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route) {
                                popUpTo("home") {
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
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(
                    onSwitchView = { /* Student view - no switching */ },
                    viewModel = jobViewModel
                )
            }

            composable("saved") {
                SavedScreen(
                    onSwitchView = { /* Student view - no switching */ },
                    viewModel = jobViewModel
                )
            }

            composable("search") {
                SearchScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSwitchView = { /* Student view - no switching */ },
                    viewModel = jobViewModel
                )
            }

            composable("profile") {
                ProfileScreen(
                    onSwitchView = { /* Student view - no switching */ },
                    isOwnerView = false,
                    onNavigateToEditProfile = {
                        // TODO: Navigate to edit profile
                    },
                    onLogout = onLogout,
                    viewModel = viewModel
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)