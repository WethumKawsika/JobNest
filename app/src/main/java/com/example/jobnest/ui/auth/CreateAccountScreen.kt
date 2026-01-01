package com.example.jobnest.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobnest.viewmodel.AuthViewModel

@Composable
fun CreateAccountScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Student") }

    // ✅ NAVIGATION TRIGGER
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate("home") {
                popUpTo("create_account") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Create Account",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Button(
                onClick = { userType = "Student" },
                colors = ButtonDefaults.buttonColors(
                    if (userType == "Student") Color.Blue else Color.Gray
                )
            ) { Text("Student") }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { userType = "Job Owner" },
                colors = ButtonDefaults.buttonColors(
                    if (userType == "Job Owner") Color.Blue else Color.Gray
                )
            ) { Text("Job Owner") }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                authViewModel.signUpWithEmail(
                    email = email,
                    password = password,
                    fullName = fullName,
                    phoneNumber = "",
                    address = "",
                    userType = userType
                )

            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Create Account")
            }
        }

        authState.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red)
        }
    }
}
