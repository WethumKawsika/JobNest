package com.example.jobnest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobnest.auth.*
import com.example.jobnest.main.owner.MapPickerScreen
import com.example.jobnest.main.owner.MyJobsScreen
import com.example.jobnest.main.owner.PostJobScreen
import com.example.jobnest.main.screens.*
import com.example.jobnest.ui.theme.JobnestTheme
import com.example.jobnest.viewmodel.AuthViewModel
import com.example.jobnest.viewmodel.JobViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainActivity : ComponentActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    // ---------------- GOOGLE SIGN IN ----------------
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val idToken = account.idToken!!
            authViewModel.signInWithGoogle(idToken)
        } catch (e: ApiException) {
            Log.w("MainActivity", "Google sign in failed", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ---------------- LOCATION PERMISSION ----------------
        if (!hasLocationPermission()) {
            requestLocationPermission()
        }

        // ---------------- FIRESTORE SETTINGS ----------------
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        // ---------------- GOOGLE SIGN IN CONFIG ----------------
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // ---------------- UI ----------------
        enableEdgeToEdge()
        setContent {
            JobnestTheme {
                JobNestApp(
                    modifier = Modifier.fillMaxSize(),
                    onGoogleSignInClicked = {
                        googleSignInLauncher.launch(
                            googleSignInClient.signInIntent
                        )
                    }
                )
            }
        }
    }

    // ---------------- LOCATION PERMISSION HELPERS ----------------
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}

// ===================================================================
// ========================== NAVIGATION ==============================
// ===================================================================

