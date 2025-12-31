package com.example.jobnest.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jobnest.main.owner.MyJobsScreen
import com.example.jobnest.main.screens.HomeScreen
import com.example.jobnest.main.screens.ProfileScreen
import com.example.jobnest.main.screens.SavedScreen
import com.example.jobnest.main.screens.SearchScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    isOwner: Boolean,
    onNavigateToEditProfile: () -> Unit,
    onLogout: () -> Unit,
) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(onSwitchView = { /* Handled by NavController */ })
        }
        composable(BottomNavItem.Search.route) {
            SearchScreen(onBackClick = { navController.popBackStack() }, onSwitchView = { })
        }
        composable(BottomNavItem.Saved.route) {
            SavedScreen(onSwitchView = { })
        }
        composable(BottomNavItem.MyJobs.route) {
            MyJobsScreen(onSwitchView = { })
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                onSwitchView = { },
                isOwnerView = isOwner,
                onNavigateToEditProfile = onNavigateToEditProfile,
                onLogout = onLogout
            )
        }
    }
}