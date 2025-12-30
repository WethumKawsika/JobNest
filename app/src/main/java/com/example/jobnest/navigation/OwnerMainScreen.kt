package com.example.jobnest.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobnest.main.screens.ProfileScreen
import com.example.jobnest.viewmodel.AuthViewModel
import com.example.jobnest.viewmodel.JobViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OwnerMainScreen(
    onLogout: () -> Unit,
    viewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val jobViewModel: JobViewModel = viewModel()
    var selectedItem by remember { mutableStateOf(0) }
    var refreshTrigger by remember { mutableStateOf(0) }

    val items = listOf(
        BottomNavItem("My Jobs", Icons.Filled.Work, "my_jobs"),
        BottomNavItem("Post Job", Icons.Filled.Add, "post_job"),
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
                                popUpTo("my_jobs") {
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
            startDestination = "my_jobs",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("my_jobs") {
                // Use the actual MyJobsScreen from com.example.jobnest.main.owner
                com.example.jobnest.main.owner.MyJobsScreen(
                    onSwitchView = { /* Owner view - no switching */ },
                    viewModel = jobViewModel,
                    refreshTrigger = refreshTrigger
                )
            }

            composable("post_job") {
                // Use the actual PostJobScreen from com.example.jobnest.main.owner
                com.example.jobnest.main.owner.PostJobScreen(
                    onBackPressed = {
                        // Navigate back to My Jobs when back button is clicked
                        selectedItem = 0
                        refreshTrigger++ // Trigger refresh of MyJobsScreen
                        navController.navigate("my_jobs") {
                            popUpTo("my_jobs") { inclusive = true }
                        }
                    },
                    onPostJob = {
                        // After successfully posting, navigate to My Jobs
                        selectedItem = 0
                        refreshTrigger++ // Trigger refresh of MyJobsScreen
                        navController.navigate("my_jobs") {
                            popUpTo("my_jobs") { inclusive = true }
                        }
                    },
                    onOpenMapPicker = {
                        // TODO: Implement map picker if needed
                    },
                    selectedAddress = "",
                    selectedLatLng = null,
                    viewModel = jobViewModel,
                    authViewModel = viewModel
                )
            }

            composable("profile") {
                ProfileScreen(
                    onSwitchView = { /* Owner view - no switching */ },
                    isOwnerView = true,
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