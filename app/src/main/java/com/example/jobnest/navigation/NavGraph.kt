package com.example.jobnest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobnest.auth.*
import com.example.jobnest.main.MainScreen
import com.example.jobnest.ui.auth.CreateAccountScreen
import com.example.jobnest.viewmodel.AuthViewModel

@Composable
fun AppNavGraph(authViewModel: AuthViewModel) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SPLASH_ROUTE
    ) {
        composable(SPLASH_ROUTE) {
            SplashScreen(
                onLoginNavigate = { 
                    navController.navigate(LOGIN_ROUTE) {
                        popUpTo(SPLASH_ROUTE) { inclusive = true }
                    }
                },
                onMainNavigate = {
                    navController.navigate(MAIN_ROUTE) {
                        popUpTo(SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(LOGIN_ROUTE) {
            LoginScreen(
                onSignUpClicked = { navController.navigate(CREATE_ACCOUNT_ROUTE) },
                onStudentLoginSuccess = { 
                    navController.navigate(MAIN_ROUTE) {
                        popUpTo(LOGIN_ROUTE) { inclusive = true }
                    }
                 },
                onOwnerLoginSuccess = { 
                    navController.navigate(MAIN_ROUTE) {
                        popUpTo(LOGIN_ROUTE) { inclusive = true }
                    }
                 },
                onForgotPasswordClicked = { navController.navigate(FORGOT_PASSWORD_ROUTE) },
                onGoogleSignInClicked = { /* Handle Google Sign In */ },
                viewModel = authViewModel
            )
        }

        composable(MAIN_ROUTE) {
            MainScreen(
                onNavigateToPostJob = { /* Navigate to Post Job */ },
                onNavigateToEditProfile = { /* Navigate to Edit Profile */ },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(LOGIN_ROUTE) {
                        popUpTo(MAIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(CREATE_ACCOUNT_ROUTE) {
            CreateAccountScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(FORGOT_PASSWORD_ROUTE) {
            ForgotPasswordScreen(
                onSendClicked = { navController.navigateUp() },
                onBackToLoginClicked = { navController.navigateUp() },
                viewModel = authViewModel
            )
        }
    }
}