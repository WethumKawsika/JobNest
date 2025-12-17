package com.example.jobnest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobnest.auth.LoginScreen
import com.example.jobnest.auth.SignUpScreen
import com.example.jobnest.main.MainScreen
import com.example.jobnest.ui.theme.JobnestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobnestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JobNestApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun JobNestApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login") {
            LoginScreen(
                onSignUpClicked = { navController.navigate("signup") },
                onLoginSuccess = { navController.navigate("main") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignInClicked = { navController.navigate("login") }
            )
        }
        composable("main") {
            MainScreen()
        }
    }
}
