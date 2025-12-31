package com.example.jobnest
import com.example.jobnest.viewmodel.LocationViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.jobnest.auth.SignUpScreen
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

        if (!hasLocationPermission()) {
            requestLocationPermission()
        }

        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        enableEdgeToEdge()
        setContent {
            JobnestTheme {
                JobNestApp(
                    modifier = Modifier.fillMaxSize(),
                    onGoogleSignInClicked = {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                )
            }
        }
    }

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

@Composable
fun JobNestApp(
    modifier: Modifier = Modifier,
    onGoogleSignInClicked: () -> Unit
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val jobViewModel: JobViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel()
    var jobPostRefreshTrigger by remember { mutableStateOf(0) }

    NavHost(navController, startDestination = "splash", modifier = modifier) {

        composable("splash") {
            val authState by authViewModel.authState.collectAsState()
            SplashScreen(
                onSplashComplete = {
                    val destination = if (authState.currentUserData != null) "main" else "login"
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onSignUpClicked = { navController.navigate("signup") },
                onStudentLoginSuccess = { navController.navigate("main?userType=student") { popUpTo("login") { inclusive = true } } },
                onOwnerLoginSuccess = { navController.navigate("main?userType=owner") { popUpTo("login") { inclusive = true } } },
                onForgotPasswordClicked = { navController.navigate("forgot_password") },
                onGoogleSignInClicked = onGoogleSignInClicked,
                viewModel = authViewModel
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignInClicked = { navController.navigate("login") },
                onStudentSignUpSuccess = { navController.navigate("main?userType=student") { popUpTo(0) { inclusive = true } } },
                onJobCoordinatorSignUpSuccess = { navController.navigate("main?userType=owner") { popUpTo(0) { inclusive = true } } },
                viewModel = authViewModel
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onSendClicked = { navController.navigateUp() },
                onBackToLoginClicked = { navController.navigateUp() }
            )
        }

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
                    navController.navigate("login") { popUpTo("main") { inclusive = true } }
                }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                onBackPressed = { navController.navigateUp() },
                onOpenMapPicker = { navController.navigate("map_picker_profile") },
                selectedAddress = "",
                selectedLatLng = null
            )
        }

        composable("map_picker_profile") {
            MapPickerScreen(
                onLocationSelected = { _, _ -> navController.navigateUp() },
                onBackPressed = { navController.navigateUp() }
            )
        }

        composable("post_job") {
            val locationState by locationViewModel.locationState.collectAsState()

            val savedStateHandle = it.savedStateHandle
            val selectedAddress = savedStateHandle.get<String>("selected_address") ?: ""
            val selectedLat = savedStateHandle.get<Double>("selected_lat")
            val selectedLng = savedStateHandle.get<Double>("selected_lng")
            val selectedLatLng = if (selectedLat != null && selectedLng != null) {
                LatLng(selectedLat, selectedLng)
            } else null

            PostJobScreen(
                selectedAddress = selectedAddress,
                selectedLatLng = selectedLatLng,
                onOpenMapPicker = { navController.navigate("map_picker") },
                onBackPressed = { navController.navigateUp() },
                onJobPostedSuccessfully = {
                    locationViewModel.clearLocation()
                    // Let the MainScreen know it should refresh the job list
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
                        set("selected_lat", latLng.latitude)
                        set("selected_lng", latLng.longitude)
                    }
                    locationViewModel.setLocation(address, latLng)

                    navController.navigateUp()
                },
                onBackPressed = { navController.navigateUp() }
            )
        }
    }
}

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
    val isOwner = initialUserType == "owner" || authState.currentUserData?.userType == "owner"
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                if (isOwner) {
                    val ownerItems = listOf("My Jobs", "Profile")
                    val ownerIcons = listOf(Icons.Default.Work, Icons.Default.Person)
                    ownerItems.forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(ownerIcons[index], contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                } else {
                    val studentItems = listOf("Home", "Saved", "Search", "Profile")
                    val studentIcons = listOf(Icons.Default.Home, Icons.Default.Bookmark, Icons.Default.Search, Icons.Default.Person)
                    studentItems.forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(studentIcons[index], contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (isOwner) {
                FloatingActionButton(onClick = onNavigateToPostJob) {
                    Icon(Icons.Default.Add, contentDescription = "Post Job")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isOwner) {
                when (selectedTab) {
                    0 -> MyJobsScreen(onSwitchView = {})
                    1 -> ProfileScreen(onLogout = onLogout, onNavigateToEditProfile = onNavigateToEditProfile, isOwnerView = true, onSwitchView = {})
                }
            } else { // Student
                when (selectedTab) {
                    0 -> HomeScreen(onSwitchView = {})
                    1 -> SavedScreen(onSwitchView = {})
                    2 -> SearchScreen(onBackClick = {}, onSwitchView = {})
                    3 -> ProfileScreen(onLogout = onLogout, onNavigateToEditProfile = onNavigateToEditProfile, isOwnerView = false, onSwitchView = {})
                }
            }
        }
    }
}
