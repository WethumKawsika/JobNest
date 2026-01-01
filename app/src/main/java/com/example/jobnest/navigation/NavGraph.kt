package com.example.jobnest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobnest.ui.auth.CreateAccountScreen
import com.example.jobnest.main.screens.HomeScreen
import com.example.jobnest.viewmodel.AuthViewModel

@Composable
fun AppNavGraph(authViewModel: AuthViewModel) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "create_account"
    ) {

        composable("create_account") {
            CreateAccountScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("home") {
            HomeScreen()
        }
    }
}