@Composable
fun JobNestApp(
    modifier: Modifier = Modifier,
    onGoogleSignInClicked: () -> Unit
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val jobViewModel: JobViewModel = viewModel()
    var jobPostRefreshTrigger by remember { mutableStateOf(0) }

    NavHost(navController, startDestination = "splash", modifier = modifier) {

        composable("splash") {
            SplashScreen {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        composable("login") {
            LoginScreen(
                onSignUpClicked = { navController.navigate("signup") },
                onStudentLoginSuccess = {
                    navController.navigate("main?userType=student") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onOwnerLoginSuccess = {
                    navController.navigate("main?userType=owner") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPasswordClicked = { navController.navigate("forgot_password") },
                onGoogleSignInClicked = onGoogleSignInClicked,
                viewModel = authViewModel
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignInClicked = { navController.navigate("login") },
                onSignUpSuccess = {
                    navController.navigate("main") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onSendClicked = { navController.navigateUp() },
                onBackToLoginClicked = { navController.navigateUp() }
            )
        }

        // ========== NEW SCREENS ==========
        composable("notifications") {
            NotificationsScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }



        composable("about") {
            AboutScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }
        // =================================

        composable("main?userType={userType}") { entry ->
            val userType = entry.arguments?.getString("userType")

            val shouldRefresh by entry.savedStateHandle
                .getStateFlow("should_refresh_jobs", false)
                .collectAsState()

            LaunchedEffect(shouldRefresh) {
                if (shouldRefresh) {
                    jobPostRefreshTrigger++
                    entry.savedStateHandle["should_refresh_jobs"] = false
                }
            }

            MainScreen(
                initialUserType = userType,
                jobViewModel = jobViewModel,
                refreshTrigger = jobPostRefreshTrigger,
                onNavigateToPostJob = { navController.navigate("post_job") },
                onNavigateToEditProfile = { navController.navigate("edit_profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSecurity = { navController.navigate("security") },
                onNavigateToAbout = { navController.navigate("about") },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        composable("edit_profile") {
            val backStackEntry = navController.currentBackStackEntry
            val savedStateHandle = backStackEntry?.savedStateHandle

            val address by savedStateHandle
                ?.getStateFlow("profile_address", "")
                ?.collectAsState() ?: remember { mutableStateOf("") }

            val latLng by savedStateHandle
                ?.getStateFlow<LatLng?>("profile_latlng", null)
                ?.collectAsState() ?: remember { mutableStateOf<LatLng?>(null) }

            EditProfileScreen(
                onBackPressed = { navController.navigateUp() },
                onOpenMapPicker = { navController.navigate("map_picker_profile") },
                selectedAddress = address,
                selectedLatLng = latLng
            )
        }

        composable("map_picker_profile") {
            MapPickerScreen(
                onLocationSelected = { address, latLng ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("profile_address", address)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("profile_latlng", latLng)
                    navController.navigateUp()
                },
                onBackPressed = { navController.navigateUp() }
            )
        }

        composable("post_job") {
            val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

            val address by savedStateHandle
                ?.getStateFlow("selected_address", "")
                ?.collectAsState() ?: remember { mutableStateOf("") }

            val latLng by savedStateHandle
                ?.getStateFlow<LatLng?>("selected_latlng", null)
                ?.collectAsState() ?: remember { mutableStateOf<LatLng?>(null) }

            PostJobScreen(
                selectedAddress = address,
                selectedLatLng = latLng,
                onOpenMapPicker = { navController.navigate("map_picker") },
                onBackPressed = { navController.navigateUp() },
                onPostJob = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("should_refresh_jobs", true)
                    navController.navigateUp()
                }
            )
        }

        composable("map_picker") {
            MapPickerScreen(
                onLocationSelected = { address, latLng ->
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set("selected_address", address)
                        set("selected_latlng", latLng)
                    }
                    navController.navigateUp()
                },
                onBackPressed = { navController.navigateUp() }
            )
        }
    }
}

/* ------------------------ MAIN SCREEN ------------------------ */

@Composable
fun MainScreen(
    initialUserType: String?,
    jobViewModel: JobViewModel,
    refreshTrigger: Int,
    onNavigateToPostJob: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    val isOwner = initialUserType == "owner" ||
            authState.currentUserData?.userType == "owner"

    var currentView by remember { mutableStateOf(if (isOwner) "owner" else "student") }
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            if (currentView == "student") {
                NavigationBar {
                    listOf("Home", "Saved", "Search", "Profile").forEachIndexed { i, label ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    listOf(
                                        Icons.Default.Home,
                                        Icons.Default.Bookmark,
                                        Icons.Default.Search,
                                        Icons.Default.Person
                                    )[i],
                                    contentDescription = label
                                )
                            },
                            label = { Text(label) },
                            selected = selectedTab == i,
                            onClick = { selectedTab = i }
                        )
                    }
                }
            } else {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Work, null) },
                        label = { Text("My Jobs") },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AddCircle, null) },
                        label = { Text("Post Job") },
                        selected = false,
                        onClick = onNavigateToPostJob
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text("Profile") },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (currentView) {
                "student" -> when (selectedTab) {
                    0 -> HomeScreen(
                        onSwitchView = { currentView = "owner"; selectedTab = 0 },
                        viewModel = jobViewModel
                    )
                    1 -> SavedScreen(
                        onSwitchView = { currentView = "owner"; selectedTab = 0 },
                        viewModel = jobViewModel
                    )
                    2 -> SearchScreen(
                        onBackClick = { selectedTab = 0 },
                        onSwitchView = { currentView = "owner"; selectedTab = 0 },
                        viewModel = jobViewModel
                    )
                    3 -> ProfileScreen(
                        isOwnerView = false,
                        onSwitchView = { currentView = "owner"; selectedTab = 0 },
                        onNavigateToEditProfile = onNavigateToEditProfile,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToSecurity = onNavigateToSecurity,
                        onNavigateToAbout = onNavigateToAbout,
                        onLogout = onLogout
                    )
                }

                "owner" -> when (selectedTab) {
                    0 -> MyJobsScreen(
                        viewModel = jobViewModel,
                        refreshTrigger = refreshTrigger,
                        onSwitchView = { currentView = "student"; selectedTab = 0 }
                    )
                    1 -> ProfileScreen(
                        isOwnerView = true,
                        onSwitchView = { currentView = "student"; selectedTab = 0 },
                        onNavigateToEditProfile = onNavigateToEditProfile,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToSecurity = onNavigateToSecurity,
                        onNavigateToAbout = onNavigateToAbout,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}