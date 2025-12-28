package com.example.jobnest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobnest.auth.ForgotPasswordScreen
import com.example.jobnest.auth.LoginScreen
import com.example.jobnest.auth.SignUpScreen
import com.example.jobnest.auth.SplashScreen
import com.example.jobnest.main.MainScreen
import com.example.jobnest.ui.theme.JobnestTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configure Firestore for offline persistence
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        
        enableEdgeToEdge()
        setContent {
            JobnestTheme {
                // Removed Scaffold to eliminate padding and white space
                JobNestApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun JobNestApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(onSplashComplete = {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                onSignUpClicked = { navController.navigate("signup") },
                onLoginSuccess = { navController.navigate("main") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignInClicked = { navController.navigate("login") },
                onSignUpSuccess = { navController.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }}
            )
        }
        composable("main") {
            MainScreen()
        }
    }
}